package com.bookstore.payment_service.infrastructure.message.publisher;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

/**
 * RabbitMQ producer class.
 *
 * @author Filipa Simões
 */
@Service
public class RabbitMQProducer {

  private static final Logger LOGGER = LogManager.getLogger(RabbitMQProducer.class);

  private final RabbitTemplate rabbitTemplate;

  public RabbitMQProducer(RabbitTemplate rabbitTemplate) {

    this.rabbitTemplate = rabbitTemplate;
  }

  public void sendMessage(String queueName, String message) {

    rabbitTemplate.convertAndSend(queueName, message);
    LOGGER.log(Level.INFO, "Message sent: queue {}, message {}", queueName, message);
  }
}
