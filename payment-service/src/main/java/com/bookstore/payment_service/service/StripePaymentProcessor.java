package com.bookstore.payment_service.service;

import com.bookstore.payment_service.infrastructure.message.publisher.RabbitMQProducer;
import com.bookstore.payment_service.model.dto.StripePayment;
import com.bookstore.payment_service.model.dto.StripeToken;
import com.bookstore.payment_service.model.dto.enums.PaymentStatus;
import com.bookstore.payment_service.model.entity.BasePayment;
import com.bookstore.payment_service.repository.BasePaymentRepository;
import com.bookstore.payment_service.utils.PaymentUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
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
public class StripePaymentProcessor implements PaymentProcessor {

  private static final Logger LOGGER = LogManager.getLogger(StripePaymentProcessor.class);
  @Autowired BasePaymentRepository basePaymentRepository;
  @Autowired RabbitMQProducer producer;
  @Autowired ObjectMapper mapper;

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

  @Override
  public Object createPayment(Map<String, Object> request) throws StripeException {

    StripePayment stripePayment = mapper.convertValue(request, StripePayment.class);

    Stripe.apiKey = stripeSecretKey;

    // Stripe operations
    stripePayment.setSuccess(false);
    Map<String, Object> chargeParams = new HashMap<>();
    chargeParams.put("amount", (int) (stripePayment.getAmount() * 100)); // cents
    chargeParams.put("currency", stripePayment.getCurrency());
    chargeParams.put(
        "description",
        "Payment for order with id "
            + stripePayment.getAdditionalInfo().getOrDefault("ORDER_ID", ""));
    chargeParams.put("source", stripePayment.getStripeToken());
    Map<String, Object> metaData = new HashMap<>();
    metaData.put("id", stripePayment.getChargeId());
    metaData.putAll(stripePayment.getAdditionalInfo());
    chargeParams.put("metadata", metaData);

    Charge charge = Charge.create(chargeParams);
    stripePayment.setMessage(charge.getOutcome().getSellerMessage());

    // Database operations
    BasePayment payment = PaymentUtils.createBasePayment(stripePayment);

    if (charge.getPaid()) {
      stripePayment.setChargeId(charge.getId());
      stripePayment.setSuccess(true);
      stripePayment.setReceiptUrl(charge.getReceiptUrl());

      payment.setPaymentDate(Timestamp.from(Instant.ofEpochSecond(charge.getCreated())));
      payment.setPaymentStatus(PaymentStatus.COMPLETE);
      basePaymentRepository.save(payment);
      try {
        producer.sendMessage(eventPaidQueue, PaymentUtils.buildMessage(stripePayment.getOrderId()));
      } catch (JsonProcessingException e) {
        LOGGER.error("Error building message", e);
      }
    } else {
      payment.setPaymentStatus(PaymentStatus.FAILED);
      basePaymentRepository.save(payment);
    }

    return stripePayment;
  }
}
