package com.bookstore.order_service.controller;

import com.bookstore.order_service.model.dto.ItemDto;
import com.bookstore.order_service.model.dto.OrderDto;
import com.bookstore.order_service.model.dto.enums.OrderStatus;
import com.bookstore.order_service.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/order")
public class OrderController {

  // TODO: falta validar user
  // TODO: falta cenário de pre-order
  @Autowired OrderService orderService;

  @PostMapping
  public ResponseEntity<OrderDto> createNewOrder(@RequestBody OrderDto orderDto) {
    return ResponseEntity.status(HttpStatus.CREATED).body(orderService.createNewOrder(orderDto));
  }

  //to set status OPEN, CANCELLED or DELIVERED
  @PatchMapping("/{id}")
  public ResponseEntity<Boolean> updateOrderStatus(
      @PathVariable(value = "id") int id, @RequestParam OrderStatus status) {
    return ResponseEntity.ok(orderService.editOrderStatus(id, status));
  }

  //to set status READY_TO_PAY
  @PatchMapping("/{id}/payment")
  public ResponseEntity<Boolean> setOrderStatusToReadyToPay(
      @PathVariable(value = "id") int id) {
    orderService.setOrderStatusToReadyToPay(id);
    return ResponseEntity.ok().build();
  }

  @PatchMapping("/edit-items/{order_id}")
  public ResponseEntity<OrderDto> editOrderItem(
      @PathVariable(value = "order_id") int orderId, @RequestBody ItemDto itemDto) {
    return ResponseEntity.ok(orderService.updateOrderItem(orderId, itemDto));
  }

  @PatchMapping("/delete-items/{order_id}")
  public ResponseEntity<OrderDto> deleteOrderItems(
      @PathVariable(value = "order_id") int orderId, @RequestBody List<ItemDto> itemDtoList) {
    return ResponseEntity.ok(orderService.deleteOrderItems(orderId, itemDtoList));
  }
}
