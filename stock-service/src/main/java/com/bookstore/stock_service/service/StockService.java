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

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.apache.logging.log4j.Level;
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

      LOGGER.log(Level.INFO, "Stock entry created for book with id {}.", bookId);
    } else throw new StockFoundException();
  }

  /**
   * Update book stock. Possible scenarios: 1) Book units are added by an admin (bookstore received
   * units from the publisher) and 2) Books are added to an order, and we need to remove them from
   * the available stock and add them to the pending stock;
   *
   * @param bookId the book identifier.
   * @param units the units to add or remove from stock.
   * @return the {@link StockStatus}.
   */
  public StockStatus updateStock(int bookId, int units) {

    Optional<Stock> stock = stockRepository.findByBookId(bookId);

    try {
      if (stock.isPresent()) {

        // add books to stock
        if (units > 0) return addAvailableUnits(stock.get(), units);
        // remove books from stock
        else return removeAvailableUnitsAndUpdatePendingUnits(stock.get(), units);

      } else {
        LOGGER.log(
            Level.ERROR,
            "Book is not present in catalog. Please insert the book in Catalog Service before adding stock.");
        return StockStatus.BOOK_NOT_FOUND;
      }

    } catch (JsonProcessingException e) {
      LOGGER.log(Level.ERROR, "Error building message", e);
      return StockStatus.MESSAGE_ERROR;
    }
  }

  /**
   * Removing the number of units from pending units and add them to the available units - used when
   * order is cancelled or items are removed from the order.
   *
   * @param bookId the book identifier.
   * @param units the number of units to remove from pending units and added to the available units.
   * @return UPDATED {@link StockStatus}
   */
  public StockStatus removePendingUnitsAndUpdateAvailableUnits(int bookId, int units) {
    // removing units from pending units
    removePendingUnits(bookId, units);

    Optional<Stock> stock = stockRepository.findByBookId(bookId);
    // adding units to available units again
    if (stock.isPresent()) {
      try {
        addAvailableUnits(stock.get(), units);
      } catch (JsonProcessingException e) {
        LOGGER.log(Level.ERROR, "Error building message", e);
        return StockStatus.MESSAGE_ERROR;
      }
      return StockStatus.UPDATED;
    } else throw new StockNotFoundException();
  }

  /**
   * Order is shipped and the books are not in the physical stock - removing the units from pending
   * units.
   *
   * @param bookId the book identifier.
   * @param units the book units to be removed from pending units.
   */
  public StockStatus removePendingUnits(int bookId, int units) {
    Optional<Stock> stock = stockRepository.findByBookId(bookId);
    if (stock.isPresent()) {
      stock.get().setPendingUnits(stock.get().getPendingUnits() - units);
      stockRepository.save(stock.get());
      return StockStatus.UPDATED;
    } else throw new StockNotFoundException();
  }

  /**
   * Get the book available units.
   *
   * @param bookId the book identifier.
   * @return the number of available units.
   */
  public int getAvailableUnitsByBookId(int bookId) {

    Optional<Stock> stock = stockRepository.findByBookId(bookId);

    if (stock.isPresent()) {
      return stock.get().getAvailableUnits();
    } else throw new StockNotFoundException();
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

    LOGGER.log(
        Level.INFO,
        "Stock updated - book with id {} have {} units",
        updatedStock.getBookId(),
        updatedStock.getAvailableUnits());

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
      stock.setPendingUnits(stock.getPendingUnits() + Math.abs(units));
      Stock updatedStock = stockRepository.save(stock);

      LOGGER.log(
          Level.INFO,
          "Stock updated - book with id {} have {} available units. Pending units: {}",
          updatedStock.getBookId(),
          updatedStock.getAvailableUnits(),
          updatedStock.getPendingUnits());

      // sending a message to catalog service (the queue is chosen according to the number of
      // available units)
      if (updatedStock.getAvailableUnits() > 0) {
        producer.sendMessage(eventUpdatedQueue, buildMessage(stock.getBookId()));
        return StockStatus.UPDATED;
      } else {
        producer.sendMessage(eventSoldOutQueue, buildMessage(stock.getBookId()));
        LOGGER.log(Level.INFO, "Stock updated - book with id {} is sold out", stock.getBookId());

        return StockStatus.SOLD_OUT;
      }
    }
  }

  /**
   * Build a message to be sent by rabbitmq to catalog service with the book available units.
   *
   * @param bookId the book identifier.
   * @return the message containing the book identifier and the available units.
   * @throws JsonProcessingException when there's an error with messaging service.
   */
  private String buildMessage(int bookId) throws JsonProcessingException {

    Map<String, Integer> map = new HashMap<>();
    map.put("bookId", bookId);
    map.put("availableUnits",getAvailableUnitsByBookId(bookId));

    return objectMapper.writeValueAsString(map);
  }
}
