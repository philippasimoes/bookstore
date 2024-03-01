package com.bookstore.catalog_service.controller;

import com.bookstore.catalog_service.infrastructure.security.TokenService;
import com.bookstore.catalog_service.model.dto.AuthenticationDto;
import com.bookstore.catalog_service.model.dto.LoginResponseDto;
import com.bookstore.catalog_service.model.dto.UserDto;
import com.bookstore.catalog_service.model.entity.User;
import com.bookstore.catalog_service.repository.UserRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/auth")
public class AuthenticationController {


    //  @Autowired
    // private AuthenticationManager authManager;

    @Autowired
    private TokenService tokenService;

    @Autowired
    private UserRepository userRepository;


    @PostMapping("/login")
    public ResponseEntity login(@RequestBody @Valid AuthenticationDto authenticationDto) {


        UserDetails user = userRepository.findByUsername(authenticationDto.username());
        var usernamePassword = new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword(), user.getAuthorities());

        //      var authUser = this.authManager.authenticate(usernamePassword);

        var accessToken = tokenService.generateAccessToken(usernamePassword);

        return ResponseEntity.ok(new LoginResponseDto(accessToken));
    }

    @PostMapping("/register")
    public ResponseEntity register(@RequestBody @Valid UserDto userDto) {

        if (userRepository.existsByUsername(userDto.getUsername())) return ResponseEntity.badRequest().build();
        else {

            User user = new User();
            user.setUsername(userDto.getUsername());
            user.setPassword(new BCryptPasswordEncoder().encode(userDto.getPassword()));
            user.setRole(userDto.getRole());
            user.setName(userDto.getName());
            user.setEmail(userDto.getEmail());
            user.setEnabled(true);
            userRepository.save(user);

            return ResponseEntity.ok().build();
        }

    }

}
