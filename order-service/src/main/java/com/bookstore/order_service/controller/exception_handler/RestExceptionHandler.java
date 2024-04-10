package com.bookstore.order_service.controller.exception_handler;

import java.sql.SQLException;
import java.util.Date;

import com.bookstore.order_service.exception.ResourceNotFoundException;
import com.bookstore.order_service.utils.ApiError;
import com.fasterxml.jackson.core.JsonProcessingException;
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

  @ExceptionHandler(value = JsonProcessingException.class)
  public ResponseEntity<ApiError> handleJsonProcessingException() {

    ApiError error =
        new ApiError(
            HttpStatus.INTERNAL_SERVER_ERROR.value(),
            "Error processing json message.",
            new Date(System.currentTimeMillis()));

    return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
  }
}
