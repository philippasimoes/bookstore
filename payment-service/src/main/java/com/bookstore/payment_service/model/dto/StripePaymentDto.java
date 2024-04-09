package com.bookstore.payment_service.model.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.HashMap;
import java.util.Map;

@EqualsAndHashCode(callSuper = true)
@Data
public class StripePaymentDto extends BasePaymentDto {

  private String stripeToken;
  private String username;
  private Boolean success;
  private String message;
  private String chargeId;
  private Map<String, Object> additionalInfo = new HashMap<>();
  private String receiptUrl;
}
