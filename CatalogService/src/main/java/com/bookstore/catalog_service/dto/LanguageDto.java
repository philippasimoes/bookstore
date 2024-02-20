package com.bookstore.catalog_service.dto;

import com.bookstore.catalog_service.model.Book;
import lombok.*;

import java.util.List;
import java.util.UUID;


@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class LanguageDto {

    private int id;
    private String code;
    private List<BookDto> books;
}
