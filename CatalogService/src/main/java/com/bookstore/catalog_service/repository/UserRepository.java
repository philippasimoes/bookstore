package com.bookstore.catalog_service.repository;


import com.bookstore.catalog_service.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Integer> {

    UserDetails findByUsername(String username);
    boolean existsByUsername(String username);


}
