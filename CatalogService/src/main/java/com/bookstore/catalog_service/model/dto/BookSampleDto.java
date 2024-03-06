package com.bookstore.catalog_service.model.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class BookSampleDto {

  private int id;

  @NotNull private int bookId;

  @NotNull private String sample;
}
