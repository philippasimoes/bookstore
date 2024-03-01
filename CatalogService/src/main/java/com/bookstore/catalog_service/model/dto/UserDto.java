package com.bookstore.catalog_service.model.dto;

import com.bookstore.catalog_service.model.dto.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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
    private Role role;
    private boolean enabled;

}
