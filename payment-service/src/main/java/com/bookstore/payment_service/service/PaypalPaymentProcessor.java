package com.bookstore.payment_service.service;

import com.bookstore.payment_service.exception.ResourceNotFoundException;
import com.bookstore.payment_service.infrastructure.message.publisher.RabbitMQProducer;
import com.bookstore.payment_service.model.dto.PayPalPayment;
import com.bookstore.payment_service.model.dto.enums.PaymentStatus;
import com.bookstore.payment_service.model.entity.BasePayment;
import com.bookstore.payment_service.repository.BasePaymentRepository;
import com.bookstore.payment_service.utils.PaymentUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
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
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class PaypalPaymentProcessor {

  private static final Logger LOGGER = LogManager.getLogger(PaypalPaymentProcessor.class);

  @Autowired
  RabbitMQProducer producer;

  @Value("${rabbitmq.queue.event.paid.name}")
  private String eventPaidQueue;

  @Autowired BasePaymentRepository basePaymentRepository;
  @Autowired APIContext apiContext;


  public Payment createPayment(PayPalPayment paymentRequest) throws PayPalRESTException {
    // PayPal operations
    Amount amount = new Amount();
    amount.setCurrency(paymentRequest.getCurrency());
    amount.setTotal(
        String.format(
            Locale.forLanguageTag(paymentRequest.getCurrency()),
            "%.2f",
            paymentRequest.getAmount()));

    Transaction transaction = new Transaction();
    transaction.setDescription(paymentRequest.getDescription());
    transaction.setAmount(amount);

    List<Transaction> transactions = new ArrayList<>();
    transactions.add(transaction);

    Payer payer = new Payer();
    payer.setPaymentMethod(paymentRequest.getMethod());

    Payment payPalPayment = new Payment();
    payPalPayment.setIntent(paymentRequest.getIntent());
    payPalPayment.setPayer(payer);
    payPalPayment.setTransactions(transactions);

    RedirectUrls redirectUrls = new RedirectUrls();
    redirectUrls.setCancelUrl(paymentRequest.getCancelUrl());
    redirectUrls.setReturnUrl(paymentRequest.getSuccessUrl());

    payPalPayment.setRedirectUrls(redirectUrls);

    Payment createdPayPalPayment = payPalPayment.create(apiContext);

    // database operations
    BasePayment basePayment = PaymentUtils.createBasePayment(paymentRequest);
    
    Map<String, Object> paypalPaymentDetails = new HashMap<>();
    paypalPaymentDetails.put("paymentId", createdPayPalPayment.getId());
    basePayment.setPaymentDetails(paypalPaymentDetails);
    
    basePaymentRepository.save(basePayment);

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
        try {
          producer.sendMessage(eventPaidQueue, PaymentUtils.buildMessage(basePayment.get().getOrderId()));
        } catch (JsonProcessingException e) {
          LOGGER.error("Error building message", e);
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
