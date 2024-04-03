package com.bookstore.payment_service.model.dto;


import com.bookstore.payment_service.model.dto.enums.PaymentMethod;
import lombok.Data;

@Data
public class GenericPayment {
    int customerId;
    int orderId;
    double amount;
    String currency;
    PaymentMethod paymentMethod;
}
