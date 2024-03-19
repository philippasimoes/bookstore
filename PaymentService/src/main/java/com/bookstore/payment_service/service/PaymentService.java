package com.bookstore.payment_service.service;

import com.bookstore.payment_service.model.entity.Payment;
import com.bookstore.payment_service.repository.PaymentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PaymentService {

  @Autowired PaymentRepository paymentRepository;

  public void createBasicPaymentEntry(int orderId, int customerId, double price) {

    Payment payment = new Payment();

    payment.setOrderId(orderId);
    payment.setCustomerId(customerId);
    payment.setPaymentAmount(price);

    paymentRepository.save(payment);
  }
}
