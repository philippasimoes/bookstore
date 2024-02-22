package com.bookstore.stock_service.publisher;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class RabbitMQProducer {

    private final Logger LOGGER = LogManager.getLogger(RabbitMQProducer.class);

    @Value("${rabbitmq.exchange.name}")
    private String exchange;

    @Value("${rabbitmq.routing.key}")
    private String routingKey;

    @Autowired
    public RabbitTemplate rabbitTemplate;


    public void sendMessage(int book_id, int stock) {
        LOGGER.info("filipaaaaaaaaaaaaaaaaaaaaaaaa");
        Map<Integer, Integer> map = new HashMap<>();
        map.put(book_id, stock);
        rabbitTemplate.convertAndSend(exchange, routingKey, map);
    }
}
