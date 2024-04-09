package com.bookstore.order_service.model.dto;

import lombok.Data;

@Data
public class ItemDto {

  private int id;
  private OrderDto order;
  private int bookId;
  private int quantity;
  private double unitPrice;
  private double unitWeight;
}
