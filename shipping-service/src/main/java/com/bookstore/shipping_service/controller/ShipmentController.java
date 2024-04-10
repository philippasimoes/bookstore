package com.bookstore.shipping_service.controller;

import com.bookstore.shipping_service.model.dto.Address;
import com.bookstore.shipping_service.service.ShipmentService;
import com.fasterxml.jackson.core.JsonProcessingException;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/shipment")
@Tag(name = "Shipment endpoints")
public class ShipmentController {

  @Autowired ShipmentService shipmentService;

  @GetMapping("/address-validator")
  public ResponseEntity<Void> validateAddress(@RequestBody Address address)
      throws JsonProcessingException {

    if (shipmentService.validateAddress(address)) return ResponseEntity.ok().build();
    else return ResponseEntity.notFound().build();
  }

  @GetMapping("/tax")
  public ResponseEntity<Double> calculateTax(@RequestParam double weight) {
    return ResponseEntity.ok(shipmentService.calculateFee(weight));
  }

  @PostMapping
  public ResponseEntity<Integer> createShipment(@RequestParam(value = "order_id") int orderId) {
    return ResponseEntity.ok(shipmentService.createShipment(orderId));
  }
}
