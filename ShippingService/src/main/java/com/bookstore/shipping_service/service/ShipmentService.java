package com.bookstore.shipping_service.service;

import com.bookstore.shipping_service.exception.DuplicatedResourceException;
import com.bookstore.shipping_service.model.dto.Address;
import com.bookstore.shipping_service.model.dto.GeoApiObject;
import com.bookstore.shipping_service.model.dto.Ponto;
import com.bookstore.shipping_service.model.dto.enums.ShipmentMethod;
import com.bookstore.shipping_service.model.entity.Shipment;
import com.bookstore.shipping_service.repository.ShipmentRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.circuitbreaker.CircuitBreaker;
import org.springframework.cloud.client.circuitbreaker.CircuitBreakerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.sql.Timestamp;
import java.util.Optional;
import java.util.zip.DataFormatException;

@Service
@Transactional
public class ShipmentService {
  private static final Logger LOGGER = LogManager.getLogger(ShipmentService.class);
  private static final String GEOAPI_URL = "https://geoapi.pt/cp/";
  private static final String DUMMY_CTT_URL = "http://dummy-ctt:10008/ctt/tracking-code";
  @Autowired RestTemplate restTemplate;

  @Autowired CircuitBreakerFactory circuitBreakerFactory;

  @Autowired ObjectMapper objectMapper;

  @Autowired ShipmentRepository shipmentRepository;

  public boolean validateAddress(Address address) throws JsonProcessingException {

    boolean exists = false;

    CircuitBreaker circuitBreaker = circuitBreakerFactory.create("circuitbreaker-address");

    String addresses =
        circuitBreaker.run(
            () ->
                restTemplate.getForObject(
                    GEOAPI_URL + address.postalCode() + "?json=1", String.class),
            throwable -> {
              LOGGER.warn("Error connecting to geoapi.", throwable);
              return null;
            });

    GeoApiObject geoApiObject = objectMapper.readValue(addresses, GeoApiObject.class);

    for (Ponto ponto : geoApiObject.getPontos()) {
      if (ponto.getRua().contains(address.streetName().toUpperCase())
          && ponto.getCasa().contains(address.number())) {
        exists = true;
        break;
      }
    }
    return exists;
  }

  public double calculateTax(double weight) {
    if (weight <= 5.0) {
      return 2.60;
    } else return 2.95;
  }

  public int createShipment(int orderId) {

    Optional<Shipment> shipment = shipmentRepository.findByOrderId(orderId);

    if (shipment.isEmpty()) {
      Shipment newShipment = new Shipment();
      newShipment.setOrderId(orderId);
      newShipment.setShipmentMethod(ShipmentMethod.CTT);

      CircuitBreaker circuitBreaker = circuitBreakerFactory.create("circuitbreaker-shipment");

      String trackingNumber =
          circuitBreaker.run(
              () ->
                  restTemplate.getForObject(
                      DUMMY_CTT_URL + "?client_id=bookstore?order_id=" + orderId, String.class),
              throwable -> {
                LOGGER.warn("Error connecting to dummy ctt.", throwable);
                return null;
              });

      newShipment.setTrackingNumber(trackingNumber);
      newShipment.setDate(new Timestamp(System.currentTimeMillis()));


      return  shipmentRepository.save(newShipment).getId();
    } else
      throw new DuplicatedResourceException(
          String.format("Shipment record for order %s already exists.", orderId));
  }
}
