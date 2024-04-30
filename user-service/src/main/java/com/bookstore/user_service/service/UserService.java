package com.bookstore.user_service.service;

import com.bookstore.user_service.exception.ResourceNotFoundException;
import com.bookstore.user_service.exception.UserAlreadyExistAuthenticationException;
import com.bookstore.user_service.model.dto.UserDto;
import com.bookstore.user_service.model.entity.User;
import com.bookstore.user_service.model.mapper.UserMapper;
import com.bookstore.user_service.repository.UserRepository;
import jakarta.transaction.Transactional;
import java.util.Optional;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * User service class.
 *
 * @author Filipa Simões
 */
@Service
@Transactional
public class UserService implements UserDetailsService {

  private static final Logger LOGGER = LogManager.getLogger(UserService.class);

  @Autowired UserRepository userRepository;
  @Autowired UserMapper userMapper;

  @Override
  public UserDetails loadUserByUsername(String username) {

    try {
      userRepository.findByUsername(username);
    } catch (UsernameNotFoundException e) {
      LOGGER.log(Level.ERROR, e.getStackTrace());
    }
    return null;
  }

  /**
   * Register a new user.
   *
   * @param userDto the user to register {@link UserDto}.
   * @return the new created user.
   */
  public UserDto addNewUser(UserDto userDto) {
    if (!existsByUsername(userDto.getUsername())) {

      User user = userMapper.userDtoToUser(userDto);

      user.setPassword(new BCryptPasswordEncoder().encode(userDto.getPassword()));

      User savedUser = userRepository.save(user);

      return userMapper.userToUserDto(savedUser);
    } else {
      throw new UserAlreadyExistAuthenticationException("Username already exists");
    }
  }

  /**
   * Validates if the user exists by querying the database with the user's username.
   *
   * @param username the user's username.
   * @return true if exists.
   */
  public boolean existsByUsername(String username) {
    return userRepository.existsByUsername(username);
  }

  /**
   * Retrieves core user data.
   *
   * @param username the username.
   * @return the user data.
   */
  public UserDetails findByUsername(String username) {
    return userRepository.findByUsername(username);
  }

  /**
   * Get user e-mail.
   *
   * @param id the user identifier.
   * @return the user e-mail.
   */
  public String getUserEmail(int id) {
    Optional<User> user = userRepository.findById(id);

    if (user.isPresent()) {
      return user.get().getEmail();
    } else throw new ResourceNotFoundException("User not found.");
  }

  /**
   * Get user by identifier.
   *
   * @param userId the user identifier.
   * @return a user {@link UserDto}.
   */
  public boolean userExists(int userId) {
    Optional<User> user = userRepository.findById(userId);
    return user.isPresent();
  }
}
