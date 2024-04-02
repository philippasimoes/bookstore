package com.bookstore.payment_service.service;

import com.bookstore.payment_service.exception.ResourceNotFoundException;
import com.bookstore.payment_service.model.dto.enums.PaymentMethod;
import com.bookstore.payment_service.model.dto.enums.PaymentStatus;
import com.bookstore.payment_service.model.entity.BasePayment;
import com.bookstore.payment_service.repository.PaymentRepository;
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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PaypalPaymentProcessor {

  @Autowired PaymentRepository paymentRepository;
  @Autowired APIContext apiContext;

  public Payment createPayment(
      int customerId,
      int orderId,
      Double price,
      String currency,
      String method,
      String intent,
      String description,
      String cancelUrl,
      String successUrl)
      throws PayPalRESTException {
    // PayPal operations
    Amount amount = new Amount();
    amount.setCurrency(currency);
    amount.setTotal(String.format(Locale.forLanguageTag(currency), "%.2f", price));

    Transaction transaction = new Transaction();
    transaction.setDescription(description);
    transaction.setAmount(amount);

    List<Transaction> transactions = new ArrayList<>();
    transactions.add(transaction);

    Payer payer = new Payer();
    payer.setPaymentMethod(method);

    Payment payPalPayment = new Payment();
    payPalPayment.setIntent(intent);
    payPalPayment.setPayer(payer);
    payPalPayment.setTransactions(transactions);

    RedirectUrls redirectUrls = new RedirectUrls();
    redirectUrls.setCancelUrl(cancelUrl);
    redirectUrls.setReturnUrl(successUrl);

    payPalPayment.setRedirectUrls(redirectUrls);

    Payment createdPayPalPayment = payPalPayment.create(apiContext);

    // database operations
    BasePayment basePayment = new BasePayment();

    basePayment.setOrderId(orderId);
    basePayment.setCustomerId(customerId);
    basePayment.setPaymentAmount(price);
    basePayment.setPaymentMethod(PaymentMethod.PAYPAL);

    Map<String, Object> paypalPaymentDetails = new HashMap<>();
    paypalPaymentDetails.put("paymentId", createdPayPalPayment.getId());

    basePayment.setPaymentDetails(paypalPaymentDetails);
    paymentRepository.save(basePayment);

    return createdPayPalPayment;
  }

  public Payment executePayment(String paymentId, String payerId)
      throws PayPalRESTException, ResourceNotFoundException {
    Payment payPalPayment = new Payment();
    payPalPayment.setId(paymentId);
    PaymentExecution paymentExecute = new PaymentExecution();
    paymentExecute.setPayerId(payerId);

    Payment executedPayment = payPalPayment.execute(apiContext, paymentExecute);

    Optional<BasePayment> basePayment =
        paymentRepository.findByPaypalPaymentId(executedPayment.getId());

    if (executedPayment.getState().equals("approved")) {

      if (basePayment.isPresent()) {
        basePayment.get().setPaymentStatus(PaymentStatus.COMPLETE);
        paymentRepository.save(basePayment.get());
      }
    } else {
      if (basePayment.isPresent()) {
        basePayment.get().setPaymentStatus(PaymentStatus.FAILED);
        paymentRepository.save(basePayment.get());
      }
    }

    return executedPayment;
  }
}
