package com.bookstore.payment_service.utils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.AttributeConverter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class HashMapConverter implements AttributeConverter<Map<String, Object>, String> {

  private static final Logger logger = LogManager.getLogger(HashMapConverter.class);

  private final ObjectMapper objectMapper = new ObjectMapper();

  @Override
  public String convertToDatabaseColumn(Map<String, Object> customerInfo) {

    String customerInfoJson = null;
    try {
      customerInfoJson = objectMapper.writeValueAsString(customerInfo);
    } catch (final JsonProcessingException e) {
      logger.error("JSON writing error", e);
    }

    return customerInfoJson;
  }

  @Override
  public Map<String, Object> convertToEntityAttribute(String customerInfoJSON) {

    Map<String, Object> customerInfo = null;
    try {
      customerInfo =
          objectMapper.readValue(customerInfoJSON, new TypeReference<HashMap<String, Object>>() {});
    } catch (final IOException e) {
      logger.error("JSON reading error", e);
    }

    return customerInfo;
  }
}
