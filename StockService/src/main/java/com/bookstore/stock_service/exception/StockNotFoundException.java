package com.bookstore.stock_service.exception;

public class StockNotFoundException extends RuntimeException {

    public StockNotFoundException() {
        super();
    }

    public StockNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

}
