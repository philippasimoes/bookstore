package com.bookstore.catalog_service.model;

import lombok.*;

import jakarta.persistence.*;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "author", schema = "catalogservice")
public class Author {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private int id;

  @Column(nullable = false)
  private String name;

  @Column(name = "original_full_name")
  private String originalFullName;

  @Column(name = "date_of_birth")
  private String dateOfBirth;

  @Column(name = "place_of_birth")
  private String placeOfBirth;

  @Column(name = "date_of_death")
  private String dateOfDeath;

  @Column(name = "place_of_death")
  private String placeOfDeath;

  @Column private String about;

  @ManyToMany(fetch = FetchType.LAZY, mappedBy = "authors")
  private List<Book> books;
}
