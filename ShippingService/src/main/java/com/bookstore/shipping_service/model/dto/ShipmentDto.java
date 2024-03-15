package com.bookstore.shipping_service.model.dto;

import com.bookstore.shipping_service.model.dto.enums.ShipmentMethod;
import java.sql.Timestamp;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ShipmentDto {
  private int id;
  private int orderId;
  private Timestamp date;
  private String trackingNumber;
  private ShipmentMethod shipmentMethod;
}
