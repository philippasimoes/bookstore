package com.bookstore.payment_service.service;

import com.bookstore.payment_service.infrastructure.message.publisher.RabbitMQProducer;
import com.bookstore.payment_service.model.dto.CreditCardPayment;
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

@Service
public class CreditCardPaymentProcessor {
  private static final Logger LOGGER = LogManager.getLogger(CreditCardPaymentProcessor.class);
  @Autowired BasePaymentRepository basePaymentRepository;

  @Autowired RabbitMQProducer producer;
  @Autowired ObjectMapper objectMapper;

  @Value("${rabbitmq.queue.event.paid.name}")
  private String eventPaidQueue;

  // dummy method, just for testing
  public String createPayment(CreditCardPayment creditCardPayment) {

    BasePayment basePayment = PaymentUtils.createBasePayment(creditCardPayment);
    basePayment.setPaymentStatus(PaymentStatus.COMPLETE);

    basePaymentRepository.save(basePayment);

    try {
      producer.sendMessage(
          eventPaidQueue, PaymentUtils.buildMessage(creditCardPayment.getOrderId()));
    } catch (JsonProcessingException e) {
      LOGGER.error("Error building message", e);
    }
    return PaymentStatus.COMPLETE.toString();
  }
}
