package com.bookstore.user_service.repository;

import com.bookstore.user_service.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * User repository class.
 *
 * @author Filipa Simões
 */
@Repository
public interface UserRepository  extends JpaRepository<User, Integer> {

    UserDetails findByUsername(String username);
    boolean existsByUsername(String username);

}
