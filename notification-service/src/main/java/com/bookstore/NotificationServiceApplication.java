package com.bookstore;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
@EnableTransactionManagement
@EnableScheduling
@OpenAPIDefinition(
    info = @Info(title = "Notification Service API", description = "Notification Service API"))
public class NotificationServiceApplication {

  public static void main(String[] args) {

    SpringApplication.run(NotificationServiceApplication.class, args);
  }

  @Bean
  public RestTemplate getRestTemplate() {
    return new RestTemplate();
  }
}
