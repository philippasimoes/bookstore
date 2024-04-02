package com.bookstore.catalog_service.infrastructure.security;

import static org.springframework.security.config.Customizer.withDefaults;

import java.util.Collection;
import java.util.Map;
import java.util.stream.Collectors;

import jakarta.servlet.Filter;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
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
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.session.SessionManagementFilter;

/**
 * API security configuration.
 *
 * @author Filipa Simões
 */
@Configuration
@EnableWebSecurity
@Profile(value = {"dev", "prod"})
public class SecurityConfig {

  @Bean
  public SecurityFilterChain configure(HttpSecurity http) throws Exception {

    return http.authorizeHttpRequests(
            authorize ->
                authorize
                    .requestMatchers(HttpMethod.POST, "/books")
                    .hasRole("ADMIN")
                    .requestMatchers(HttpMethod.PATCH, "/books/**")
                    .hasRole("ADMIN")
                    .requestMatchers(HttpMethod.POST, "/authors/**")
                    .hasRole("ADMIN")
                    .requestMatchers(HttpMethod.PUT, "/authors/**")
                    .hasRole("ADMIN")
                    .requestMatchers(HttpMethod.POST, "/lang/**")
                    .hasRole("ADMIN")
                    .requestMatchers(HttpMethod.POST, "/tags/**")
                    .hasRole("ADMIN")
                    .requestMatchers(HttpMethod.GET, "/confirmation/**")
                    .fullyAuthenticated()
                    .anyRequest()
                    .permitAll())
        .sessionManagement(c -> c.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED))
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
