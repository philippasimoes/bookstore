package com.bookstore.notification_service.model.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.sql.Timestamp;

@NoArgsConstructor
@Setter
@Getter
@Entity
@Table(name = "notification")
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private int id;

    @Column(name = "book_id")
    private int bookId;

    @Column(name = "customer_email")
    private String customerEmail;

    @Column(name = "creation_date")
    @CreationTimestamp
    private Timestamp creationDate;

    @Column(name = "update_date")
    @UpdateTimestamp
    private Timestamp updateDate;

    @Column
    private String message;

    @Column
    private boolean sent;

}
