package com.bookstore.order_service.exception;

/**
 * Custom exception to be used when the resource is not present in database.
 *
 * @author Filipa Simões
 */
public class ResourceNotFoundException extends RuntimeException {

  public ResourceNotFoundException(String message) {
    super(message);
  }

}
