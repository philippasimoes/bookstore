package com.bookstore.user_service.controller;

import com.bookstore.user_service.exception.UserAlreadyExistAuthenticationException;
import com.bookstore.user_service.infrastructure.security.TokenService;
import com.bookstore.user_service.model.dto.AuthenticationDto;
import com.bookstore.user_service.model.dto.LoginResponseDto;
import com.bookstore.user_service.model.dto.UserDto;
import com.bookstore.user_service.service.UserService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Authentication Rest Controller.
 *
 * @author Filipa Simões
 */
@RestController
@RequestMapping(value = "/auth")
@Tag(name = "Register and authentication endpoints")
public class AuthenticationController {

  @Autowired TokenService tokenService;
  @Autowired UserService userService;

  @PostMapping("/login")
  public ResponseEntity<LoginResponseDto> login(
      @RequestBody @Valid AuthenticationDto authenticationDto) {

    UserDetails user = userService.findByUsername(authenticationDto.username());
    UsernamePasswordAuthenticationToken usernamePassword =
        new UsernamePasswordAuthenticationToken(
            user.getUsername(), user.getPassword(), user.getAuthorities());

    String accessToken = tokenService.generateAccessToken(usernamePassword);

    return ResponseEntity.ok(new LoginResponseDto(accessToken));
  }

  @PostMapping("/register")
  public ResponseEntity<UserDto> register(@RequestBody @Valid UserDto userDto) {

    try {
      return ResponseEntity.ok(userService.addNewUser(userDto));
    } catch (UserAlreadyExistAuthenticationException e) {
      return ResponseEntity.badRequest().build();
    }
  }
}
