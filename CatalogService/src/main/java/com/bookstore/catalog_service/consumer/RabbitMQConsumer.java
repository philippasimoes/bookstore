package com.bookstore.catalog_service.consumer;

import com.bookstore.catalog_service.model.Book;
import com.bookstore.catalog_service.repository.BookRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.util.Map;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RabbitMQConsumer {

  @Autowired
  BookRepository bookRepository;

  private final Logger LOGGER = LogManager.getLogger(RabbitMQConsumer.class);

  @RabbitListener(queues = "stock_queue")
  public void consume(Map<Integer, Integer> map) {
    LOGGER.info("filipa helllowwwwwwwwwwwwwwwwwwwwwwwwww");
    for (int key : map.keySet()) {
      Book book = bookRepository.findById(key).get();
      book.setStockAvailable(map.get(key));
      bookRepository.save(book);
    }

  }
}
