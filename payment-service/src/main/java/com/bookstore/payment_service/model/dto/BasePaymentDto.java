package com.bookstore.payment_service.model.dto;

import com.bookstore.payment_service.model.dto.enums.PaymentMethod;
import lombok.Data;

@Data
public class BasePaymentDto {
  private int customerId;
  private int orderId;
  private int returnId;
  private double amount;
  private String currency;
  private PaymentMethod paymentMethod;
}
