package com.bookstore.return_service.model.dto;

import com.bookstore.return_service.model.dto.enums.ReturnReason;
import com.bookstore.return_service.model.dto.enums.ReturnStatus;
import java.sql.Timestamp;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ReturnDto {

  private int id;
  private int orderId;
  private List<Integer> itemIdList;
  private ReturnReason returnReason;
  private Timestamp date;
  private ReturnStatus returnStatus;
}
