package com.bookstore.stock_service.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import jakarta.persistence.*;
import java.sql.Timestamp;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "stock")
public class Stock {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private int id;

    @Column(name="book_id")
    private int bookId;

    @Column(name = "available_stock")
    private int availableStock;

    @Column(name = "creation_date")
    private Timestamp creationDate;

    @Column(name= "update_date")
    private Timestamp updateDate;

}
