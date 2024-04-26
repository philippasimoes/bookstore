package com.bookstore.catalog_service.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Publisher data transfer object.
 *
 * @author Filipa Simões
 */
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PublisherDto {

  private int id;

  private String name;

  private String email;

  private String phoneNumber;

}
