package com.bookstore.payment_service.controller;

import com.bookstore.payment_service.model.dto.OrderData;
import com.bookstore.payment_service.model.dto.PaymentDto;
import com.bookstore.payment_service.model.dto.enums.PaymentMethod;
import com.bookstore.payment_service.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

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

  @PutMapping("/{id}")
  public ResponseEntity<PaymentDto> addPaymentMethod(
      @PathVariable("id") int paymentId,
      @RequestParam(value = "payment_method") String paymentMethod,
      @RequestBody Map<String, Object> paymentDetails) {

    return ResponseEntity.ok(paymentService.addPaymentMethod(paymentId, paymentMethod, paymentDetails));
  }
}
