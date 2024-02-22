package com.bookstore.stock_service.publisher;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class RabbitMQProducer {

    @Value("${rabbitmq.exchange.name}")
    private String exchange;

    @Value("${rabbitmq.routing.key}")
    private String routingKey;

    @Autowired
    public RabbitTemplate rabbitTemplate;


    public void sendMessage(int book_id, int stock) {

        Map<Integer, Integer> map = new HashMap<>();
        map.put(book_id, stock);
        rabbitTemplate.convertAndSend(exchange, routingKey, map);
    }
}
