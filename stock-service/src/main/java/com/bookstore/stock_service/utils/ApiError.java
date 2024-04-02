package com.bookstore.stock_service.utils;

import com.bookstore.stock_service.controller.exception_handler.RestExceptionHandler;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

/**
 * API error class - used by {@link RestExceptionHandler}.
 *
 * @author Filipa Simões
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ApiError {

    private int errorCode;
    private String description;
    private Date date;
}
