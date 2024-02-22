package com.bookstore.catalog_service.consumer;

import com.bookstore.catalog_service.model.Book;
import com.bookstore.catalog_service.repository.BookRepository;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class RabbitMQConsumer {
    @Autowired
    BookRepository bookRepository;

   @RabbitListener(queues = "stock_queue")
    public void consume(Map<Integer, Integer> map) {

        for (int key : map.keySet()) {
            Book book = bookRepository.findById(key).get();
            book.setStockAvailable(map.get(key));
            bookRepository.save(book);
        }

    }
}
