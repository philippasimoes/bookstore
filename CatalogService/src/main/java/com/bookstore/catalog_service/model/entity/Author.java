package com.bookstore.catalog_service.model.entity;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import lombok.*;

import jakarta.persistence.*;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "author", schema = "catalogservice")
@JsonIdentityInfo(
        generator = ObjectIdGenerators.PropertyGenerator.class,
        property = "id")
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