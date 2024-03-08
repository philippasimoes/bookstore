package com.bookstore.notification_service.exception;

/**
 * Custom exception to be used when the notification is not present in database.
 *
 * @author Filipa Simões
 */
public class NotificationNotFoundException extends RuntimeException {

    public NotificationNotFoundException() {
        super();
    }

    public NotificationNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

}
