package com.bookstore.catalog_service.controller.exception_handler;

import com.bookstore.catalog_service.exception.DuplicatedResourceException;
import com.bookstore.catalog_service.exception.InputNotAcceptedException;
import com.bookstore.catalog_service.exception.ResourceNotFoundException;
import com.bookstore.catalog_service.utils.ApiError;
import java.sql.SQLException;
import java.util.Date;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.TransactionException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;

/**
 * Class to handle rest exceptions.
 *
 * @author Filipa Simões
 */
@RestController
@ControllerAdvice
public class RestExceptionHandler {

  @ExceptionHandler(value = ResourceNotFoundException.class)
  public ResponseEntity<ApiError> handleResourceNotFoundException() {

    ApiError error =
        new ApiError(
            HttpStatus.NOT_FOUND.value(),
            "Resource Not Found.",
            new Date(System.currentTimeMillis()));
    return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
  }

  @ExceptionHandler(value = InputNotAcceptedException.class)
  public ResponseEntity<ApiError> handleInputNotAcceptedException() {

    ApiError error =
        new ApiError(
            HttpStatus.NOT_ACCEPTABLE.value(),
            "The start price should bw lower than the end price.",
            new Date(System.currentTimeMillis()));
    return new ResponseEntity<>(error, HttpStatus.NOT_ACCEPTABLE);
  }

  @ExceptionHandler(value = DuplicatedResourceException.class)
  public ResponseEntity<ApiError> handleDuplicatedResourceException() {

    ApiError error =
        new ApiError(
            HttpStatus.FOUND.value(),
            "Book with entered ISBN already exists.",
            new Date(System.currentTimeMillis()));
    return new ResponseEntity<>(error, HttpStatus.FOUND);
  }

  @ExceptionHandler(value = SQLException.class)
  public ResponseEntity<ApiError> handleSqlException() {

    ApiError error =
        new ApiError(
            HttpStatus.INTERNAL_SERVER_ERROR.value(),
            "Something went wrong with database.",
            new Date(System.currentTimeMillis()));

    return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
  }

  @ExceptionHandler(value = TransactionException.class)
  public ResponseEntity<ApiError> handleTransactionException() {

    ApiError error =
        new ApiError(
            HttpStatus.INTERNAL_SERVER_ERROR.value(),
            "Something went wrong with database.",
            new Date(System.currentTimeMillis()));

    return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
  }
}
