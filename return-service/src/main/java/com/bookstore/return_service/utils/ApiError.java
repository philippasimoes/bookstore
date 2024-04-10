package com.bookstore.return_service.utils;

import com.bookstore.return_service.controller.exception_handler.RestExceptionHandler;
import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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
