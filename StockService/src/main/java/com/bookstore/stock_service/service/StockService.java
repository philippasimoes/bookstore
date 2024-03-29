package com.bookstore.stock_service.service;

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
import java.util.Optional;

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

/**
 * Stock service class.
 *
 * @author Filipa Simões
 */
@Service
@Transactional
public class StockService {
  private static final Logger LOGGER = LogManager.getLogger(StockService.class);

  private static final String TOKEN_URL =
          "http://keycloak:8080/realms/bookstore/protocol/openid-connect/token";
  private static final String CATALOG_SERVICE_ID = "catalog-service";
  private static final String CATALOG_SERVICE_SECRET = "FD3bZqrV67ZGFktuQnX02qaPMuE3V71v";
  private static final String GRANT_TYPE = "client_credentials";
  private static final String BOOK_CONFIRMATION_URL =
          "http://catalog-service:10000/books/confirmation/";

  @Autowired StockRepository stockRepository;
  @Autowired ObjectMapper objectMapper;
  @Autowired RestTemplate restTemplate;
  @Autowired RabbitMQProducer producer;

  @Value("${rabbitmq.queue.event.updated.name}")
  private String eventUpdatedQueue;

  @Value("${rabbitmq.queue.event.soldout.name}")
  private String eventSoldOutQueue;


  /**
   * Used by catalog-service: when the book is created, a stock entry is created with the new book
   * id and zero units available.
   *
   * @param bookId the book identifier.
   */
  public void addStock(int bookId) {
    if (stockRepository.findByBookId(bookId).isEmpty()) {
      Stock newStockEntry = new Stock();
      newStockEntry.setAvailableUnits(0);
      newStockEntry.setPendingUnits(0);
      newStockEntry.setBookId(bookId);
      stockRepository.save(newStockEntry);

      LOGGER.info(String.format("Stock entry created for book with id %s.", bookId));
    } else throw new StockFoundException();
  }

  /**
   * Update book stock. Possible scenarios: 1) Book units are added by an admin (bookstore received
   * units from the publisher) and 2) Books are added to an order, and we need to remove them from the
   * available stock and add them to the pending stock;
   *
   * @param bookId the book identifier.
   * @param units the units to add or remove from stock.
   * @return the {@link StockStatus}.
   */
  public StockStatus updateStock(int bookId, int units) {

    Optional<Stock> stock = stockRepository.findByBookId(bookId);

    try {
      if (bookExists(bookId) && stock.isPresent()) {

        // add books to stock
        if (units > 0) return addAvailableUnits(stock.get(), units);
        // remove books from stock
        else return removeAvailableUnitsAndUpdatePendingUnits(stock.get(), units);

      } else {
        LOGGER.error(
            "Book is not present in catalog. Please insert the book in Catalog Service before adding stock.");
        return StockStatus.BOOK_NOT_FOUND;
      }

    } catch (JsonProcessingException e) {
      LOGGER.error("Error building message", e);
      return StockStatus.MESSAGE_ERROR;
    }
  }


  /**
   * Add units to available units.
   *
   * @param stock the stock entry.
   * @param units the units to be added.
   * @return UPDATED {@link StockStatus}
   * @throws JsonProcessingException when there's an error with messaging service.
   */
  private StockStatus addAvailableUnits(Stock stock, int units) throws JsonProcessingException {

    int updatedUnits = stock.getAvailableUnits() + units;
    stock.setAvailableUnits(updatedUnits);
    Stock updatedStock = stockRepository.save(stock);

    // sending a message to catalog service to update the book available units
    producer.sendMessage(eventUpdatedQueue, buildMessage(stock.getBookId()));

    LOGGER.info(
        String.format(
            "Stock updated - book with id %s have %s units",
            updatedStock.getBookId(), updatedStock.getAvailableUnits()));

    return StockStatus.UPDATED;
  }

