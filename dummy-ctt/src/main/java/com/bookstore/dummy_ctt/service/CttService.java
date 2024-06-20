package com.bookstore.dummy_ctt.service;

import com.bookstore.dummy_ctt.infrastructure.message.publisher.RabbitMQProducer;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
public class CttService {
    private static final Logger LOGGER = LogManager.getLogger(CttService.class);

    @Autowired
    ObjectMapper objectMapper;
    @Autowired
    RabbitMQProducer producer;

    @Value("${rabbitmq.queue.event.delivered.name}")
    private String eventDeliveredQueue;

    @Value("${rabbitmq.queue.event.returned.name}")
    private String eventReturnedQueue;

    public CttService(ObjectMapper objectMapper, RabbitMQProducer producer) {

        this.objectMapper = objectMapper;
        this.producer = producer;
    }

    public String generateTrackingCode() {
        return UUID.randomUUID().toString();
    }

    public void notifyOrderDelivered(int orderId) {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
        LocalDateTime now = LocalDateTime.now();
        try {
            Map<String, String> map = new HashMap<>();
            map.put("orderId", String.valueOf(orderId));
            map.put("deliveredDate", dtf.format(now));
            producer.sendMessage(eventDeliveredQueue, objectMapper.writeValueAsString(map));
        } catch (JsonProcessingException e) {
            LOGGER.log(Level.ERROR, "Error building message", e);
        }
    }

    public void returnCollected(int returnId) {
        try {
            Map<String, Integer> map = new HashMap<>();
            map.put("returnId", returnId);

            producer.sendMessage(eventDeliveredQueue, objectMapper.writeValueAsString(map));
        } catch (JsonProcessingException e) {
            LOGGER.log(Level.ERROR, "Error building message", e);
        }
    }
}
