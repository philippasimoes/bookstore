package com.example.notification.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import jakarta.persistence.*;
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
    private int booId;

    @Column(name = "customer_email")
    private String customerEmail;

    @Column(name = "creation_date")
    private Timestamp creationDate;

    @Column(name = "update_date")
    private Timestamp updateDate;

    @Column
    private String message;

}
