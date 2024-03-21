package com.bookstore.payment_service.service;

import com.bookstore.payment_service.exception.ResourceNotFoundException;
import com.bookstore.payment_service.model.dto.PaymentDto;
import com.bookstore.payment_service.model.dto.enums.PaymentMethod;
import com.bookstore.payment_service.model.entity.BasePayment;
import com.bookstore.payment_service.model.mapper.PaymentMapper;
import com.bookstore.payment_service.repository.PaymentRepository;
import com.paypal.api.payments.Amount;
import com.paypal.api.payments.Payer;
import com.paypal.api.payments.Payment;
import com.paypal.api.payments.PaymentExecution;
import com.paypal.api.payments.RedirectUrls;
import com.paypal.api.payments.Transaction;
import com.paypal.base.rest.PayPalRESTException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import com.paypal.base.rest.APIContext;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class PaymentService {

  private static final Logger LOGGER = LogManager.getLogger(PaymentService.class);
  @Autowired PaymentRepository paymentRepository;

  @Autowired PaymentMapper paymentMapper;

  @Autowired RestTemplate restTemplate;

  @Autowired private APIContext apiContext;

  @Value("${bookstore.merchantId}")
  private String merchantId;

  public void createBasicPaymentEntry(int orderId, int customerId, double price) {

    BasePayment basePayment = new BasePayment();

    basePayment.setOrderId(orderId);
    basePayment.setCustomerId(customerId);
    basePayment.setPaymentAmount(price);

    paymentRepository.save(basePayment);
  }

  public PaymentDto addPaymentMethod(
      int id, String paymentMethod, Map<String, Object> paymentDetails) {

    Optional<BasePayment> payment = paymentRepository.findById(id);

    if (payment.isPresent()) {
      payment.get().setPaymentMethod(PaymentMethod.valueOf(paymentMethod));
      payment.get().setPaymentDetails(paymentDetails);
      return paymentMapper.toDto(paymentRepository.save(payment.get()));
    } else throw new ResourceNotFoundException("Payment not found");
  }

  //TODO: paypal
 /* public Payment createPayment(
      Double total,
      String currency,
      String method,
      String intent,
      String description,
      String cancelUrl,
      String successUrl)
      throws PayPalRESTException {
    Amount amount = new Amount();
    amount.setCurrency(currency);
    total = new BigDecimal(total).setScale(2, RoundingMode.HALF_UP).doubleValue();
    amount.setTotal(String.format("%.2f", total));

    Transaction transaction = new Transaction();
    transaction.setDescription(description);
    transaction.setAmount(amount);

    List<Transaction> transactions = new ArrayList<>();
    transactions.add(transaction);

    Payer payer = new Payer();
    payer.setPaymentMethod(method.toString());

    Payment payment = new Payment();
    payment.setIntent(intent.toString());
    payment.setPayer(payer);
    payment.setTransactions(transactions);
    RedirectUrls redirectUrls = new RedirectUrls();
    redirectUrls.setCancelUrl(cancelUrl);
    redirectUrls.setReturnUrl(successUrl);
    payment.setRedirectUrls(redirectUrls);

    return payment.create(apiContext);
  }

  public Payment executePayment(String paymentId, String payerId) throws PayPalRESTException {
    Payment payment = new Payment();
    payment.setId(paymentId);
    PaymentExecution paymentExecute = new PaymentExecution();
    paymentExecute.setPayerId(payerId);
    return payment.execute(apiContext, paymentExecute);
  }*/
}
