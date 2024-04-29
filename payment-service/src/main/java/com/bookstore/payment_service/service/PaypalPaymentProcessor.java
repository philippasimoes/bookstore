package com.bookstore.payment_service.service;

import com.bookstore.payment_service.exception.ResourceNotFoundException;
import com.bookstore.payment_service.infrastructure.message.publisher.RabbitMQProducer;
import com.bookstore.payment_service.model.dto.PayPalPaymentDto;
import com.bookstore.payment_service.model.dto.enums.PaymentStatus;
import com.bookstore.payment_service.model.entity.BasePayment;
import com.bookstore.payment_service.repository.BasePaymentRepository;
import com.bookstore.payment_service.utils.PaymentUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.paypal.api.payments.Amount;
import com.paypal.api.payments.Payer;
import com.paypal.api.payments.Payment;
import com.paypal.api.payments.PaymentExecution;
import com.paypal.api.payments.RedirectUrls;
import com.paypal.api.payments.Transaction;
import com.paypal.base.rest.APIContext;
import com.paypal.base.rest.PayPalRESTException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class PaypalPaymentProcessor implements PaymentProcessor {

  private static final Logger LOGGER = LogManager.getLogger(PaypalPaymentProcessor.class);
  static final String CANCEL_URL = "http://localhost:10004/payment/paypal/cancel";
  static final String SUCCESS_URL = "http://localhost:10004/payment/paypal/success";

  private final RabbitMQProducer producer;
  private final BasePaymentRepository basePaymentRepository;
  private final APIContext apiContext;
  private final ObjectMapper mapper;

  @Value("${rabbitmq.queue.event.paid.name}")
  private String eventPaidQueue;

  public PaypalPaymentProcessor(
      RabbitMQProducer producer,
      BasePaymentRepository basePaymentRepository,
      APIContext apiContext,
      ObjectMapper mapper) {

    this.producer = producer;
    this.basePaymentRepository = basePaymentRepository;
    this.apiContext = apiContext;
    this.mapper = mapper;
  }

  @Override
  public Object createPayment(Map<String, Object> request) throws PayPalRESTException {

    PayPalPaymentDto payment = mapper.convertValue(request, PayPalPaymentDto.class);
    payment.setCancelUrl(CANCEL_URL);
    payment.setSuccessUrl(SUCCESS_URL);

    // PayPal operations
    Amount amount = new Amount();
    amount.setCurrency(payment.getCurrency());
    amount.setTotal(
        String.format(Locale.forLanguageTag(payment.getCurrency()), "%.2f", payment.getAmount()));

    Transaction transaction = new Transaction();
    transaction.setDescription(payment.getDescription());
    transaction.setAmount(amount);

    List<Transaction> transactions = new ArrayList<>();
    transactions.add(transaction);

    Payer payer = new Payer();
    payer.setPaymentMethod(payment.getMethod());

    Payment payPalPayment = new Payment();
    payPalPayment.setIntent(payment.getIntent());
    payPalPayment.setPayer(payer);
    payPalPayment.setTransactions(transactions);

    RedirectUrls redirectUrls = new RedirectUrls();
    redirectUrls.setCancelUrl(payment.getCancelUrl());
    redirectUrls.setReturnUrl(payment.getSuccessUrl());

    payPalPayment.setRedirectUrls(redirectUrls);

    Payment createdPayPalPayment = payPalPayment.create(apiContext);
    LOGGER.log(Level.INFO, "PayPal payment created");

    // database operations
    BasePayment basePayment = PaymentUtils.createBasePayment(payment);

    Map<String, Object> paypalPaymentDetails = new HashMap<>();
    paypalPaymentDetails.put("externalPaymentId", createdPayPalPayment.getId());
    basePayment.setPaymentDetails(paypalPaymentDetails);

    BasePayment paymentCreated = basePaymentRepository.save(basePayment);
    LOGGER.log(Level.INFO, "Payment created with id {}.", paymentCreated.getId());
    return createdPayPalPayment;
  }

  public Payment executePayment(String paymentId, String payerId)
      throws PayPalRESTException, ResourceNotFoundException {
    Payment payPalPayment = new Payment();
    payPalPayment.setId(paymentId);
    PaymentExecution paymentExecute = new PaymentExecution();
    paymentExecute.setPayerId(payerId);

    Payment executedPayment = payPalPayment.execute(apiContext, paymentExecute);

    updateBasePayment(executedPayment);

    return executedPayment;
  }

  private void updateBasePayment(Payment executedPayment) {

    Optional<BasePayment> basePayment =
        basePaymentRepository.findByPaypalPaymentId(executedPayment.getId());

    if (executedPayment.getState().equals("approved")) {

      if (basePayment.isPresent()) {
        basePayment.get().setPaymentStatus(PaymentStatus.COMPLETE);
        basePaymentRepository.save(basePayment.get());
        LOGGER.log(Level.INFO, "Payment with id {} is completed.", basePayment.get().getId());
        try {
          producer.sendMessage(
              eventPaidQueue, PaymentUtils.buildMessage("orderId", basePayment.get().getOrderId()));
        } catch (JsonProcessingException e) {
          LOGGER.log(Level.ERROR, "Error building message", e);
        }
      }
    } else {
      if (basePayment.isPresent()) {
        basePayment.get().setPaymentStatus(PaymentStatus.FAILED);
        basePaymentRepository.save(basePayment.get());
      }
    }
  }
}
