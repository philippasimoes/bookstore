package com.bookstore.stock_service.infrastructure.publisher;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;


@Service
public class RabbitMQProducer {

    private final Logger LOGGER = LoggerFactory.getLogger(RabbitMQProducer.class);

    @Value("${rabbitmq.exchange.name}")
    private String exchange;

    @Value("${rabbitmq.routing.key}")
    private String routingKey;

    @Autowired
    public RabbitTemplate rabbitTemplate;

    public void sendMessage(String queueName, String message) {

        rabbitTemplate.convertAndSend(queueName, message);
        LOGGER.info(String.format("Message sent: queue %s, message %s", queueName, message));
    }

}
