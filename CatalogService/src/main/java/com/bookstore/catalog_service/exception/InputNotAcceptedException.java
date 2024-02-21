package com.bookstore.catalog_service.exception;

public class InputNotAcceptedException extends RuntimeException{

    public InputNotAcceptedException(String message) {
        super(message);
    }

    public InputNotAcceptedException(String message, Throwable cause) {
        super(message, cause);
    }
}
