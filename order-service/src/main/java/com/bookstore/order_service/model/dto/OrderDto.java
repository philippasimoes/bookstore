package com.bookstore.order_service.model.dto;

import com.bookstore.order_service.model.dto.enums.OrderStatus;
import lombok.Data;

import java.sql.Timestamp;
import java.util.List;

@Data
public class OrderDto {

    private int id;
    private int customerId;
    private Timestamp shipmentDate;
    private Timestamp deliveredDate;
    private double totalPriceItems;
    private double tax;
    private double totalPriceOrder;
    private double totalWeight;
    private OrderStatus status;
    private boolean editable;
    private List<ItemDto> items;
}
