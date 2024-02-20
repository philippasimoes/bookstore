package com.bookstore.catalog_service.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.FOUND)
public class DuplicatedResourceException extends RuntimeException {

    public DuplicatedResourceException(String message) {
        super(message);
    }

    public DuplicatedResourceException(String message, Throwable cause) {
        super(message, cause);
    }
}
