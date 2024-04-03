package com.bookstore.payment_service.model.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class CreditCardPayment extends GenericPayment {

  String creditCardNumber;
}
