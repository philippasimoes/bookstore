package com.bookstore.notification_service.controller.exception_handler;

import com.bookstore.notification_service.exception.NotificationNotFoundException;
import com.bookstore.notification_service.utils.ApiError;
import java.util.Date;
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

    @ExceptionHandler(value = NotificationNotFoundException.class)
    public ResponseEntity<ApiError> handleResourceNotFoundException() {

        ApiError error = new ApiError(HttpStatus.NOT_FOUND.value(), "Notification Not Found", new Date(System.currentTimeMillis()));
        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }
    

}
