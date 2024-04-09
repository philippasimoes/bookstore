package com.bookstore.return_service.model.dto;

import lombok.Data;

@Data
public class ReturnItemDto {

  private int id;
  private ReturnDto returnEntity;
  private int bookId;
  private int quantity;
  private double orderUnitPrice;
  private double unitWeight;
}
