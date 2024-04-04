package com.bookstore.dummy_ctt.controller;

import com.bookstore.dummy_ctt.service.CttService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/ctt")
public class CttController {

  @Autowired CttService cttService;

  @GetMapping("/tracking-code")
  public ResponseEntity<String> generateTrackingCode(
      @RequestParam(value = "client_id") String clientId,
      @RequestParam(value = "order_id") int orderId) {
    return ResponseEntity.ok(cttService.generateTrackingCode(clientId, orderId));
  }

  @GetMapping("/delivered")
  public ResponseEntity<Void> orderDelivered(@RequestParam(value = "order_id") int orderId) {
    cttService.notifyOrderDelivered(orderId);
    return ResponseEntity.ok().build();
  }

  public ResponseEntity<Void> returnCollected(@RequestParam(value = "return_id") int returnId) {
    return ResponseEntity.ok().build();
  }
}
