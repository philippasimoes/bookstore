package com.bookstore.payment_service.controller;

import com.bookstore.payment_service.model.dto.OrderData;
import com.bookstore.payment_service.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/payment")
public class PaymentController {

  @Autowired PaymentService paymentService;

  @PostMapping
  public ResponseEntity<Integer> createBasicPaymentEntry(@RequestBody OrderData request) {

    paymentService.createBasicPaymentEntry(
        Integer.parseInt(request.orderId()),
        Integer.parseInt(request.customerId()),
        Double.parseDouble(request.price()));

    return ResponseEntity.ok().build();
  }
}
