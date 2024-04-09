package com.bookstore.payment_service.service;

import com.bookstore.payment_service.infrastructure.message.publisher.RabbitMQProducer;
import com.bookstore.payment_service.model.dto.CreditCardPaymentDto;
import com.bookstore.payment_service.model.dto.enums.PaymentStatus;
import com.bookstore.payment_service.model.entity.BasePayment;
import com.bookstore.payment_service.repository.BasePaymentRepository;
import com.bookstore.payment_service.utils.PaymentUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
public class CreditCardPaymentProcessor implements PaymentProcessor {
  private static final Logger LOGGER = LogManager.getLogger(CreditCardPaymentProcessor.class);
  @Autowired BasePaymentRepository basePaymentRepository;
  @Autowired RabbitMQProducer producer;
  @Autowired ObjectMapper objectMapper;

  @Value("${rabbitmq.queue.event.paid.name}")
  private String eventPaidQueue;

  // dummy method, just for testing
  @Override
  public String createPayment(Map<String, Object> request) {

    CreditCardPaymentDto creditCardPayment =
        objectMapper.convertValue(request, CreditCardPaymentDto.class);

    BasePayment basePayment = PaymentUtils.createBasePayment(creditCardPayment);
    basePayment.setPaymentStatus(PaymentStatus.COMPLETE);

    Map<String, Object> map = new HashMap<>();
    map.put("externalPaymentId", UUID.randomUUID());

    basePayment.setPaymentDetails(map);
    basePaymentRepository.save(basePayment);

    try {
      producer.sendMessage(
          eventPaidQueue, PaymentUtils.buildMessage("orderId", creditCardPayment.getOrderId()));
    } catch (JsonProcessingException e) {
      LOGGER.error("Error building message", e);
    }
    return PaymentStatus.COMPLETE.toString();
  }
}
