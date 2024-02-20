package com.bookstore.catalog_service.dto;

import com.bookstore.catalog_service.dto.enums.Availability;
import com.bookstore.catalog_service.dto.enums.Format;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.sql.Timestamp;
import java.util.Set;


@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class BookDto {
    private int id;
    @NotNull
    private String title;
    private String originalTitle;
    @NotNull
    private String isbn;
    private String releaseDate;
    private String editionDate;
    private String genre;
    private String edition;
    private boolean series;
    private String publisher;
    private String synopsis;
    private double price;
    private double promotionalPrice;
    private Timestamp creationDate;
    private Timestamp updateDate;
    private String collection;
    private String category;
    private Availability availability;
    private Format format;
    private Set<LanguageDto> languages;
    private Set<AuthorDto> authors;
    private Set<TagDto> tags;
    private int stockAvailable;
}
