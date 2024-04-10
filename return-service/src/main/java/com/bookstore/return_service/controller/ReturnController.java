package com.bookstore.return_service.controller;

import com.bookstore.return_service.model.dto.ReturnDto;
import com.bookstore.return_service.service.ReturnService;
import com.fasterxml.jackson.core.JsonProcessingException;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.Map;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/return")
@Tag(name = "Return endpoints")
public class ReturnController {

  private static final Logger LOGGER = LogManager.getLogger(ReturnController.class);

  @Autowired ReturnService returnService;

  @PostMapping
  public ResponseEntity<Map<String, String>> createReturn(@RequestBody ReturnDto returnDto) {
    try {
      Map<String, String> map = returnService.createReturn(returnDto);
      return ResponseEntity.ok(map);
    } catch (RuntimeException | JsonProcessingException e) {
      LOGGER.error(e.getMessage());
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
  }
}
