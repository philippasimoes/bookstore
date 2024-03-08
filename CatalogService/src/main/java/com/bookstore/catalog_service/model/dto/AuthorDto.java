package com.bookstore.catalog_service.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Author data transfer object.
 *
 * @author Filipa Simões
 */
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class AuthorDto {

  private int id;

  @NotBlank
  @Size(min = 0, max = 512)
  private String name;

  @Size(min = 0, max = 512)
  private String originalFullName;

  @NotBlank
  @Size(min = 0, max = 8, message = "YYYYmmdd")
  private String dateOfBirth;

  @Size(min = 0, max = 256)
  private String placeOfBirth;

  @Size(min = 0, max = 8, message = "YYYYmmdd")
  private String dateOfDeath;

  @Size(min = 0, max = 256)
  private String placeOfDeath;

  @Size(min = 0, max = 1024)
  private String about;
}
