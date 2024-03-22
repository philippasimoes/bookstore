package com.bookstore.order_service.service;

import com.bookstore.order_service.exception.ResourceNotFoundException;
import com.bookstore.order_service.model.dto.ItemDto;
import com.bookstore.order_service.model.dto.OrderDto;
import com.bookstore.order_service.model.dto.enums.OrderStatus;
import com.bookstore.order_service.model.entity.Item;
import com.bookstore.order_service.model.entity.Order;
import com.bookstore.order_service.model.mapper.ItemMapper;
import com.bookstore.order_service.model.mapper.OrderMapper;
import com.bookstore.order_service.repository.ItemRepository;
import com.bookstore.order_service.repository.OrderRepository;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
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

  @Autowired
  ObjectMapper objectMapper;

  @Autowired CircuitBreakerFactory circuitBreakerFactory;

  private static final String STOCK_URL = "http://stock-service:10001/stock/";

  private static final String NOTIFICATION_URL = "http://notification-service:10002/order";

  private static final String PAYMENTS_URL = "http://payment-service:10004/payment";

  private static final String SHIPMENT_URL = "http://shipping-service:10006/shipment/tax?weight=";

  private static final String USER_URL = "http://user-service:10007/user/";

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
    Optional<Order> order = orderRepository.findById(id);

    if (order.isPresent()) {

      if (order.get().getStatus().equals(OrderStatus.SHIPPED)
          || order.get().getStatus().equals(OrderStatus.DELIVERED)
          || order.get().getStatus().equals(OrderStatus.CANCELLED)) {
        LOGGER.error("The order can't be edited");
        return false;
      }

      if (orderStatus.equals(OrderStatus.OPEN)) {
        order.get().setEditable(true);
        order.get().setStatus(orderStatus);
        orderRepository.save(order.get());
        return true;
      } else if (orderStatus.equals(OrderStatus.READY_TO_PAY)
          || orderStatus.equals(OrderStatus.DELIVERED)
          || orderStatus.equals(OrderStatus.CANCELLED)) {
        order.get().setEditable(false);
        order.get().setStatus(orderStatus);
        orderRepository.save(order.get());
        return true;
      } else return false;

    } else {
      throw new ResourceNotFoundException("Order not found");
    }
  }

  // order is shipped, notification is created to send email to the customer
  public void setOrderStatusToShipped(String message) {
    Map<String, String> map;

    try {
      map = objectMapper.readValue(message, Map.class);

      Optional<Order> order = orderRepository.findById(Integer.parseInt(map.get("orderId")));

      if (order.isPresent()) {
        order.get().setStatus(OrderStatus.SHIPPED);
        order.get().setEditable(false);
        order.get().setShipmentDate(Timestamp.valueOf(map.get("date")));
        Order updatedOrder = orderRepository.save(order.get());

        String customerEmail = getCustomerEmail(order.get().getCustomerId());

        createNotificationToSendEmail(orderMapper.toDto(updatedOrder), customerEmail, map.get("trackingCode"));

      } else throw new ResourceNotFoundException("Order not found");

    } catch (JsonProcessingException e) {
        throw new RuntimeException(e);
    }
  }

  @RabbitListener(queues = "${rabbitmq.queue.event.shipped.name}")
  public void consumeUpdatedEvents(String message) {

    LOGGER.info(String.format("received message [%s]", message));

    setOrderStatusToShipped(message);
  }

  // order is completed and ready to be sent to payment service - the total price is calculated
  // and the information about the order is sent via rest template
  public void setOrderStatusToReadyToPay(int id) {
    if (orderRepository.findById(id).isPresent()) {

      Order order = orderRepository.findById(id).get();
      order.setStatus(OrderStatus.READY_TO_PAY);

      double totalPrice = 0;
      double totalWeight = 0;
      for (Item item : order.getItems()) {

        totalPrice = totalPrice + (item.getQuantity() * item.getUnitPrice());
        totalWeight = totalWeight + (item.getQuantity() * item.getUnitWeight());
      }
      order.setTotalPriceItems(totalPrice);
      order.setTotalWeight(totalWeight);

      order.setTax(calculateTax(totalWeight));

      order.setTotalPriceOrder(order.getTotalPriceItems() + order.getTax());
      order.setEditable(false);

      sendOrderToPaymentService(id, order.getCustomerId(), order.getTotalPriceOrder());

      orderRepository.save(order);

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

    Optional<Order> order = orderRepository.findById(orderId);
    Optional<Item> item = itemRepository.findById(itemDto.getId());

    if (order.isPresent() && order.get().isEditable()) {
      if (item.isPresent()) {
        if (itemDto.getQuantity() == 0) {
          itemRepository.deleteById(itemDto.getId());
        } else if (!confirmStock(itemDto.getBookId(), itemDto.getQuantity())) {
          LOGGER.warn(
              String.format(
                  "Order could not be updated: book with id %s not added - not enough stock.",
                  itemDto.getBookId()));
        } else {
          itemRepository.save(itemMapper.toEntity(itemDto));
        }

        return orderMapper.toDto(order.get());
      } else throw new ResourceNotFoundException("Item not found");
    } else throw new ResourceNotFoundException("Order not found or not editable");
  }

  /**
   * Delete items from order.
   *
   * @param orderId the order identifier.
   * @param itemDtoList the list of orders to delete.
   * @return the updated order (DTO).
   */
  public OrderDto deleteOrderItems(int orderId, List<ItemDto> itemDtoList) {
    Optional<Order> order = orderRepository.findById(orderId);

    if (order.isPresent()) {
      for (ItemDto itemDto : itemDtoList) {
        Optional<Item> item = itemRepository.findById(itemDto.getId());
        if (item.isPresent()) {
          itemRepository.delete(item.get());
        } else {
          throw new ResourceNotFoundException("Item not found");
        }
      }
    } else {
      throw new ResourceNotFoundException("Order not found");
    }

    return orderMapper.toDto(order.get());
  }

  private boolean confirmStock(int bookId, int quantity) {

    CircuitBreaker circuitBreaker = circuitBreakerFactory.create("circuitbreakerstock");

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

    Map<String, String> map = new HashMap<>();
    map.put("orderId", String.valueOf(orderId));
    map.put("customerId", String.valueOf(customerId));
    map.put("price", String.valueOf(price));

    CircuitBreaker circuitBreaker = circuitBreakerFactory.create("circuitbreakerpayment");

    circuitBreaker.run(
        () -> restTemplate.postForObject(PAYMENTS_URL, map, Void.class),
        throwable -> {
          LOGGER.warn("Error connecting to payment service.", throwable);
          return null;
        });
  }

  private Double calculateTax(double orderWeight) {
    // connection with shipping service

    CircuitBreaker circuitBreaker = circuitBreakerFactory.create("circuitbreakertax");

    return circuitBreaker.run(
        () ->
            restTemplate
                .exchange(SHIPMENT_URL + orderWeight, HttpMethod.GET, null, Double.class)
                .getBody(),
        throwable -> {
          LOGGER.warn("Error connecting to stock service.", throwable);
          return 0.0;
        });
  }

  private String getCustomerEmail(int customerId) {
    CircuitBreaker circuitBreaker = circuitBreakerFactory.create("circuitbreakeruser");

    // get client email
    return circuitBreaker.run(
        () ->
            restTemplate
                .exchange(USER_URL + customerId, HttpMethod.GET, null, String.class)
                .getBody(),
        throwable -> {
          LOGGER.warn("Error connecting to user service.", throwable);
          return null;
        });
  }

  private void createNotificationToSendEmail(
      OrderDto orderDto, String customerEmail, String trackingCode) {
    CircuitBreaker circuitBreaker = circuitBreakerFactory.create("circuitbreakernotification");
    // create notification and send email to the client
    circuitBreaker.run(
        () ->
            restTemplate.postForObject(
                NOTIFICATION_URL
                    + "?customer_email="
                    + customerEmail
                    + "&tracking_number="
                    + trackingCode,
                orderDto,
                String.class),
        throwable -> {
          LOGGER.warn("Error connecting to notification service.", throwable);
          return null;
        });
  }
}
