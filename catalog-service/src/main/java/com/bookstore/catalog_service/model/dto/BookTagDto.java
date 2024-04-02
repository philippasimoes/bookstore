package com.bookstore.catalog_service.model.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * BookTag data transfer object.
 *
 * @author Filipa Simões
 */
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class BookTagDto {

  private int id;

  @NotNull
  @Size(min = 0, max = 50)
  private String value;
}
