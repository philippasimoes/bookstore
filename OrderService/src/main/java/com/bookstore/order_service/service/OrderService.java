package com.bookstore.order_service.service;

import com.bookstore.exception.ResourceNotFoundException;
import com.bookstore.order_service.model.dto.ItemDto;
import com.bookstore.order_service.model.dto.OrderDto;
import com.bookstore.order_service.model.dto.enums.OrderStatus;
import com.bookstore.order_service.model.entity.Item;
import com.bookstore.order_service.model.entity.Order;
import com.bookstore.order_service.model.mapper.ItemMapper;
import com.bookstore.order_service.model.mapper.OrderMapper;
import com.bookstore.order_service.repository.ItemRepository;
import com.bookstore.order_service.repository.OrderRepository;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.circuitbreaker.CircuitBreaker;
import org.springframework.cloud.client.circuitbreaker.CircuitBreakerFactory;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class OrderService {
  private static final Logger LOGGER = LogManager.getLogger(OrderService.class);

  @Autowired OrderRepository orderRepository;

  @Autowired ItemRepository itemRepository;

  @Autowired OrderMapper orderMapper;

  @Autowired ItemMapper itemMapper;

  @Autowired RestTemplate restTemplate;

  @Autowired CircuitBreakerFactory circuitBreakerFactory;

  private static final String STOCK_URL = "http://stock-service:10001/stock/";

  private static final String PAYMENTS_URL = "http://payment-service:10004/payment";

  /**
   * Create new order and order items.
   *
   * @param orderDto the order DTO.
   * @return the new order (DTO).
   */
  public OrderDto createNewOrder(OrderDto orderDto) {

    Order order = orderRepository.save(orderMapper.toEntity(orderDto));

    List<Item> itemList = new ArrayList<>();

    for (ItemDto itemDto : orderDto.getItems()) {
      if (confirmStock(itemDto.getBookId(), itemDto.getQuantity())) {
        Item item = itemMapper.toEntity(itemDto);
        item.setOrder(order);
        itemList.add(itemRepository.save(item));
      } else {
        LOGGER.warn(
            String.format("Book with id %s not added - not enough stock.", itemDto.getBookId()));
      }
    }

    order.setStatus(OrderStatus.OPEN);
    order.setItems(itemList);

    Order savedOrder = orderRepository.save(order);
    return orderMapper.toDto(savedOrder);
  }

  /**
   * Edit the order status.
   *
   * @param id the order identifier.
   * @param orderStatus the desired order status.
   * @return true if the update is successful.
   */
  public boolean editOrderStatus(int id, OrderStatus orderStatus) {
    if (orderRepository.findById(id).isPresent()) {

      Order order = orderRepository.findById(id).get();
      order.setStatus(orderStatus);

      // order is completed and ready to be sent to payment service - the total price is calculated
      // and the information about the order is sent via rest template
      if (orderStatus.equals(OrderStatus.READY_TO_PAY)) {
        double totalPrice = 0;
        for (Item item : order.getItems()) {

          totalPrice = totalPrice + (item.getQuantity() * item.getUnitPrice());
        }
        order.setTotalPrice(totalPrice);
        order.setEditable(false);

        sendOrderToPaymentService(id, order.getCustomerId(), order.getTotalPrice());
      }

      // in case of rollback to add or remove items
      if (orderStatus.equals(OrderStatus.OPEN)) {
        order.setEditable(true);
      }

      orderRepository.save(order);

      return true;
    } else {
      throw new ResourceNotFoundException("Order not found");
    }
  }

  /**
   * Update one item from order.
   *
   * @param orderId the order identifier.
   * @param itemDto the item to be updated.
   * @return hte updated order (DTO).
   */
  public OrderDto updateOrderItem(int orderId, ItemDto itemDto) {

    if (orderRepository.findById(orderId).isPresent()) {
      if (itemRepository.findById(itemDto.getId()).isPresent()) {
        if (itemDto.getQuantity() == 0) {
          orderRepository.deleteById(itemDto.getId());
        } else if (!confirmStock(itemDto.getBookId(), itemDto.getQuantity())) {
          LOGGER.warn(
              String.format(
                  "Order could not be updated: book with id %s not added - not enough stock.",
                  itemDto.getBookId()));
        } else {
          itemRepository.save(itemMapper.toEntity(itemDto));
        }
        return orderMapper.toDto(orderRepository.findById(orderId).get());
      } else throw new ResourceNotFoundException("Item not found");
    } else throw new ResourceNotFoundException("Order not found");
  }

  /**
   * Delete items from order.
   *
   * @param orderId the order identifier.
   * @param itemDtoList the list of orders to delete.
   * @return the updated order (DTO).
   */
  public OrderDto deleteOrderItems(int orderId, List<ItemDto> itemDtoList) {
    if (orderRepository.findById(orderId).isPresent()) {
      for (ItemDto itemDto : itemDtoList) {
        if (itemRepository.findById(itemDto.getId()).isPresent()) {
          itemRepository.delete(itemMapper.toEntity(itemDto));
        } else {
          throw new ResourceNotFoundException("Item not found");
        }
      }
    } else {
      throw new ResourceNotFoundException("Order not found");
    }
    return orderMapper.toDto(orderRepository.findById(orderId).get());
  }

  private boolean confirmStock(int bookId, int quantity) {

    CircuitBreaker circuitBreaker = circuitBreakerFactory.create("circuitbreaker");

    int availableUnits =
        circuitBreaker.run(
            () ->
                restTemplate
                    .exchange(STOCK_URL + bookId, HttpMethod.GET, null, Integer.class)
                    .getBody(),
            throwable -> {
              LOGGER.warn("Error connecting to stock service.", throwable);
              return 0;
            });

    return availableUnits > quantity;
  }

  private void sendOrderToPaymentService(int orderId, int customerId, double price) {
    // connection with payment service if the order is ready

    Map<String,String> map = new HashMap<>();
    map.put("orderId", String.valueOf(orderId));
    map.put("customerId", String.valueOf(customerId));
    map.put("price", String.valueOf(price));

    CircuitBreaker circuitBreaker = circuitBreakerFactory.create("circuitbreaker");

    circuitBreaker.run(
        () -> restTemplate.postForObject(PAYMENTS_URL, map, Void.class),
        throwable -> {
          LOGGER.warn("Error connecting to payment service.", throwable);
          return null;
        });
  }
}
