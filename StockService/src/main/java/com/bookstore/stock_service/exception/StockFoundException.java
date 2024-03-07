package com.bookstore.stock_service.exception;

public class StockFoundException extends RuntimeException {

    public StockFoundException() {
        super();
    }

    public StockFoundException(String message, Throwable cause) {
        super(message, cause);
    }

}
