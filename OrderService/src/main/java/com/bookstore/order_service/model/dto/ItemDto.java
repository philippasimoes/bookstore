package com.bookstore.order_service.model.dto;

import com.bookstore.order_service.model.entity.Order;
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
  private Order order;
  private int bookId;
  private int quantity;
  private double unitPrice;
  private double unitWeight;
}
