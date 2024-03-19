package com.bookstore.order_service.model.dto.valueobject;

import com.bookstore.order_service.model.dto.enums.Format;

public record BookDto(
    int id, String title, double price, double promotionalPrice, double weight, Format format) {}
