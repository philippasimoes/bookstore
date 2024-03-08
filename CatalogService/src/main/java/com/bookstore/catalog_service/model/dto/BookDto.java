package com.bookstore.catalog_service.model.dto;

import com.bookstore.catalog_service.model.dto.enums.Availability;
import com.bookstore.catalog_service.model.dto.enums.Format;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import java.sql.Timestamp;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Book data transfer object.
 *
 * @author Filipa Simões
 */
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class BookDto {

  private int id;

  @NotNull
  @NotBlank
  @Size(min = 0, max = 512)
  private String title;

  @Size(min = 0, max = 512)
  private String originalTitle;

  @NotNull
  @Size(min = 0, max = 20)
  private String isbn;

  @Size(min = 0, max = 8, message = "YYYYmmdd")
  private String releaseDate;

  @Size(min = 0, max = 8, message = "YYYYmmdd")
  private String editionDate;

  @Size(min = 0, max = 50)
  private String genre;

  @Size(min = 0, max = 50)
  private String edition;

  private boolean series;

  @Size(min = 0, max = 50)
  private String publisher;

  @Size(min = 0, max = 1024)
  private String synopsis;

  @PositiveOrZero private double price;

  @PositiveOrZero private double promotionalPrice;

  private Timestamp creationDate;

  private Timestamp updateDate;

  @Size(min = 0, max = 50)
  private String collection;

  @Size(min = 0, max = 50)
  private String category;

  private Availability availability;

  private Format format;

  private Set<LanguageDto> languages;

  private Set<AuthorDto> authors;

  private Set<BookTagDto> bookTags;

  @PositiveOrZero private int stockAvailable;
}
