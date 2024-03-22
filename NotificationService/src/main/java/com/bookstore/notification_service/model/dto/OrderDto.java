package com.bookstore.notification_service.model.dto;

import com.bookstore.notification_service.model.dto.enums.OrderStatus;
import java.sql.Timestamp;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrderDto {

  private int id;
  private int customerId;
  private Timestamp shipmentDate;
  private double totalPriceItems;
  private double tax;
  private double totalPriceOrder;
  private double totalWeight;
  private OrderStatus status;
  private List<ItemDto> items;
}
