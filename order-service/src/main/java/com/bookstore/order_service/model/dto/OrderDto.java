package com.bookstore.order_service.model.dto;

import com.bookstore.order_service.model.dto.enums.OrderStatus;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import lombok.Data;

@Data
public class OrderDto {

  private int id;
  private int customerId;
  private Timestamp shipmentDate;
  private double totalPriceItems;
  private double tax;
  private double totalPriceOrder;
  private double totalWeight;
  private OrderStatus status;
  private boolean editable;
  private List<ItemDto> items;
}
