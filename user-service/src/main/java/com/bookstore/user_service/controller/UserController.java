package com.bookstore.user_service.controller;

import com.bookstore.user_service.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/user")
public class UserController {

  private final UserService userService;

  public UserController(UserService userService) {

    this.userService = userService;
  }

  @GetMapping("/email/{user_id}")
  public ResponseEntity<String> getUserEmail(@PathVariable("user_id") int userId) {
    return ResponseEntity.ok(userService.getUserEmail(userId));
  }

  @GetMapping("/{user_id}")
  public ResponseEntity<Boolean> userExists(@PathVariable("user_id") int userId) {
    return ResponseEntity.ok(userService.userExists(userId));
  }
}
