package com.bookstore.payment_service.model.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class PayPalPaymentDto extends BasePaymentDto {
  String method;
  String intent;
  String description;
  String cancelUrl;
  String successUrl;
}
