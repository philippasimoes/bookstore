package com.bookstore.dummy_ctt.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/ctt")
public class CttController {

  @GetMapping("/tracking-code")
  public ResponseEntity<String> generateTrackingCode(@RequestParam(value = "client_id") String clientId, @RequestParam(value="order_id") int orderId) {
    return ResponseEntity.ok(UUID.randomUUID().toString());
  }

}
