package com.bookstore.catalog_service.dto;

import lombok.*;

import java.util.List;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class TagDto {

    private int id;
    private String value;
    private List<BookDto> books;

}
