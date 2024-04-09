package com.bookstore.dummy_ctt.service;

import com.bookstore.dummy_ctt.infrastructure.message.publisher.RabbitMQProducer;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;

@Service
public class CttService {
  private static final Logger LOGGER = LogManager.getLogger(CttService.class);
  static ObjectMapper objectMapper = new ObjectMapper();
  @Autowired RabbitMQProducer producer;

  @Value("${rabbitmq.queue.event.delivered.name}")
  private String eventDeliveredQueue;

  public String generateTrackingCode() {
    return UUID.randomUUID().toString();
  }

  public void notifyOrderDelivered(int orderId) {
    DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
    LocalDateTime now = LocalDateTime.now();
    try {
      Pair<Integer, String> pair = Pair.of(orderId, dtf.format(now));
      producer.sendMessage(eventDeliveredQueue, objectMapper.writeValueAsString(pair));
    } catch (JsonProcessingException e) {
      LOGGER.error("Error building message", e);
    }
  }

  public void returnCollected(int returnId) {
    try {
      Pair<String, Integer> pair = Pair.of("returnId", returnId);
      producer.sendMessage(eventDeliveredQueue, objectMapper.writeValueAsString(pair));
    } catch (JsonProcessingException e) {
      LOGGER.error("Error building message", e);
    }
  }
}
