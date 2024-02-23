package com.bookstore.stock_service.controller.exception_handler;

import com.bookstore.stock_service.exception.InsufficientStockException;
import com.bookstore.stock_service.exception.ResourceNotFoundException;
import com.bookstore.stock_service.utils.ApiError;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.TransactionException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;

import java.sql.SQLException;
import java.util.Date;

@RestController
@ControllerAdvice
public class RestExceptionHandler {

    @ExceptionHandler(value = ResourceNotFoundException.class)
    public ResponseEntity<ApiError> handleResourceNotFoundException() {

        ApiError error = new ApiError(HttpStatus.NOT_FOUND.value(), "Resource Not Found", new Date(System.currentTimeMillis()));
        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(value = SQLException.class)
    public ResponseEntity<ApiError> handleSqlException() {

        ApiError error = new ApiError(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Something went wrong with database", new Date(System.currentTimeMillis()));

        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);

    }

    @ExceptionHandler(value = TransactionException.class)
    public ResponseEntity<ApiError> handleTransactionException() {

        ApiError error = new ApiError(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Something went wrong with database", new Date(System.currentTimeMillis()));

        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);

    }


    @ExceptionHandler(value = InsufficientStockException.class)
    public ResponseEntity<ApiError> handleInsufficientStockException() {

        ApiError error = new ApiError(HttpStatus.NOT_ACCEPTABLE.value(), "Insufficient Stock. Transaction could not be completed.", new Date(System.currentTimeMillis()));

        return new ResponseEntity<>(error, HttpStatus.NOT_ACCEPTABLE);

    }

}
