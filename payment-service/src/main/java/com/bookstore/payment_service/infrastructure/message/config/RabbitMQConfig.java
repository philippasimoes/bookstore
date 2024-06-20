package com.bookstore.payment_service.infrastructure.message.config;

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

  @Value("${rabbitmq.queue.event.paid.name}")
  private String eventPaidQueue;

  @Value("${rabbitmq.queue.event.refunded.name}")
  private String eventRefundedQueue;

  @Value("${rabbitmq.exchange.name}")
  private String exchange;

  @Value("${rabbitmq.payment.routing.key}")
  private String paymentRoutingKey;

  @Value("${rabbitmq.refunded.routing.key}")
  private String refundedRoutingKey;

  @Bean
  public Queue eventPaidQueue() {
    return new Queue(eventPaidQueue);
  }

  @Bean
  public Queue eventRefundedQueue() {
    return new Queue(eventRefundedQueue);
  }

  @Bean
  public TopicExchange exchange() {
    return new TopicExchange(exchange);
  }

  @Bean
  public Binding updatedPaymentBinding(@Qualifier("eventPaidQueue") Queue queue, TopicExchange exchange) {
    return BindingBuilder.bind(queue).to(exchange).with(paymentRoutingKey);
  }

  @Bean
  public Binding updatedRefundedBinding(@Qualifier("eventRefundedQueue") Queue queue, TopicExchange exchange) {
    return BindingBuilder.bind(queue).to(exchange).with(refundedRoutingKey);
  }
}
