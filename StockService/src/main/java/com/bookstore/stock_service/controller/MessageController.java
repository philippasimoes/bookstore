package com.bookstore.stock_service.controller;

import com.bookstore.stock_service.publisher.RabbitMQProducer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/stock")
public class MessageController {

    @Autowired
    private RabbitMQProducer producer;

    @PostMapping("/publish")
    public ResponseEntity<String> sendMessage(@RequestParam("book_id") int book_id, @RequestParam("stock")int stock) {
        producer.sendMessage(book_id, stock);
        return ResponseEntity.ok("message send to RabbitMQ");
    }
}
