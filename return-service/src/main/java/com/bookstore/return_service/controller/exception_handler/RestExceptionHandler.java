package com.bookstore.return_service.controller.exception_handler;


import java.util.Date;

import com.bookstore.return_service.exception.ResourceNotFoundException;
import com.bookstore.return_service.utils.ApiError;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

        ApiError error = new ApiError(HttpStatus.NOT_FOUND.value(), "Notification Not Found", new Date(System.currentTimeMillis()));
        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }
    

}
