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
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.sql.Time;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.circuitbreaker.CircuitBreaker;
import org.springframework.cloud.client.circuitbreaker.CircuitBreakerFactory;
import org.springframework.data.util.Pair;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class OrderService {

  private static final Logger LOGGER = LogManager.getLogger(OrderService.class);
  @Autowired OrderRepository orderRepository;
  @Autowired ItemRepository itemRepository;
  @Autowired
  OrderMapper orderMapper;
  @Autowired ItemMapper itemMapper;
  @Autowired RestTemplate restTemplate;
  @Autowired ObjectMapper objectMapper;
  @Autowired CircuitBreakerFactory circuitBreakerFactory;
  private static final String STOCK_URL = "http://stock-service/stock/";
  private static final String NOTIFICATION_URL = "http://notification-service/order";
  private static final String SHIPMENT_URL = "http://shipping-service/shipment/tax?weight=";
  private static final String USER_URL = "http://user-service/user/";

  /**
   * Create new order and order items.
   *
   * @param orderDto the order DTO.
   * @return the new order (DTO).
   */
  public OrderDto createNewOrder(OrderDto orderDto) {

    Order order = orderRepository.save(orderMapper.toEntity(orderDto));

    List<Item> itemList = new ArrayList<>();

    if (!orderDto.getItems().isEmpty()) {
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
    }

    order.setEditable(true);
    order.setStatus(OrderStatus.OPEN);
    order.setItems(itemList);

    Order savedOrder = orderRepository.save(order);
    return orderMapper.toDto(savedOrder);
  }

  /**
   * Edit the order status - possible options: OPEN, READY_TO_PAY, DELIVERED and CANCELLED (status
   * PAID and SHIPPED will be added in {@link #setOrderStatusToPaid(String) setOrderStatusToPaid}
   * and {@link #setOrderStatusToShipped(String) setOrderStatusToShipped}).
   *
   * @param id the order identifier.
   * @param orderStatus the desired order status.
   * @return the edited order dto.
   */
  public OrderDto editOrderStatus(int id, OrderStatus orderStatus) {
    Optional<Order> order = orderRepository.findById(id);

    if (order.isPresent()) {

      if (order.get().getStatus().equals(OrderStatus.SHIPPED)
          || order.get().getStatus().equals(OrderStatus.DELIVERED)
          || order.get().getStatus().equals(OrderStatus.CANCELLED)
          || order.get().getStatus().equals(OrderStatus.PAID)) {
        LOGGER.error("The order can't be edited");
      }

      switch (orderStatus) {
        case OPEN -> setOrderStatusToOpen(order.get());
        case READY_TO_PAY -> setOrderStatusToReadyToPay(id);
        case DELIVERED, CANCELLED -> setOrderStatusToDeliveredOrCancelled(orderStatus, order.get());
      }
      return orderMapper.toDto(order.get());
    } else {
      throw new ResourceNotFoundException("Order not found");
    }
  }

  public List<OrderDto> findDeliveredOrdersByCustomerAndShipping(int customerId) {

    Timestamp initialShippingDate = Timestamp.from(Instant.now().minus(30, ChronoUnit.DAYS));
    Timestamp finalShippingDate = Timestamp.from(Instant.now());

    return orderMapper.toDtoList(orderRepository.findByCustomerIdAndShipmentDate(
        customerId, OrderStatus.DELIVERED.name(), initialShippingDate, finalShippingDate));
  }

  /**
   * Receive shipped orders in shipped events queue.
   *
   * @param message the message containing the order identifier and other relevant data.
   */
  @RabbitListener(queues = "${rabbitmq.queue.event.shipped.name}")
  public void consumeShippedEvents(String message) {

    LOGGER.info(String.format("received message [%s]", message));

    setOrderStatusToShipped(message);
  }

  /**
   * Receive paid orders in paid events queue.
   *
   * @param message the message sent by Payment Service containing the order identifier.
   */
  @RabbitListener(queues = "${rabbitmq.queue.event.paid.name}")
  public void consumePaidEvents(String message) {

    LOGGER.info(String.format("received message [%s]", message));

    setOrderStatusToPaid(message);
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
          adjustStockWhenOrderIsCancelledOrItemIsRemovedFromOrder(
              itemDto.getBookId(), itemDto.getQuantity());
        } else {
          throw new ResourceNotFoundException("Item not found");
        }
      }
    } else {
      throw new ResourceNotFoundException("Order not found");
    }

    return orderMapper.toDto(order.get());
  }

  /**
   * Checks whether the book's available units are sufficient.
   *
   * @param bookId the book identifier.
   * @param quantity the book units to confirm.
   * @return true if the available units are sufficient.
   */
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

  /**
   * Order is shipped - the order status is updated accordingly and the stock is adjusted. A
   * notification is created to send an e-mail to customer.
   *
   * @param message the message containing the order identifier and other relevant data.
   */
  private void setOrderStatusToShipped(String message) {

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

        // sending e-mail to client with tracking code
        createNotificationToSendEmail(
            orderMapper.toDto(updatedOrder), customerEmail, map.get("trackingCode"));

        // updating stock
        for (Item item : order.get().getItems()) {
          updateStockWhenOrderIsShipped(item.getBookId(), item.getQuantity());
        }

      } else throw new ResourceNotFoundException("Order not found");

    } catch (JsonProcessingException e) {
      throw new RuntimeException(e);
    }
  }

  /**
   * Change {@link OrderStatus} to Delivered or Cancelled - the order cannot be edited anymore.
   *
   * @param orderStatus the desired status (Delivered or Cancelled).
   * @param order       the order to edit.
   */
  private void setOrderStatusToDeliveredOrCancelled(OrderStatus orderStatus, Order order) {

    order.setEditable(false);
    order.setStatus(orderStatus);
    orderRepository.save(order);
  }

  /**
   * Change {@link OrderStatus} to OPEN, and it's possible to edit the order again.
   *
   * @param order the order to edit.
   */
  private void setOrderStatusToOpen(Order order) {

    order.setEditable(true);
    order.setStatus(OrderStatus.OPEN);
    orderRepository.save(order);
  }

  /**
   * Order is shipped and units are removed from pending units.
   *
   * @param bookId the book identifier.
   * @param units the units to be removed from pending units.
   */
  private void updateStockWhenOrderIsShipped(int bookId, int units) {

    CircuitBreaker circuitBreaker = circuitBreakerFactory.create("circuitbreakerstock");

    circuitBreaker.run(
        () ->
            restTemplate.patchForObject(
                STOCK_URL + "/book/" + bookId + "?pending-units=" + units, null, String.class),
        throwable -> {
          LOGGER.warn("Error connecting to stock service.", throwable);
          return null;
        });
  }

  /**
   * Order is cancelled or the items are removed from the order - units are removed from the pending
   * units and reintroduced in the available units.
   *
   * @param bookId the book identifier.
   * @param units the book units.
   */
  private void adjustStockWhenOrderIsCancelledOrItemIsRemovedFromOrder(int bookId, int units) {

    CircuitBreaker circuitBreaker = circuitBreakerFactory.create("circuitbreakerstock");

    circuitBreaker.run(
        () ->
            restTemplate.patchForObject(
                STOCK_URL + "/book/order-canceled" + bookId + "?pending-units=" + units,
                null,
                String.class),
        throwable -> {
          LOGGER.warn("Error connecting to stock service.", throwable);
          return null;
        });
  }

  /**
   * Order is completed and ready to be paid - the total order value is defined (items price + tax)
   * and the order cannot be edited (unless the order status is changed to open again).
   *
   * @param id the order identifier.
   * @return true if order is found in database and updated successfully.
   */
  private boolean setOrderStatusToReadyToPay(int id) {

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

      orderRepository.save(order);
      return true;
    } else {
      throw new ResourceNotFoundException("Order not found");
    }
  }

  /**
   * Order is paid and the status is changed accordingly.
   *
   * @param message The message from Payment Service containing the order identifier.
   */
  private void setOrderStatusToPaid(String message) {

    Pair<String, Integer> pair;

    try {
      pair = objectMapper.readValue(message, Pair.class);

      Optional<Order> order = orderRepository.findById(pair.getSecond());

      if (order.isPresent()) {
        order.get().setStatus(OrderStatus.PAID);
        order.get().setEditable(false);
        orderRepository.save(order.get());

      } else throw new ResourceNotFoundException("Order not found");

    } catch (JsonProcessingException e) {
      throw new RuntimeException(e);
    }
  }

  /**
   * Connection with Shipping Service to get the tax value.
   *
   * @param orderWeight the total order weight.
   * @return the tax value.
   */
  private Double calculateTax(double orderWeight) {

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

  /**
   * Connection with User Service to get the customer e-mail.
   *
   * @param customerId the customer identifier.
   * @return the customer e-mail.
   */
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

  /**
   * Create an order notification to send the order summary to customer.
   *
   * @param orderDto the order.
   * @param customerEmail the customer e-mail.
   * @param trackingCode the order tracking code.
   */
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
