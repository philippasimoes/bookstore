package com.bookstore.payment_service.model.dto;

import lombok.Data;

@Data
public class StripeToken {
  String cardNumber;
  String expMonth;
  String expYear;
  String cvc;
  String token;
  String username;
  boolean success;
}
