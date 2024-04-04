package com.bookstore.order_service.infrastructure.message;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.annotation.RabbitListenerConfigurer;
import org.springframework.amqp.rabbit.listener.RabbitListenerEndpointRegistrar;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.handler.annotation.support.DefaultMessageHandlerMethodFactory;

/**
 * RabbitMQ configuration class.
 *
 * @author Filipa Simões
 */
@Configuration
public class RabbitMQConfig implements RabbitListenerConfigurer {

  @Value("${rabbitmq.queue.event.shipped.name}")
  private String eventShippedQueue;

  @Value("${rabbitmq.queue.event.paid.name}")
  private String eventPaidQueue;

  @Value("${rabbitmq.queue.event.delivered.name}")
  private String eventDeliveredQueue;

  @Value("${rabbitmq.order.exchange.name}")
  private String exchange;

  @Value("${rabbitmq.shipping.routing.key}")
  private String shippingRoutingKey;

  @Value("${rabbitmq.payment.routing.key}")
  private String paymentRoutingKey;

  @Value("${rabbitmq.delivered.routing.key}")
  private String deliveredRoutingKey;

  @Bean
  public Queue eventShippedQueue() {
    return new Queue(eventShippedQueue);
  }

  @Bean
  public Queue eventPaidQueue() {
    return new Queue(eventPaidQueue);
  }

  @Bean
  public Queue eventDeliveredQueue() {
    return new Queue(eventDeliveredQueue);
  }

  @Bean
  public TopicExchange exchange() {
    return new TopicExchange(exchange);
  }

  @Bean
  public Binding updatedShippingBinding(
      @Qualifier("eventShippedQueue") Queue queue, TopicExchange exchange) {
    return BindingBuilder.bind(queue).to(exchange).with(shippingRoutingKey);
  }

  @Bean
  public Binding updatedPaymentBinding(
      @Qualifier("eventPaidQueue") Queue queue, TopicExchange exchange) {
    return BindingBuilder.bind(queue).to(exchange).with(paymentRoutingKey);
  }

  @Bean
  public Binding updatedDeliveredBinding(
      @Qualifier("eventDeliveredQueue") Queue queue, TopicExchange exchange) {
    return BindingBuilder.bind(queue).to(exchange).with(deliveredRoutingKey);
  }

  @Bean
  MappingJackson2MessageConverter jackson2Converter() {

    return new MappingJackson2MessageConverter();
  }

  @Bean
  DefaultMessageHandlerMethodFactory jsonMessageHandlerMethod() {
    DefaultMessageHandlerMethodFactory factory = new DefaultMessageHandlerMethodFactory();
    factory.setMessageConverter(jackson2Converter());

    return factory;
  }

  @Override
  public void configureRabbitListeners(
      RabbitListenerEndpointRegistrar rabbitListenerEndpointRegistrar) {
    rabbitListenerEndpointRegistrar.setMessageHandlerMethodFactory(jsonMessageHandlerMethod());
  }
}
