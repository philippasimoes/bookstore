package com.bookstore.dummy_ctt.infrastructure.message.config;

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

  @Value("${rabbitmq.queue.event.delivered.name}")
  private String eventDeliveredQueue;

  @Value("${rabbitmq.queue.event.returned.name}")
  private String eventReturnedQueue;

  @Value("${rabbitmq.exchange.name}")
  private String exchange;

  @Value("${rabbitmq.delivered.routing.key}")
  private String deliveredRoutingKey;

  @Value("${rabbitmq.returned.routing.key}")
  private String returnedRoutingKey;

  @Bean
  public Queue eventDeliveredQueue() {
    return new Queue(eventDeliveredQueue);
  }

  @Bean
  public Queue eventReturnedQueue() {
    return new Queue(eventReturnedQueue);
  }

  @Bean
  public TopicExchange exchange() {
    return new TopicExchange(exchange);
  }

  @Bean
  public Binding updatedDeliveredBinding(@Qualifier("eventDeliveredQueue") Queue queue, TopicExchange exchange) {
    return BindingBuilder.bind(queue).to(exchange).with(deliveredRoutingKey);
  }

  @Bean
  public Binding updatedReturnedBinding(@Qualifier("eventReturnedQueue") Queue queue, TopicExchange exchange) {
    return BindingBuilder.bind(queue).to(exchange).with(returnedRoutingKey);
  }
}