  /**
   * Book units are added to an order, and we need to remove the units from available units (and
   * they are not showing for the customer) and add them to the pending units (because the sale is
   * not completed).
   *
   * @param stock the stock entry.
   * @param units the units to be removed from the available units and added to the pending units.
   * @return INSUFFICIENT_STOCK, UPDATED or SOLD_OUT {@link StockStatus} according to the number of
   *     available units.
   * @throws JsonProcessingException when there's an error with messaging service.
   */
  private StockStatus removeAvailableUnitsAndUpdatePendingUnits(Stock stock, int units)
      throws JsonProcessingException {

    if (units > stock.getAvailableUnits()) {
      return StockStatus.INSUFFICIENT_STOCK;
    } else {
      int updatedUnits =
          stock.getAvailableUnits() + units; // because the units is a negative number in this case
      stock.setAvailableUnits(updatedUnits);
      stock.setPendingUnits(stock.getPendingUnits() + units);
      Stock updatedStock = stockRepository.save(stock);

      LOGGER.info(
          String.format(
              "Stock updated - book with id %s have %s available units. Pending units: %s",
              updatedStock.getBookId(),
              updatedStock.getAvailableUnits(),
              updatedStock.getPendingUnits()));

      // sending a message to catalog service (the queue is chosen according to the number of
      // available units)
      if (updatedStock.getAvailableUnits() > 0) {
        producer.sendMessage(eventUpdatedQueue, buildMessage(stock.getBookId()));
        return StockStatus.UPDATED;
      } else {
        producer.sendMessage(eventSoldOutQueue, buildMessage(stock.getBookId()));
        LOGGER.info(
            String.format("Stock updated - book with id %s is sold out", stock.getBookId()));

        return StockStatus.SOLD_OUT;
      }
    }
  }

  /**
   * Removing the number o units from pending units and add them to the available units - used when
   * order is cancelled or items are removed from the order.
   *
   * @param bookId the book identifier.
   * @param units the number of units to remove from pending units and added to the available units.
   * @return UPDATED {@link StockStatus}
   */
  public StockStatus removePendingUnitsAndUpdateAvailableUnits(int bookId, int units) {
    // removing units from pending units
    removePendingUnits(bookId, units);

    // adding units to available units again
    try {
      addAvailableUnits(stockRepository.findByBookId(bookId).get(), units);
    } catch (JsonProcessingException e) {
      LOGGER.error("Error building message", e);
      return StockStatus.MESSAGE_ERROR;
    }
    return StockStatus.UPDATED;
  }

  /**
   * Order is shipped and the books are not in the physical stock - removing the units from pending
   * units.
   *
   * @param bookId the book identifier.
   * @param units the book units to be removed from pending units.
   */
  public void removePendingUnits(int bookId, int units) {
    Optional<Stock> stock = stockRepository.findByBookId(bookId);
    if (stock.isPresent()) {
      stock.get().setPendingUnits(stock.get().getPendingUnits() - units);
      stockRepository.save(stock.get());
    } else throw new StockNotFoundException();
  }

  /**
   * Get the book available units.
   *
   * @param bookId the book identifier.
   * @return the number of available units.
   */
  public int getAvailableUnitsByBookId(int bookId) {

    if (stockRepository.findByBookId(bookId).isPresent()) {
      return stockRepository.findByBookId(bookId).get().getAvailableUnits();
    } else throw new StockFoundException();
  }

  /**
   * Build a message to be sent by rabbitmq to catalog service with the book available units.
   *
   * @param bookId the book identifier.
   * @return the message containing the book identifier and the available units.
   * @throws JsonProcessingException when there's an error with messaging service.
   */
  private String buildMessage(int bookId) throws JsonProcessingException {

    Pair<Integer, Integer> pair = Pair.of(bookId, getAvailableUnitsByBookId(bookId));

    return objectMapper.writeValueAsString(pair);
  }

  /**
   * Authenticate in catalog service.
   *
   * @return a jwt token.
   */
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

  /**
   * Validate if a book is registered in the catalog service.
   *
   * @param bookId the book identifier.
   * @return true if the book exists.
   * @throws JsonProcessingException when there's an error reading the jwt token.
   */
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
