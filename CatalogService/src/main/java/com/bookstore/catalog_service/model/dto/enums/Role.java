package com.bookstore.catalog_service.model.dto.enums;

public enum Role {

    ADMIN("admin"),
    USER("user");

    private final String role;

    Role(String role) {

        this.role = role;
    }

    public String getRole() {

        return role;
    }
}
