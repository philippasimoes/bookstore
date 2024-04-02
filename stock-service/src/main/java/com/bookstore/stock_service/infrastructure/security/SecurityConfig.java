package com.bookstore.stock_service.infrastructure.security;

import static org.springframework.security.config.Customizer.withDefaults;

import java.util.Collection;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;

/**
 * API security configuration.
 *
 * @author Filipa Simões
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

  @Bean
  public SecurityFilterChain configure(HttpSecurity http) throws Exception {

    return http
        .authorizeHttpRequests(
            authorize ->
                authorize
                    .requestMatchers(HttpMethod.POST, "/**")
                        .fullyAuthenticated()
                    .requestMatchers(HttpMethod.PUT, "/**")
                    .hasRole("ADMIN")
                    .anyRequest()
                    .permitAll())
        .sessionManagement(c -> c.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .oauth2Login(withDefaults())
        .oauth2ResourceServer(conf -> conf.jwt(withDefaults()))
        .build();
  }

  @Bean
  public JwtAuthenticationConverter jwtAuthenticationConverter() {
    Converter<Jwt, Collection<GrantedAuthority>> jwtGrantedAuthoritiesConverter =
        jwt -> {
          Map<String, Collection<String>> realmAccess = jwt.getClaim("realm_access");
          Collection<String> roles = realmAccess.get("roles");
          return roles.stream()
              .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
              .collect(Collectors.toList());
        };

    var jwtAuthenticationConverter = new JwtAuthenticationConverter();
    jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(jwtGrantedAuthoritiesConverter);

    return jwtAuthenticationConverter;
  }

  @Bean
  JwtDecoder jwtDecoder() {
    return NimbusJwtDecoder.withJwkSetUri(
            "http://keycloak:8080/realms/bookstore/protocol/openid-connect/certs")
        .build();
  }
}
