package com.bookstore.stock_service.controller;

import com.bookstore.stock_service.infrastructure.message.publisher.RabbitMQProducer;
import com.bookstore.stock_service.service.StockService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import org.springframework.jdbc.core.SqlReturnType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.net.http.HttpResponse;
import java.util.Map;

@RestController
@RequestMapping(value = "/stock")
public class StockController {

  private final Logger LOGGER = LogManager.getLogger(StockController.class);

  @Autowired StockService stockService;

  @Autowired RabbitMQProducer producer;

  @Autowired ObjectMapper objectMapper;

  @Autowired RestTemplate restTemplate;

  @Value("${rabbitmq.queue.event.updated.name}")
  private String eventUpdatedQueue;

  @Value("${rabbitmq.queue.event.soldout.name}")
  private String eventSoldOutQueue;



  @PostMapping("/book/{book_id}")
  public ResponseEntity<String> updateStock(
      @PathVariable("book_id") int bookId,
      @RequestParam("stock") int stock) {

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


  public String authenticateAndGetJwtToken(){
    String authUrl="http://keycloak:8080/realms/bookstore/protocol/openid-connect/token";

    HttpHeaders authHeaders = new HttpHeaders();
    authHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

    String requestBody = "grant_type=client_credentials&client_id=catalog-service&client_secret=FD3bZqrV67ZGFktuQnX02qaPMuE3V71v";
    HttpEntity<String> requestEntity = new HttpEntity<>(requestBody, authHeaders);

    ResponseEntity<String> response = restTemplate.postForEntity(authUrl, requestEntity, String.class);

    if(response.getStatusCode().is2xxSuccessful()){
        return response.getBody();
    } else {
      System.err.println("Authentication failure. Status: " + response.getStatusCode());
      return null;
    }
  }
  private boolean bookExists(int bookId) throws JsonProcessingException {
   ObjectMapper objectMapper = new ObjectMapper();

    Map jwtString = objectMapper.readValue(authenticateAndGetJwtToken(), Map.class);

    HttpHeaders headers = new HttpHeaders();
    headers.set("Authorization", "Bearer "+ jwtString.get("access_token"));
    HttpEntity<String> requestEntity = new HttpEntity<>(headers);

    ResponseEntity<Boolean> response = restTemplate.exchange("http://catalog-service:10000/books/exists/" + bookId, HttpMethod.GET, requestEntity, Boolean.class);

    return Boolean.TRUE.equals(response.getBody());
  }
}
