package com.bookstore;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.OAuthFlow;
import io.swagger.v3.oas.annotations.security.OAuthFlows;
import io.swagger.v3.oas.annotations.security.OAuthScope;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.security.SecuritySchemes;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
@EnableTransactionManagement
@EnableDiscoveryClient
@OpenAPIDefinition(info = @Info(title = "Stock Service API", description = "Stock Service API"))
@SecuritySchemes({
  @SecurityScheme(
      name = "admin-only",
      scheme = "Bearer",
      type = SecuritySchemeType.OAUTH2,
      flows =
          @OAuthFlows(
              authorizationCode =
                  @OAuthFlow(
                      authorizationUrl = "${springdoc.oAuthFlow.authorizationUrl}",
                      tokenUrl = "${springdoc.oAuthFlow.tokenUrl}",
                      scopes = {
                        @OAuthScope(name = "openid"),
                        @OAuthScope(name = "profile"),
                        @OAuthScope(name = "email"),
                        @OAuthScope(name = "roles")
                      })))
})
public class StockServiceApplication {

  public static void main(String[] args) {
    SpringApplication.run(StockServiceApplication.class, args);
  }

  @LoadBalanced
  @Bean
  public RestTemplate restTemplate(RestTemplateBuilder builder) {
    return builder.build();
  }
}
