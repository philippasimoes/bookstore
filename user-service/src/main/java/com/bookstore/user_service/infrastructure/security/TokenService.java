package com.bookstore.user_service.infrastructure.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

@Service
public class TokenService {
  @Value("${api.security.token.secret}")
  private String secret;

  public String generateAccessToken(UsernamePasswordAuthenticationToken user) {
    try {
      Algorithm algorithm = Algorithm.HMAC256(secret);
      return JWT.create()
          .withIssuer("catalog-service")
          .withSubject(user.getName())
          .withExpiresAt(genExpirationDate())
          .sign(algorithm);
    } catch (JWTCreationException e) {
      throw new RuntimeException("Error while generating token", e);
    }
  }

  public String validateToken(String token) {
    try {
      Algorithm algorithm = Algorithm.HMAC256(secret);
      return JWT.require(algorithm)
          .withIssuer("catalog-service")
          .build()
          .verify(token)
          .getSubject();
    } catch (JWTVerificationException e) {
      e.getStackTrace();
      return "";
    }
  }

  private Instant genExpirationDate() {
    return LocalDateTime.now().plusHours(2).toInstant(ZoneOffset.UTC);
  }
}
