package com.bookstore.catalog_service.exception;

/**
 * Custom exception to be used when the user input is not acceptable.
 *
 * @author Filipa Simões
 */
public class InputNotAcceptedException extends RuntimeException {

  public InputNotAcceptedException(String message) {
    super(message);
  }

  public InputNotAcceptedException(String message, Throwable cause) {
    super(message, cause);
  }
}
