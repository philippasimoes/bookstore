package com.bookstore.user_service.infrastructure.security;

import jakarta.persistence.Column;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;

@Configuration
public class DefaultLogoutConfiguration {
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .logout(logout -> logout
                        .logoutUrl("/basic/basiclogout")
                        .addLogoutHandler(new SecurityContextLogoutHandler())
                );
        return http.build();
    }
}
