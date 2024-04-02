package com.bookstore.payment_service.service;

import com.bookstore.payment_service.infrastructure.message.publisher.RabbitMQProducer;
import com.bookstore.payment_service.model.dto.enums.PaymentMethod;
import com.bookstore.payment_service.model.dto.enums.PaymentStatus;
import com.bookstore.payment_service.model.entity.BasePayment;
import com.bookstore.payment_service.repository.PaymentRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;

@Service
public class CreditCardPaymentProcessor {
  private static final Logger LOGGER = LogManager.getLogger(CreditCardPaymentProcessor.class);
  @Autowired PaymentRepository paymentRepository;

  @Autowired RabbitMQProducer producer;
  @Autowired ObjectMapper objectMapper;

  @Value("${rabbitmq.queue.event.paid.name}")
  private String eventPaidQueue;

  // dummy method, just for testing
  public String createPayment(int customerId, int orderId, double price) {

    BasePayment basePayment = new BasePayment();

    basePayment.setOrderId(orderId);
    basePayment.setCustomerId(customerId);
    basePayment.setPaymentAmount(price);
    basePayment.setPaymentMethod(PaymentMethod.CREDIT_CARD);
    basePayment.setPaymentStatus(PaymentStatus.COMPLETE);

    paymentRepository.save(basePayment);

    try {
      producer.sendMessage(eventPaidQueue, buildMessage(orderId));
    } catch (JsonProcessingException e) {
      LOGGER.error("Error building message", e);
    }
    return PaymentStatus.COMPLETE.toString();
  }

  private String buildMessage(int orderId) throws JsonProcessingException {
    Pair<String, Integer> pair = Pair.of("orderId", orderId);
    return objectMapper.writeValueAsString(pair);
  }
}
