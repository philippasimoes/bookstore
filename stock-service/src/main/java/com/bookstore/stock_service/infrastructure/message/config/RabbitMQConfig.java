package com.bookstore.stock_service.infrastructure.message.config;

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

  @Value("${rabbitmq.queue.event.updated.name}")
  private String eventUpdatedQueue;

  @Value("${rabbitmq.queue.event.soldout.name}")
  private String eventSoldOutQueue;

  @Value("${rabbitmq.exchange.name}")
  private String exchange;

  @Value("${rabbitmq.routing.key}")
  private String routingKey;

  @Bean
  public Queue eventUpdatedQueue() {
    return new Queue(eventUpdatedQueue);
  }

  @Bean
  public Queue eventSoldOutQueue() {
    return new Queue(eventSoldOutQueue);
  }

  @Bean
  public TopicExchange exchange() {
    return new TopicExchange(exchange);
  }

  @Bean
  public Binding updatedBinding(
      @Qualifier("eventUpdatedQueue") Queue queue, TopicExchange exchange) {
    return BindingBuilder.bind(queue).to(exchange).with(routingKey);
  }

  @Bean
  public Binding soldOutBinding(
      @Qualifier("eventSoldOutQueue") Queue queue, TopicExchange exchange) {
    return BindingBuilder.bind(queue).to(exchange).with(routingKey);
  }
}
