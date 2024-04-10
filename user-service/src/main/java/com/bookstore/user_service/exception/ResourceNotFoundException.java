package com.bookstore.user_service.exception;

/**
 * Custom exception to be used when the resource is not present in database.
 *
 * @author Filipa Simões
 */
public class ResourceNotFoundException extends RuntimeException {

  public ResourceNotFoundException(String message) {
    super(message);
  }

  public ResourceNotFoundException(String message, Throwable cause) {
    super(message, cause);
  }
}
