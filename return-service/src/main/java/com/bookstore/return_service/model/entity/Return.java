package com.bookstore.return_service.model.entity;

import com.bookstore.return_service.model.dto.enums.ReturnReason;
import com.bookstore.return_service.model.dto.enums.ReturnStatus;
import jakarta.persistence.Column;
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
import org.hibernate.annotations.SoftDelete;

import java.sql.Timestamp;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@SoftDelete
@Table(name = "return", schema = "returnservice")
public class Return {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id", nullable = false)
  private int id;

  @Column
  private int orderId;

  @Column(name="item_id_list")
  private List<Integer> itemIdList;

  @Enumerated(EnumType.STRING)
  private ReturnReason returnReason;

  @Column
  private Timestamp date;

  @Enumerated(EnumType.STRING)
  private ReturnStatus returnStatus;
}
