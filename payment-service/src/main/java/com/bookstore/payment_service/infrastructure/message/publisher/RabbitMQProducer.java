package com.bookstore.payment_service.infrastructure.message.publisher;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * RabbitMQ producer class.
 *
 * @author Filipa Simões
 */
@Service
public class RabbitMQProducer {

  private final Logger LOGGER = LogManager.getLogger(RabbitMQProducer.class);

  @Autowired public RabbitTemplate rabbitTemplate;

  public void sendMessage(String queueName, String message) {

    rabbitTemplate.convertAndSend(queueName, message);
    LOGGER.info(String.format("Message sent: queue %s, message %s", queueName, message));
  }
}
