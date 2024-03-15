package com.bookstore.order_service.model.entity;

import com.bookstore.order_service.model.dto.enums.OrderStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.sql.Timestamp;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.SoftDelete;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SoftDelete
@Entity
@Table(name = "order", schema = "orderservice")
public class Order extends BaseEntity {

  @Column(name = "cutomer_id")
  private int customerId;

  @Column(name = "publisher_id")
  private int publisherId;

  @Column(name = "shipment_date")
  private Timestamp shipmentDate;

  @Column(name = "total_price")
  private double totalPrice;

  @Enumerated(EnumType.STRING)
  private OrderStatus status;

  @OneToMany
  @JoinColumn(name = "item_id")
  private List<OrderItem> orderItemList;
}
