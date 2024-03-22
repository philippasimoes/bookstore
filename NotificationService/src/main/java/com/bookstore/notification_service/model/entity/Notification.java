package com.bookstore.notification_service.model.entity;

import com.bookstore.notification_service.model.dto.enums.NotificationType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.sql.Timestamp;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

/**
 * The Notification entity.
 *
 * @author Filipa Simões
 */
@NoArgsConstructor
@Setter
@Getter
@Entity
@Table(name = "notification")
public class Notification {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private int id;

  @Column(name = "book_id")
  private int bookId;

  @Column(name = "order_id")
  private int orderId;

  @Column(name = "customer_email")
  private String customerEmail;

  @Column(name = "creation_date")
  @CreationTimestamp
  private Timestamp creationDate;

  @Column(name = "update_date")
  @UpdateTimestamp
  private Timestamp updateDate;

  @Column private String message;

  @Column private boolean sent;

  @Enumerated(EnumType.STRING)
  private NotificationType notificationType;
}
