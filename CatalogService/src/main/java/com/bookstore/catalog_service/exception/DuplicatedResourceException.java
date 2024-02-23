package com.bookstore.catalog_service.exception;

public class DuplicatedResourceException extends RuntimeException {

    public DuplicatedResourceException(String message) {
        super(message);
    }

    public DuplicatedResourceException(String message, Throwable cause) {
        super(message, cause);
    }
}
