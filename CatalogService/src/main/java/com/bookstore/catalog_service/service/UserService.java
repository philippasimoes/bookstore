package com.bookstore.catalog_service.service;

import com.bookstore.catalog_service.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserService implements UserDetailsService {

    @Autowired
    UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) {

        try {
            userRepository.findByUsername(username);
        } catch (UsernameNotFoundException e) {
            e.getStackTrace();
        }
        return null;
    }

}
