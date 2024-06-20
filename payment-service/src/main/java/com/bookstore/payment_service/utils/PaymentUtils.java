package com.bookstore.payment_service.utils;

import com.bookstore.payment_service.model.dto.CreditCardPaymentDto;
import com.bookstore.payment_service.model.dto.BasePaymentDto;
import com.bookstore.payment_service.model.dto.PayPalPaymentDto;
import com.bookstore.payment_service.model.dto.StripePaymentDto;
import com.bookstore.payment_service.model.dto.enums.PaymentMethod;
import com.bookstore.payment_service.model.entity.BasePayment;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.data.util.Pair;

import java.util.HashMap;
import java.util.Map;

public final class PaymentUtils {

    private PaymentUtils() {
    }

    static ObjectMapper objectMapper = new ObjectMapper();

    public static String buildMessage(String key, int id) throws JsonProcessingException {
        Map<String, Integer> mapToSend = new HashMap<>();
        mapToSend.put(key, id);

        return objectMapper.writeValueAsString(mapToSend);
    }

    public static BasePayment createBasePayment(BasePaymentDto basePaymentDto) {

        BasePayment payment = new BasePayment();
        payment.setOrderId(basePaymentDto.getOrderId());
        payment.setCustomerId(basePaymentDto.getCustomerId());
        payment.setAmount(basePaymentDto.getAmount());

        switch (basePaymentDto) {
            case StripePaymentDto stripePayment -> payment.setPaymentMethod(PaymentMethod.STRIPE);
            case PayPalPaymentDto payPalPayment -> basePaymentDto.setPaymentMethod(PaymentMethod.PAYPAL);
            case CreditCardPaymentDto creditCardPayment -> basePaymentDto.setPaymentMethod(PaymentMethod.CREDIT_CARD);
            default -> {
            }
        }

        return payment;
    }
}
