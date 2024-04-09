package com.bookstore.return_service.model.entity;

import com.bookstore.return_service.model.dto.enums.RefundType;
import com.bookstore.return_service.model.dto.enums.ReturnReason;
import com.bookstore.return_service.model.dto.enums.ReturnStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
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
@Entity
@SoftDelete
@Table(name = "return", schema = "returnservice")
public class Return extends BaseEntity {

  @Column private int orderId;

  @Column private int customerId;

  @OneToMany
  @JoinColumn(name = "return_id")
  private List<ReturnItem> returnItems;

  @Enumerated(EnumType.STRING)
  private ReturnReason returnReason;

  @Enumerated(EnumType.STRING)
  private ReturnStatus returnStatus;

  @Column(name = "amount_to_refund")
  private double amountToRefund;

  @Enumerated(EnumType.STRING)
  private RefundType refundType;

  @Column(name = "tracking_code")
  private String trackingCode;

  @Column(name = "external_payment_id")
  private String externalPaymentId;
}
