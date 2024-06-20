package com.bookstore.user_service.model.dto;


import java.sql.Timestamp;
import lombok.Data;

@Data
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