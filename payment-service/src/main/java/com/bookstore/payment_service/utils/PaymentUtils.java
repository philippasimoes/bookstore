package com.bookstore.payment_service.utils;

import com.bookstore.payment_service.model.dto.CreditCardPayment;
import com.bookstore.payment_service.model.dto.GenericPayment;
import com.bookstore.payment_service.model.dto.PayPalPayment;
import com.bookstore.payment_service.model.dto.StripePayment;
import com.bookstore.payment_service.model.dto.enums.PaymentMethod;
import com.bookstore.payment_service.model.entity.BasePayment;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.data.util.Pair;

public final class PaymentUtils {

  private PaymentUtils() {}

  static ObjectMapper objectMapper = new ObjectMapper();

  public static String buildMessage(int orderId) throws JsonProcessingException {
    Pair<String, Integer> pair = Pair.of("orderId", orderId);
    return objectMapper.writeValueAsString(pair);
  }

  public static BasePayment createBasePayment(GenericPayment genericPayment) {

    BasePayment payment = new BasePayment();
    payment.setOrderId(genericPayment.getOrderId());
    payment.setCustomerId(genericPayment.getCustomerId());
    payment.setPaymentAmount(genericPayment.getAmount());

    switch (genericPayment) {
      case StripePayment stripePayment -> payment.setPaymentMethod(PaymentMethod.STRIPE);
      case PayPalPayment payPalPayment -> genericPayment.setPaymentMethod(PaymentMethod.PAYPAL);
      case CreditCardPayment creditCardPayment ->
          genericPayment.setPaymentMethod(PaymentMethod.CREDIT_CARD);
      default -> {}
    }

    return payment;
  }
}
