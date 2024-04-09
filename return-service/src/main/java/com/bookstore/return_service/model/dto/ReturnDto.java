package com.bookstore.return_service.model.dto;

import com.bookstore.return_service.model.dto.enums.RefundType;
import com.bookstore.return_service.model.dto.enums.ReturnReason;
import com.bookstore.return_service.model.dto.enums.ReturnStatus;
import java.sql.Timestamp;
import java.util.List;
import lombok.Data;

@Data
public class ReturnDto {

  private int id;
  private int orderId;
  private int customerId;
  private List<ReturnItemDto> returnItems;
  private ReturnReason returnReason;
  private Timestamp date;
  private ReturnStatus returnStatus;
  private RefundType refundType;
  private String trackingCode;
  private String externalPaymentId;
}
