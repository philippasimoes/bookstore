package com.bookstore.stock_service.service;

import com.bookstore.stock_service.exception.InsufficientStockException;
import com.bookstore.stock_service.exception.StockFoundException;
import com.bookstore.stock_service.exception.StockNotFoundException;
import com.bookstore.stock_service.infrastructure.message.publisher.RabbitMQProducer;
import com.bookstore.stock_service.model.entity.Stock;
import com.bookstore.stock_service.repository.StockRepository;
import com.bookstore.stock_service.utils.StockStatus;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import java.util.Map;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.util.Pair;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@Transactional
public class StockService {
  private static final Logger LOGGER = LogManager.getLogger(StockService.class);
  @Autowired StockRepository stockRepository;
  @Autowired ObjectMapper objectMapper;
  @Autowired RestTemplate restTemplate;
  @Autowired RabbitMQProducer producer;

  @Value("${rabbitmq.queue.event.updated.name}")
  private String eventUpdatedQueue;

  @Value("${rabbitmq.queue.event.soldout.name}")
  private String eventSoldOutQueue;

  private static final String TOKEN_URL =
      "http://keycloak:8080/realms/bookstore/protocol/openid-connect/token";
  private static final String CATALOG_SERVICE_ID = "catalog-service";
  private static final String CATALOG_SERVICE_SECRET = "FD3bZqrV67ZGFktuQnX02qaPMuE3V71v";
  private static final String GRANT_TYPE = "client_credentials";
  private static final String BOOK_CONFIRMATION_URL =
      "http://catalog-service:10000/books/confirmation/";

  /**
   * Used by catalog-service: when the book is created, a stock entry is created with the new book
   * id and zero units available.
   *
   * @param bookId the book identifier.
   */
  public void addStock(int bookId) {
    if (stockRepository.findByBookId(bookId).isEmpty()) {
      Stock newStockEntry = new Stock();
      newStockEntry.setUnits(0);
      newStockEntry.setBookId(bookId);
      stockRepository.save(newStockEntry);

      LOGGER.info(String.format("Stock entry created for book with id %s.", bookId));
    } else throw new StockFoundException();
  }

  /**
   * Update book stock.
   *
   * @param bookId the book identifier.
   * @param units the units to add or remove from stock.
   * @return the {@link StockStatus}.
   */
  public StockStatus updateStock(int bookId, int units) {

    StockStatus status;

    try {
      if (bookExists(bookId) && stockRepository.findByBookId(bookId).isPresent()) {
        try {
          Stock updatedStock =
              updateAvailableUnits(stockRepository.findByBookId(bookId).get(), units);

          if (updatedStock.getUnits() > 0) {
            producer.sendMessage(eventUpdatedQueue, buildMessage(bookId));

            LOGGER.info(
                String.format(
                    "Stock updated - book with id %s have %s units",
                    bookId, updatedStock.getUnits()));

            status = StockStatus.UPDATED;
          } else {
            producer.sendMessage(eventSoldOutQueue, buildMessage(bookId));
            LOGGER.info(String.format("Stock updated - book with id %s is sold out", bookId));

            status = StockStatus.SOLD_OUT;
          }
        } catch (InsufficientStockException e) {
          LOGGER.error(e.getStackTrace());
          status = StockStatus.INSUFFICIENT_STOCK;
        }
      } else {
        LOGGER.error(
            "Book is not present in catalog. Please insert the book in Catalog Service before adding stock.");

        status = StockStatus.BOOK_NOT_FOUND;
      }
    } catch (JsonProcessingException e) {

      LOGGER.error("Error building message", e);
      status = StockStatus.MESSAGE_ERROR;
    }
    return status;
  }

  private Stock updateAvailableUnits(Stock stock, int units) {

    int updatedUnits = stock.getUnits() + units;

    if (updatedUnits >= 0) {
      stock.setUnits(updatedUnits);
      Stock updatedStock = stockRepository.save(stock);
      LOGGER.info(
          String.format(
              "Stock updated - book with id %s have %s units",
              updatedStock.getBookId(), updatedStock.getUnits()));
      return updatedStock;
    } else throw new InsufficientStockException();
  }

  public Stock getStockByBookId(int bookId) {

    return stockRepository.findByBookId(bookId).orElseThrow(StockNotFoundException::new);
  }

  private String buildMessage(int bookId) throws JsonProcessingException {

    Pair<Integer, Integer> pair = Pair.of(bookId, getStockByBookId(bookId).getUnits());

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
