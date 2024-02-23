package com.bookstore.stock_service.model.dto;

import lombok.*;

import java.sql.Timestamp;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class StockDto {

    private int id;
    private int bookId;
    private int availableStock;
    private Timestamp creationDate;
    private Timestamp updateDate;
}
