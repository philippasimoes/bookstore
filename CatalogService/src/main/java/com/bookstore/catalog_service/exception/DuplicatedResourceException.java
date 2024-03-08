package com.bookstore.catalog_service.exception;

/**
 * Custom exception to be used when the resource is already in database.
 *
 * @author Filipa Simões
 */
public class DuplicatedResourceException extends RuntimeException {

  public DuplicatedResourceException(String message) {
    super(message);
  }

  public DuplicatedResourceException(String message, Throwable cause) {
    super(message, cause);
  }
}
