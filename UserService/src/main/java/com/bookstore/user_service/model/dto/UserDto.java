package com.bookstore.user_service.model.dto;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Timestamp;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class UserDto {

    private int id;
    private String name;
    private String email;
    private String username;
    private String password;
    private String address;
    private String postalCode;
    private String role;
    private boolean enabled;
    private Timestamp creationDate;
    private Timestamp updateDate;
}