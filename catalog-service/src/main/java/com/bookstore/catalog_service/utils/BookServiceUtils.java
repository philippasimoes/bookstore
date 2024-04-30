package com.bookstore.catalog_service.utils;

import com.bookstore.catalog_service.model.entity.Book;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.core.io.Resource;
import org.springframework.data.util.Pair;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

public final class BookServiceUtils {
  private static final Logger LOGGER = LogManager.getLogger(BookServiceUtils.class);
  private static final String STOCK_CREATION_URL = "http://stock-service:10001/stock/book/";
  private static final String TOKEN_URL =
      "http://keycloak:8080/realms/bookstore/protocol/openid-connect/token";
  private static final String STOCK_SERVICE_ID = "stock-service";
  private static final String STOCK_SERVICE_SECRET = "vzFYf3wn4yItcZ35vKJZf63VmYC4TOSx";
  private static final String GRANT_TYPE = "client_credentials";

  private static final ObjectMapper objectMapper = new ObjectMapper();

  private BookServiceUtils() {}

  /**
   * Read message received from RabbitMq queues.
   *
   * @param message the message from StockService.
   */
  public static Pair<Integer, Integer> readMessage(String message) {

    LOGGER.log(Level.INFO, "Received Message: {}", message);
    try {
      return objectMapper.readValue(message, Pair.class);
    } catch (JsonProcessingException e) {
      LOGGER.log(Level.ERROR, "Could not read pair from message", e);
      return null;
    }
  }

  /**
   * Create a new stock entry with the specified book identifier. Note that this method should only
   * be called when creating a new book because the stock is created with zero units.
   *
   * @param bookId the book identifier.
   */
  public static void createStock(int bookId) {

    RestTemplate restTemplate = new RestTemplate();
    try {
      HttpHeaders headers = new HttpHeaders();
      headers.set(
          "Authorization",
          "Bearer "
              + objectMapper
                  .readValue(authenticateAndGetJwtToken(), Map.class)
                  .get("access_token"));

      HttpEntity<String> requestEntity = new HttpEntity<>(headers);

            restTemplate.exchange(
                STOCK_CREATION_URL + bookId, HttpMethod.POST, requestEntity, String.class);

      //restTemplate.postForEntity(STOCK_CREATION_URL + bookId, requestEntity, Object.class);
    } catch (JsonProcessingException e) {
      LOGGER.log(Level.ERROR, e.getMessage());
    }
  }

  public static void exportReport(
      HttpServletResponse response, List<Book> bookList, Resource resource) {

    try {
      InputStream inputStream = resource.getInputStream();
      JasperReport jasperReport = JasperCompileManager.compileReport(inputStream);
      JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(bookList);
      JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, null, dataSource);

      response.setContentType(MediaType.APPLICATION_PDF_VALUE);
      response.setHeader("Content-Disposition", "inline; filename=books.pdf");

      final OutputStream outStream = response.getOutputStream();
      JasperExportManager.exportReportToPdfStream(jasperPrint, outStream);

    } catch (IOException | JRException e) {
      LOGGER.log(Level.ERROR, "Something wrong with report generation.", e);
    }
  }

  /**
   * Authenticate catalog service in stock service with client id and client secret.
   *
   * @return the Jwt.
   */
  public static String authenticateAndGetJwtToken() {

    HttpHeaders authHeaders = new HttpHeaders();
    authHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

    String requestBody =
        "grant_type="
            + GRANT_TYPE
            + "&client_id="
            + STOCK_SERVICE_ID
            + "&client_secret="
            + STOCK_SERVICE_SECRET;

    HttpEntity<String> requestEntity = new HttpEntity<>(requestBody, authHeaders);

    RestTemplate keycloakRestTemplate = new RestTemplate();

    ResponseEntity<String> response =
        keycloakRestTemplate.postForEntity(TOKEN_URL, requestEntity, String.class);

    if (response.getStatusCode().is2xxSuccessful()) {
      LOGGER.log(Level.INFO, "Authentication successful.");
      return response.getBody();
    } else {

      LOGGER.log(Level.ERROR, "Authentication failure. Status: {}.", response.getStatusCode());
      return null;
    }
  }
}
