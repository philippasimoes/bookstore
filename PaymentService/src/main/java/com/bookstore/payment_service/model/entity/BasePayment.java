package com.bookstore.payment_service.model.entity;

import com.bookstore.payment_service.model.dto.enums.PaymentMethod;
import com.bookstore.payment_service.model.dto.enums.PaymentStatus;
import com.bookstore.payment_service.utils.HashMapConverter;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.SoftDelete;
import org.hibernate.annotations.UpdateTimestamp;

import java.sql.Timestamp;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SoftDelete
@Entity
@Table(name = "payment", schema = "paymentservice")
public class BasePayment {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id", nullable = false)
  private int id;

  @Column(name = "creation_date")
  @CreationTimestamp
  private Timestamp creationDate;

  @Column(name = "update_date")
  @UpdateTimestamp
  private Timestamp updateDate;

  @Column(name = "order_id", updatable = false)
  private int orderId;

  @Column(name = "customer_id", updatable = false)
  private int customerId;

  @Enumerated(EnumType.STRING)
  private PaymentMethod paymentMethod;

  @Enumerated(EnumType.STRING)
  private PaymentStatus paymentStatus;

  @Column(name = "payment_date")
  private Timestamp paymentDate;

  @Column(name = "payment_amount")
  private double paymentAmount;

  @Convert(converter = HashMapConverter.class)
  private Map<String, Object> paymentDetails;
}
