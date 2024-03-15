package com.bookstore.order_service.model.dto;

import com.bookstore.order_service.model.dto.enums.OrderStatus;
import com.bookstore.order_service.model.entity.OrderItem;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Timestamp;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrderDto {

  private int id;
  private int customerId;
  private int publisherId;
  private Timestamp shipmentDate;
  private double totalPrice;
  private OrderStatus status;
  private List<OrderItem> orderItemList;
}
