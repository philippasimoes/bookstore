package com.bookstore.order_service.model.entity;

import com.bookstore.order_service.model.dto.enums.OrderStatus;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import jakarta.persistence.*;
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
@SoftDelete
@Entity
@Table(name = "order", schema = "orderservice")
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
public class Order extends BaseEntity {

    @Column(name = "customer_id", updatable = false)
    private int customerId;

    @Column(name = "shipment_date")
    private Timestamp shipmentDate;

    @Column(name = "delivered_date")
    private Timestamp deliveredDate;

    @Column(name = "total_price_items")
    private double totalPriceItems;

    @Column
    private double tax;

    @Column(name = "total_price_order")
    private double totalPriceOrder;

    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    @Column
    private boolean editable;

    @OneToMany
    @JoinColumn(name = "order_id")
    private List<Item> items;

    @Column(name = "total_weight")
    private double totalWeight;
}
