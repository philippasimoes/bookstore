package com.bookstore.catalog_service.consumer;

import com.bookstore.catalog_service.model.Book;
import com.bookstore.catalog_service.repository.BookRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class RabbitMQConsumer {
    private static final Logger LOGGER = LoggerFactory.getLogger(RabbitMQConsumer.class);

    @Autowired
    BookRepository bookRepository;

    @RabbitListener(queues = {"${rabbitmq.queue.name}"})
    public void consume(Map<Integer, Integer> map) {
        //LOGGER.info(String.format("Received message -> %s", message));

        for (int key : map.keySet()) {
            Book book = bookRepository.findById(key).get();
            book.setStockAvailable(map.get(key));
            bookRepository.save(book);
        }

    }
}
