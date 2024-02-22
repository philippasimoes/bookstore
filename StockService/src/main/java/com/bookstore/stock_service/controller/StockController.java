package com.bookstore.stock_service.controller;

import com.bookstore.stock_service.publisher.RabbitMQProducer;
import com.bookstore.stock_service.service.StockService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;


@RestController
@RequestMapping(value = "/stock")
public class StockController {

    @Autowired
    StockService stockService;

    @Autowired
    RabbitMQProducer producer;

    RestTemplate restTemplate = new RestTemplate();

    @PostMapping("/book/{book_id}")
    public ResponseEntity<String> addStock(@PathVariable int book_id, @RequestParam("stock") int stock) {

        ResponseEntity<String> responseEntity;

        if (Boolean.TRUE.equals(restTemplate.getForObject("http://catalog-service:8080/books/exists/" + book_id, Boolean.class))) {
            responseEntity = ResponseEntity.status(HttpStatus.OK).body(stockService.addStock(book_id, stock));
            producer.sendMessage(book_id, stockService.getStockByBookId(book_id).getAvailableStock());

        } else
            responseEntity = ResponseEntity.status(HttpStatus.NOT_FOUND).body("Book is not present in catalog. Please insert the book in Catalog Service before adding stock");

        return responseEntity;
    }
}
