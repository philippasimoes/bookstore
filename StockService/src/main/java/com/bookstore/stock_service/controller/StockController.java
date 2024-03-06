package com.bookstore.stock_service.controller;

import com.bookstore.stock_service.infrastructure.message.publisher.RabbitMQProducer;
import com.bookstore.stock_service.service.StockService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Map;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.util.Pair;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

@RestController
@RequestMapping(value = "/stock")
public class StockController {

  private final Logger LOGGER = LogManager.getLogger(StockController.class);

  @Autowired StockService stockService;

  @Autowired RabbitMQProducer producer;

  @Autowired ObjectMapper objectMapper;

  @Autowired RestTemplate restTemplate;

  private static final String TOKEN_URL =
      "http://keycloak:8080/realms/bookstore/protocol/openid-connect/token";

  private static final String CATALOG_SERVICE_ID = "catalog-service";

  private static final String CATALOG_SERVICE_SECRET = "FD3bZqrV67ZGFktuQnX02qaPMuE3V71v";

  private static final String GRANT_TYPE = "client_credentials";

  private static final String BOOK_CONFIRMATION_URL =
      "http://catalog-service:10000/books/confirmation/";

  @Value("${rabbitmq.queue.event.updated.name}")
  private String eventUpdatedQueue;

  @Value("${rabbitmq.queue.event.soldout.name}")
  private String eventSoldOutQueue;

  @PostMapping("/book/{book_id}")
  public ResponseEntity<String> updateStock(
      @PathVariable("book_id") int bookId, @RequestParam("stock") int stock) {

    ResponseEntity<String> responseEntity;
    try {
      if (bookExists(bookId)) {
        if (stock > 0) {
          int actualStock = stockService.addStock(bookId, stock);
          responseEntity = ResponseEntity.status(HttpStatus.OK).body("Stock updated"); // fora do if

          producer.sendMessage(eventUpdatedQueue, buildMessage(bookId));
          LOGGER.info(
              String.format("Stock updated - book with id %s have %s units", bookId, actualStock));

        } else {
          int actualStock = stockService.decreaseStock(bookId, Math.abs(stock));
          responseEntity = ResponseEntity.status(HttpStatus.OK).body("Stock updated");

          if (actualStock > 0) {
            producer.sendMessage(eventUpdatedQueue, buildMessage(bookId));
            LOGGER.info(
                String.format(
                    "Stock updated - book with id %s have %s units", bookId, actualStock));
          } else {
            producer.sendMessage(eventSoldOutQueue, buildMessage(bookId));
            LOGGER.info(String.format("Stock updated - book with id %s is sold out", bookId));
          }
        }
      } else
        responseEntity =
            ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(
                    "Book is not present in catalog. "
                        + "Please insert the book in Catalog Service before adding stock.");
    } catch (JsonProcessingException e) {
      responseEntity =
          ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
              .body(
                  "Stock is updated but the "
                      + "connection with catalog service was not made due to an error building the message.");
      LOGGER.error("Error building message", e);
    }
    return responseEntity;
  }

  @GetMapping("/{bookId}")
  public boolean stockIsAboveZero(@PathVariable("bookId") int bookId) {

    return stockService.getStockByBookId(bookId).getAvailableStock() > 0;
  }

  private String buildMessage(int bookId) throws JsonProcessingException {

    Pair<Integer, Integer> pair =
        Pair.of(bookId, stockService.getStockByBookId(bookId).getAvailableStock());

    return objectMapper.writeValueAsString(pair);
  }

  private String authenticateAndGetJwtToken() {

    HttpHeaders authHeaders = new HttpHeaders();
    authHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

    String requestBody =
        "grant_type="
            + GRANT_TYPE
            + "&client_id="
            + CATALOG_SERVICE_ID
            + "&client_secret="
            + CATALOG_SERVICE_SECRET;

    HttpEntity<String> requestEntity = new HttpEntity<>(requestBody, authHeaders);

    ResponseEntity<String> response =
        restTemplate.postForEntity(TOKEN_URL, requestEntity, String.class);

    if (response.getStatusCode().is2xxSuccessful()) {
      LOGGER.info("Authentication successful.");
      return response.getBody();
    } else {
      LOGGER.error(String.format("Authentication failure. Status: %s.", response.getStatusCode()));
      return null;
    }
  }

  private boolean bookExists(int bookId) throws JsonProcessingException {

    HttpHeaders headers = new HttpHeaders();
    headers.set(
        "Authorization",
        "Bearer "
            + objectMapper.readValue(authenticateAndGetJwtToken(), Map.class).get("access_token"));

    HttpEntity<String> requestEntity = new HttpEntity<>(headers);

    ResponseEntity<Boolean> response =
        restTemplate.exchange(
            BOOK_CONFIRMATION_URL + bookId, HttpMethod.GET, requestEntity, Boolean.class);

    return response.getStatusCode().is2xxSuccessful();
  }
}
