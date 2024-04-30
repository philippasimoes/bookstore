package com.bookstore.return_service.controller;

import com.bookstore.return_service.model.dto.ReturnDto;
import com.bookstore.return_service.service.ReturnService;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/return")
@Tag(name = "Return endpoints")
public class ReturnController {

  @Autowired ReturnService returnService;

  @PostMapping
  public ResponseEntity<Map<String, String>> createReturn(@RequestBody ReturnDto returnDto) {

    Map<String, String> map = returnService.createReturn(returnDto);
    return ResponseEntity.ok(map);
  }
}
