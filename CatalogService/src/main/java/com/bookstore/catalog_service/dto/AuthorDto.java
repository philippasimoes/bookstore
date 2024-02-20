package com.bookstore.catalog_service.dto;

import lombok.*;

import java.util.List;
import java.util.UUID;

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
