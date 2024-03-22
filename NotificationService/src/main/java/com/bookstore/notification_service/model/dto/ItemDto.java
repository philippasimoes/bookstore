package com.bookstore.notification_service.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ItemDto {

  private int id;
  private OrderDto order;
  private int bookId;
  private int quantity;
  private double unitPrice;
  private double unitWeight;
}
