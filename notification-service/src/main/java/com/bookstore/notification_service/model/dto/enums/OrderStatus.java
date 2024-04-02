package com.bookstore.notification_service.model.dto.enums;

public enum OrderStatus {
    OPEN, //ongoing order
    READY_TO_PAY,
    READY_TO_SHIP, //payment done
    SHIPPED, // order shipped
    DELIVERED, // order delivered
    CANCELLED //order cancelled
}
