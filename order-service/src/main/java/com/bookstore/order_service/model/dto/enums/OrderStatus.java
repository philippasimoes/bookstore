package com.bookstore.order_service.model.dto.enums;

public enum OrderStatus {
    OPEN, //ongoing order
    READY_TO_PAY, // all items are added to the order and the taxes are defined
    PAID, // order paid
    SHIPPED, // order shipped
    DELIVERED, // order delivered
    CANCELLED //order cancelled
}
