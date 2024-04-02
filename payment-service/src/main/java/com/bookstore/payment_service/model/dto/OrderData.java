package com.bookstore.payment_service.model.dto;

import java.io.Serializable;

public record OrderData(String orderId, String customerId, String price) implements Serializable {}
