package com.bookstore.shipping_service.infrastructure.message.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * RabbitMQ configuration class.
 *
 * @author Filipa Simões
 */
@Configuration
public class RabbitMQConfig {

  @Value("${rabbitmq.queue.event.shipped.name}")
  private String eventShippedQueue;

  @Value("${rabbitmq.exchange.name}")
  private String exchange;

  @Value("${rabbitmq.routing.key}")
  private String routingKey;

  @Bean
  public Queue eventShippedQueue() {
    return new Queue(eventShippedQueue);
  }

  @Bean
  public TopicExchange exchange() {
    return new TopicExchange(exchange);
  }

  @Bean
  public Binding updatedBinding(
          @Qualifier("eventShippedQueue") Queue queue, TopicExchange exchange) {
    return BindingBuilder.bind(queue).to(exchange).with(routingKey);
  }
}
