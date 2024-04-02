package com.bookstore.order_service.model.dto.enums;

public enum OrderStatus {
    OPEN, //ongoing order
    READY_TO_PAY,
    PAID, // order paid
    SHIPPED, // order shipped
    DELIVERED, // order delivered
    CANCELLED //order cancelled
}
