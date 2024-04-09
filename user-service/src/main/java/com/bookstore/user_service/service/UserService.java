package com.bookstore.user_service.service;

import com.bookstore.user_service.model.dto.UserDto;
import com.bookstore.user_service.model.entity.User;
import com.bookstore.user_service.model.mapper.UserMapper;
import com.bookstore.user_service.repository.UserRepository;
import jakarta.transaction.Transactional;
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
    if (!existsByUsername(userDto.getUsername())) {

      User user = userMapper.userDtoToUser(userDto);

      user.setPassword(new BCryptPasswordEncoder().encode(userDto.getPassword()));

      user.setStoreCredit(0);
      User savedUser = userRepository.save(user);

      return userMapper.userToUserDto(savedUser);
    } else {
      throw new RuntimeException("Username already exists"); //TODO: exception
    }
  }

  public boolean existsByUsername(String username) {
    return userRepository.existsByUsername(username);
  }

  public UserDetails findByUsername(String username) {
    return userRepository.findByUsername(username);
  }

  public String getUserEmail(int id) {
    return userRepository.findById(id).get().getEmail();
  }
}
