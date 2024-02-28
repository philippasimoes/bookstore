package com.bookstore.notification_service.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Timestamp;


/**
 * The Notification data transfer object.
 *
 * @author Filipa Simões
 */
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class NotificationDto {
    private int id;
    private int bookId;
    private String customerEmail;
    private Timestamp creationDate;
    private Timestamp updateDate;
    private String message;
    private boolean sent;
}
