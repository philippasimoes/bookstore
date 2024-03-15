package com.bookstore.order_service.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
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
@Table(name = "item", schema = "orderservice")
public class OrderItem extends BaseEntity {

  @ManyToOne
  @JoinColumn(name = "order_id", insertable = false, updatable = false)
  private Order order;

  @Column(name = "book_id")
  private int bookId;

  @Column private int quantity;
  @Column private double unitPrice;
}
