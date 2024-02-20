package com.bookstore.stock_service.controller;

import com.bookstore.stock_service.dto.StockDto;
import com.bookstore.stock_service.service.StockService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping(value = "/stock")
public class StockController {

    @Autowired
    StockService stockService;

    @GetMapping("/{id}")
    public ResponseEntity<StockDto> getAuthorByID(@PathVariable int id) {
        return ResponseEntity.status(HttpStatus.OK).body(stockService.getAuthorByID(id));
    }
}
