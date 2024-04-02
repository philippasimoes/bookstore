package com.bookstore.order_service.model.entity;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
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
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
public class Item extends BaseEntity {

  @ManyToOne
  @JoinColumn(name = "order_id", insertable = false, updatable = false)
  private Order order;

  @Column(name = "book_id", updatable = false)
  private int bookId;

  @Column private int quantity;

  @Column(name = "unit_price", updatable = false)
  private double unitPrice;

  @Column(name = "unit_weight")
  private double unitWeight;
}
