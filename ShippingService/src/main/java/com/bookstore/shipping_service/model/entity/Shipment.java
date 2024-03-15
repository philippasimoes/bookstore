package com.bookstore.shipping_service.model.entity;

import com.bookstore.shipping_service.model.dto.enums.ShipmentMethod;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.sql.Timestamp;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.SoftDelete;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@SoftDelete
@Table(name = "shipment", schema = "shippingservice")
public class Shipment {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id", nullable = false)
  private int id;

  @Column(name = "order_id")
  private int orderId;

  @Column private Timestamp date;

  @Column(name = "tracking_number")
  private String trackingNumber;

  @Enumerated(EnumType.STRING)
  private ShipmentMethod shipmentMethod;
}
