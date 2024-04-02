package com.bookstore.stock_service.exception;

/**
 * Custom exception to be used when the stock entry is not present in database.
 *
 * @author Filipa Simões
 */
public class StockNotFoundException extends RuntimeException {

  public StockNotFoundException() {
    super();
  }

  public StockNotFoundException(String message, Throwable cause) {
    super(message, cause);
  }
}
