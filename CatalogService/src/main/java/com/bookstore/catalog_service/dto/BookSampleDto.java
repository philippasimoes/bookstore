package com.bookstore.catalog_service.dto;

import lombok.*;

import java.util.UUID;


@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class BookSampleDto {
    private int id;
    private int bookId;
    private String sample;
}
