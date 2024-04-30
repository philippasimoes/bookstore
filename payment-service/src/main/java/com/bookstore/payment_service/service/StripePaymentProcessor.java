package com.bookstore.payment_service.service;

import com.bookstore.payment_service.infrastructure.message.publisher.RabbitMQProducer;
import com.bookstore.payment_service.model.dto.StripePaymentDto;
import com.bookstore.payment_service.model.dto.StripeToken;
import com.bookstore.payment_service.model.dto.enums.PaymentMethod;
import com.bookstore.payment_service.model.dto.enums.PaymentStatus;
import com.bookstore.payment_service.model.entity.BasePayment;
import com.bookstore.payment_service.repository.BasePaymentRepository;
import com.bookstore.payment_service.utils.PaymentUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.Charge;
import com.stripe.model.Refund;
import com.stripe.model.Token;
import com.stripe.param.RefundCreateParams;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class StripePaymentProcessor implements PaymentProcessor {

  private static final Logger LOGGER = LogManager.getLogger(StripePaymentProcessor.class);
  private static final String AMT = "amount";
  private static final String RETURN_ID = "returnId";
  private static final String EXT_PAYMENT_ID = "externalPaymentId";

  @Autowired
  BasePaymentRepository basePaymentRepository;
  @Autowired RabbitMQProducer producer;
  @Autowired ObjectMapper mapper;

  @Value("${rabbitmq.queue.event.paid.name}") // order service
  private String eventPaidQueue;

  @Value("${rabbitmq.queue.event.refund.name}") // return service
  private String eventRefundQueue;

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
        LOGGER.log(Level.INFO, "TOKEN: {}", token.getId());
      }
      return stripeToken;
    } catch (StripeException e) {
      LOGGER.log(Level.ERROR, "StripeService (createCardToken)", e);
      throw new RuntimeException(e.getMessage());
    }
  }

  /**
   * Perform a Stripe payment and create the payment object in the database.
   *
   * @param request a map with relevant information such as order identifier, customer identifier
   *     and the stripe token previously obtained.
   * @return a Stripe payment object.
   * @throws StripeException if something fails when doing the Stripe operations.
   */
  @Override
  public Object createPayment(Map<String, Object> request) throws StripeException {

    StripePaymentDto stripePayment = mapper.convertValue(request, StripePaymentDto.class);

    Stripe.apiKey = stripeSecretKey;

    // Stripe operations
    stripePayment.setSuccess(false);
    Map<String, Object> chargeParams = new HashMap<>();
    chargeParams.put(AMT, (long) (stripePayment.getAmount() * 100)); // cents
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

    if (Boolean.TRUE.equals(charge.getPaid())) {
      stripePayment.setChargeId(charge.getId());
      stripePayment.setSuccess(true);
      stripePayment.setReceiptUrl(charge.getReceiptUrl());

      payment.setOperationDate(Timestamp.from(Instant.ofEpochSecond(charge.getCreated())));
      payment.setPaymentStatus(PaymentStatus.COMPLETE);
      payment.setOperationDate(Timestamp.from(Instant.now()));

      Map<String, Object> map = new HashMap<>();
      map.put(EXT_PAYMENT_ID, charge.getId());
      payment.setPaymentDetails(map);
      basePaymentRepository.save(payment);
      try {
        producer.sendMessage(
            eventPaidQueue, PaymentUtils.buildMessage("orderId", stripePayment.getOrderId()));
      } catch (JsonProcessingException e) {
        LOGGER.log(Level.ERROR, "Error building message", e);
      }
    } else {
      payment.setPaymentStatus(PaymentStatus.FAILED);
      basePaymentRepository.save(payment);
    }

    return stripePayment;
  }

  /**
   * Perform a Stripe refund and create the payment object in database.
   *
   * @param map the map containing relevant data such as customer and return identifier, and the
   *     amount to be refunded.
   * @throws StripeException if something fails when doing the Stripe operations.
   */
  public void refund(Map<String, String> map) throws StripeException {
    Stripe.apiKey = stripeSecretKey;

    RefundCreateParams params =
        RefundCreateParams.builder()
            .setCharge(map.get(EXT_PAYMENT_ID))
            .setAmount((long) (Double.parseDouble(map.get(AMT)) * 100))
            .build();

    Refund refund1 = Refund.create(params);

    BasePayment basePayment = new BasePayment();
    basePayment.setCustomerId(Integer.parseInt(map.get("customerId")));
    basePayment.setReturnId(Integer.parseInt(map.get(RETURN_ID)));
    basePayment.setAmount(Double.parseDouble(map.get(AMT)));
    basePayment.setOperationDate(Timestamp.from(Instant.now()));
    basePayment.setPaymentMethod(PaymentMethod.STRIPE);
    basePayment.setPaymentStatus(PaymentStatus.REFUNDED);

    Map<String, Object> paymentDetails = new HashMap<>();
    map.put(EXT_PAYMENT_ID, map.get(EXT_PAYMENT_ID));
    basePayment.setPaymentDetails(paymentDetails);

    basePaymentRepository.save(basePayment);
    if (refund1.getStatus().equals("succeeded")) {
      try {
        producer.sendMessage(
            eventRefundQueue,
            PaymentUtils.buildMessage(RETURN_ID, Integer.parseInt(map.get(RETURN_ID))));
      } catch (JsonProcessingException e) {
        LOGGER.log(Level.ERROR, "Error building message", e);
      }
    }
  }
}
