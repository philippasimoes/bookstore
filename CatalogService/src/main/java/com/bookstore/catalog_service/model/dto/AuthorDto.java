package com.bookstore.catalog_service.model.dto;

import lombok.*;

import java.util.List;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class AuthorDto {

    private int id;
    private String name;
    private String originalFullName;
    private String dateOfBirth;
    private String placeOfBirth;
    private String dateOfDeath;
    private String placeOfDeath;
    private String about;
    private List<BookDto> books;
}
