package com.bookstore.payment_service.service;

import com.bookstore.payment_service.infrastructure.message.publisher.RabbitMQProducer;
import com.bookstore.payment_service.model.dto.StripePayment;
import com.bookstore.payment_service.model.dto.StripeToken;
import com.bookstore.payment_service.model.dto.enums.PaymentStatus;
import com.bookstore.payment_service.model.entity.BasePayment;
import com.bookstore.payment_service.repository.BasePaymentRepository;
import com.bookstore.payment_service.utils.PaymentUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.Charge;
import com.stripe.model.Token;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class StripePaymentProcessor {

  private static final Logger LOGGER = LogManager.getLogger(StripePaymentProcessor.class);
  @Autowired BasePaymentRepository basePaymentRepository;
  @Autowired RabbitMQProducer producer;

  @Value("${rabbitmq.queue.event.paid.name}")
  private String eventPaidQueue;

  @Value("${STRIPE_PUBLIC_KEY}")
  private String stripePublicKey;

  @Value("${STRIPE_SECRET_KEY}")
  private String stripeSecretKey;

  /**
   * Gets a token for the used card.
   *
   * @param stripeToken an object with card data.
   * @return a StripeTokenDto with card data and token data.
   */
  public StripeToken createCardToken(StripeToken stripeToken) {
    Stripe.apiKey = stripePublicKey;

    try {
      Map<String, Object> card = new HashMap<>();
      card.put("number", stripeToken.getCardNumber());
      card.put("exp_month", Integer.parseInt(stripeToken.getExpMonth()));
      card.put("exp_year", Integer.parseInt(stripeToken.getExpYear()));
      card.put("cvc", stripeToken.getCvc());

      Map<String, Object> params = new HashMap<>();
      params.put("card", card);

      Token token = Token.create(params);
      if (token != null && token.getId() != null) {
        stripeToken.setSuccess(true);
        stripeToken.setToken(token.getId());
        LOGGER.info(String.format("TOKEN: %s", token.getId()));
      }
      return stripeToken;
    } catch (StripeException e) {
      LOGGER.error("StripeService (createCardToken)", e);
      throw new RuntimeException(e.getMessage());
    }
  }

  /**
   * Perform the payment.
   *
   * @param chargeRequest the request data with the token (obtained in {@link
   *     #createCardToken(StripeToken)}) and order data (such as amount, order identifier, internal
   *     user identifier, etc.).
   * @return A charge object with all the data from Stripe.
   */
  public StripePayment createPayment(StripePayment chargeRequest) {

    Stripe.apiKey = stripeSecretKey;

    try {
      // Stripe operations
      chargeRequest.setSuccess(false);
      Map<String, Object> chargeParams = new HashMap<>();
      chargeParams.put("amount", (int) (chargeRequest.getAmount() * 100)); // cents
      chargeParams.put("currency", chargeRequest.getCurrency());
      chargeParams.put(
          "description",
          "Payment for order with id "
              + chargeRequest.getAdditionalInfo().getOrDefault("ORDER_ID", ""));
      chargeParams.put("source", chargeRequest.getStripeToken());
      Map<String, Object> metaData = new HashMap<>();
      metaData.put("id", chargeRequest.getChargeId());
      metaData.putAll(chargeRequest.getAdditionalInfo());
      chargeParams.put("metadata", metaData);

      Charge charge = Charge.create(chargeParams);
      chargeRequest.setMessage(charge.getOutcome().getSellerMessage());

      // Database operations
      BasePayment payment = PaymentUtils.createBasePayment(chargeRequest);

      if (charge.getPaid()) {
        chargeRequest.setChargeId(charge.getId());
        chargeRequest.setSuccess(true);
        chargeRequest.setReceiptUrl(charge.getReceiptUrl());

        payment.setPaymentDate(Timestamp.from(Instant.ofEpochSecond(charge.getCreated())));
        payment.setPaymentStatus(PaymentStatus.COMPLETE);
        basePaymentRepository.save(payment);
        try {
          producer.sendMessage(
              eventPaidQueue, PaymentUtils.buildMessage(chargeRequest.getOrderId()));
        } catch (JsonProcessingException e) {
          LOGGER.error("Error building message", e);
        }
      } else {
        payment.setPaymentStatus(PaymentStatus.FAILED);
        basePaymentRepository.save(payment);
      }

      return chargeRequest;
    } catch (StripeException e) {
      LOGGER.error("StripeService (charge)", e);
      throw new RuntimeException(e.getMessage());
    }
  }
}
