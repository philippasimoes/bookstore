package com.bookstore.payment_service.service;

import com.bookstore.payment_service.infrastructure.message.publisher.RabbitMQProducer;
import com.bookstore.payment_service.model.dto.CreditCardPaymentDto;
import com.bookstore.payment_service.model.dto.enums.PaymentStatus;
import com.bookstore.payment_service.model.entity.BasePayment;
import com.bookstore.payment_service.repository.BasePaymentRepository;
import com.bookstore.payment_service.utils.PaymentUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class CreditCardPaymentProcessor implements PaymentProcessor {

  private static final Logger LOGGER = LogManager.getLogger(CreditCardPaymentProcessor.class);

  private final BasePaymentRepository basePaymentRepository;
  private final RabbitMQProducer producer;
  private final ObjectMapper objectMapper;

  @Value("${rabbitmq.queue.event.paid.name}")
  private String eventPaidQueue;

  public CreditCardPaymentProcessor(
      BasePaymentRepository basePaymentRepository,
      RabbitMQProducer producer,
      ObjectMapper objectMapper) {

    this.basePaymentRepository = basePaymentRepository;
    this.producer = producer;
    this.objectMapper = objectMapper;
  }

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
      LOGGER.log(Level.ERROR, "Error building message", e);
    }
    return PaymentStatus.COMPLETE.toString();
  }
}
