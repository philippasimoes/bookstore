package com.bookstore.catalog_service.infrastructure.message;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.annotation.RabbitListenerConfigurer;
import org.springframework.amqp.rabbit.listener.RabbitListenerEndpointRegistrar;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.handler.annotation.support.DefaultMessageHandlerMethodFactory;

@Configuration
public class RabbitMQConfig implements RabbitListenerConfigurer {

    @Value("${rabbitmq.queue.event.updated.name}")
    private String eventUpdatedQueue;
    @Value("${rabbitmq.queue.event.soldout.name}")
    private String eventSoldOutQueue;
    @Value("${rabbitmq.exchange.name}")
    private String exchange;
    @Value("${rabbitmq.routing.key}")
    private String routingKey;


    @Bean
    public Queue eventUpdatedQueue(){
        return new Queue(eventUpdatedQueue);
    }
    @Bean
    public Queue eventSoldOutQueue(){
        return new Queue(eventSoldOutQueue);
    }

    @Bean
    public TopicExchange exchange(){
        return new TopicExchange(exchange);
    }

    @Bean
    public Binding updatedBinding(@Qualifier("eventUpdatedQueue") Queue queue, TopicExchange exchange){
        return BindingBuilder.bind(queue)
                .to(exchange)
                .with(routingKey);
    }

    @Bean
    public Binding soldOutBinding(@Qualifier("eventSoldOutQueue") Queue queue, TopicExchange exchange){
        return BindingBuilder.bind(queue)
                .to(exchange)
                .with(routingKey);
    }

    @Bean
    MappingJackson2MessageConverter jackson2Converter() {
        MappingJackson2MessageConverter converter = new MappingJackson2MessageConverter();
        return converter;
    }

    /**
     * give Jackson ObjetMapper to RabbitMq for JSON Mapping
     *
     * @return
     */
    @Bean
    DefaultMessageHandlerMethodFactory jsonMessageHandlerMethod() {
        DefaultMessageHandlerMethodFactory factory = new DefaultMessageHandlerMethodFactory();
        factory.setMessageConverter(jackson2Converter());

        return factory;
    }

    @Override
    public void configureRabbitListeners(RabbitListenerEndpointRegistrar rabbitListenerEndpointRegistrar) {
        rabbitListenerEndpointRegistrar.setMessageHandlerMethodFactory(jsonMessageHandlerMethod());
    }
}
