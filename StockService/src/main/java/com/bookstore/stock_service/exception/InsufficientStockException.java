package com.bookstore.stock_service.exception;

/**
 * Custom exception to be used when the available units are not enough to perform the operation.
 *
 * @author Filipa Simões
 */
public class InsufficientStockException extends RuntimeException {

  public InsufficientStockException() {
    super();
  }

  public InsufficientStockException(String message, Throwable cause) {
    super(message, cause);
  }
}
