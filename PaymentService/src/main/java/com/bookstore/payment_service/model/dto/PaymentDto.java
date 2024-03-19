package com.bookstore.payment_service.model.dto;

import com.bookstore.payment_service.model.dto.enums.PaymentMethod;
import com.bookstore.payment_service.model.dto.enums.PaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Timestamp;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PaymentDto {
    private int id;
    private int orderId;
    private int customerId;
    private PaymentMethod paymentMethod;
    private PaymentStatus paymentStatus;
    private Timestamp paymentDate;
    private double paymentAmount;
}
