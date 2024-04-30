package com.bookstore.shipping_service.service;

import com.bookstore.shipping_service.exception.DuplicatedResourceException;
import com.bookstore.shipping_service.infrastructure.message.publisher.RabbitMQProducer;
import com.bookstore.shipping_service.model.dto.Address;
import com.bookstore.shipping_service.model.dto.GeoApiObject;
import com.bookstore.shipping_service.model.dto.Ponto;
import com.bookstore.shipping_service.model.dto.enums.ShipmentMethod;
import com.bookstore.shipping_service.model.entity.Shipment;
import com.bookstore.shipping_service.repository.ShipmentRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.circuitbreaker.CircuitBreaker;
import org.springframework.cloud.client.circuitbreaker.CircuitBreakerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@Transactional
public class ShipmentService {
  private static final Logger LOGGER = LogManager.getLogger(ShipmentService.class);
  private static final String GEO_API_URL = "https://geoapi.pt/cp/";
  private static final String DUMMY_CTT_URL = "http://dummy-ctt:10008/ctt/tracking-code";

  @Autowired
  RestTemplate restTemplate;
  @Autowired CircuitBreakerFactory circuitBreakerFactory;
  @Autowired ObjectMapper objectMapper;
  @Autowired ShipmentRepository shipmentRepository;
  @Autowired RabbitMQProducer producer;

  @Value("${rabbitmq.queue.event.shipped.name}")
  private String eventShippedQueue;

  /**
   * Validate address.
   *
   * @param address the address.
   * @return true if address is correct.
   */
  public boolean validateAddress(Address address) {

    boolean exists = false;

    CircuitBreaker circuitBreaker = circuitBreakerFactory.create("circuitbreaker-address");

    RestTemplate geoRestTemplate = new RestTemplate();

    String addresses =
        circuitBreaker.run(
            () ->
                geoRestTemplate.getForObject(
                    GEO_API_URL + address.postalCode() + "?json=1", String.class),
            throwable -> {
              LOGGER.log(Level.WARN, "Error connecting to geoapi.", throwable);
              return null;
            });

    try {
      GeoApiObject geoApiObject = objectMapper.readValue(addresses, GeoApiObject.class);

      for (Ponto ponto : geoApiObject.getPontos()) {
        if (ponto.getRua().contains(address.streetName().toUpperCase())
            && ponto.getCasa().contains(address.number())) {
          exists = true;
          break;
        }
      }

    } catch (JsonProcessingException e) {
      LOGGER.log(Level.ERROR, "Error parsing json object");
      e.getMessage();
    }

    return exists;
  }

  /**
   * Calculate the fee based on order weight.
   *
   * @param weight the order weight.
   * @return the fee.
   */
  public double calculateFee(double weight) {
    if (weight <= 5.0) {
      return 2.60;
    } else return 2.95;
  }

  /**
   * Create a {@link Shipment}.
   *
   * @param orderId the order identifier.
   * @return the new shipment identifier.
   */
  public int createShipment(int orderId) {

    Optional<Shipment> shipment = shipmentRepository.findByOrderId(orderId);

    if (shipment.isEmpty()) {
      String trackingNumber = getTrackingNumber(orderId);

      // dummy date because we don't have any external integration for shipment.
      Timestamp shipmentDate = new Timestamp(System.currentTimeMillis());

      Shipment newShipment = new Shipment();
      newShipment.setOrderId(orderId);
      newShipment.setShipmentMethod(ShipmentMethod.CTT);
      newShipment.setTrackingNumber(trackingNumber);
      newShipment.setDate(shipmentDate);

      producer.sendMessage(eventShippedQueue, buildMessage(orderId, trackingNumber, shipmentDate));

      return shipmentRepository.save(newShipment).getId();
    } else
      throw new DuplicatedResourceException(
          String.format("Shipment record for order %s already exists.", orderId));
  }

  /**
   * Get tracking number from an external service (for test purposes, it's used a dummy service to
   * generate the tracking number).
   *
   * @param orderId the order identifier.
   * @return the tracking number.
   */
  private String getTrackingNumber(int orderId) {

    CircuitBreaker circuitBreaker = circuitBreakerFactory.create("circuitbreaker-shipment");

    return circuitBreaker.run(
        () ->
            restTemplate.getForObject(
                DUMMY_CTT_URL + "?client_id=bookstore&order_id=" + orderId, String.class),
        throwable -> {
          LOGGER.log(Level.WARN, "Error connecting to dummy ctt.", throwable);
          return null;
        });
  }

  /**
   * Build a message with relevant data (to be sent to {@link #eventShippedQueue}).
   *
   * @param orderId the order identifier.
   * @param trackingCode the order tracking code.
   * @param date the shipment date.
   * @return a json string with the information above.
   */
  private String buildMessage(int orderId, String trackingCode, Timestamp date) {

    Map<String, String> map = new HashMap<>();
    map.put("orderId", String.valueOf(orderId));
    map.put("trackingCode", trackingCode);
    map.put("date", String.valueOf(date));

    try {
      return objectMapper.writeValueAsString(map);
    } catch (JsonProcessingException e) {
      LOGGER.log(Level.ERROR, "Error building message", e);
      return null;
    }
  }
}
