package com.bookstore.user_service.service;

import com.bookstore.user_service.model.dto.UserDto;
import com.bookstore.user_service.model.entity.User;
import com.bookstore.user_service.model.mapper.UserMapper;
import com.bookstore.user_service.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
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

  @Autowired UserRepository userRepository;

  @Autowired UserMapper userMapper;

  @Override
  public UserDetails loadUserByUsername(String username) {

    try {
      userRepository.findByUsername(username);
    } catch (UsernameNotFoundException e) {
      e.getStackTrace();
    }
    return null;
  }

  public UserDto addNewUser(UserDto userDto) {
    User user = userMapper.userDtoToUser(userDto);

    user.setPassword(new BCryptPasswordEncoder().encode(userDto.getPassword()));

    User savedUser = userRepository.save(user);

    return userMapper.userToUserDto(savedUser);
  }

  public boolean existsByUsername(String username) {
    return userRepository.existsByUsername(username);
  }

  public UserDetails findByUsername(String username) {
    return userRepository.findByUsername(username);
  }
}
