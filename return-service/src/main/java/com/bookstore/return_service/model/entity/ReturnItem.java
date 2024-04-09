package com.bookstore.return_service.model.entity;

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
@Table(name = "item", schema =" returnservice")
public class ReturnItem extends BaseEntity {

  @ManyToOne
  @JoinColumn(name = "return_id", insertable = false, updatable = false)
  private Return returnEntity;

  @Column(name = "book_id", updatable = false)
  private int bookId;

  @Column private int quantity;

  @Column(name = "order_unit_price", updatable = false)
  private double orderUnitPrice;

  @Column(name = "unit_weight")
  private double unitWeight;
}
