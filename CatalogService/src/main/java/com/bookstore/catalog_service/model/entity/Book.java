package com.bookstore.catalog_service.model.entity;

import com.bookstore.catalog_service.model.dto.enums.Availability;
import com.bookstore.catalog_service.model.dto.enums.Format;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import jakarta.persistence.*;
import java.sql.Timestamp;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

/**
 * Book JPA entity.
 *
 * @author Filipa Simões
 */
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@Entity
@Table(name = "book", schema = "catalogservice")
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
public class Book {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private int id;

  @Column private String title;

  @Column(name = "original_title")
  private String originalTitle;

  @Column(unique = true)
  private String isbn;

  @Column(name = "release_date")
  private String releaseDate;

  @Column(name = "edition_date")
  private String editionDate;

  @Column private String genre;

  @Column private String edition;

  @Column private boolean series;

  @Column private String publisher;

  @Column private String synopsis;

  @Column private double price;

  @Column(name = "promotional_price")
  private double promotionalPrice;

  @Column(name = "creation_date")
  @CreationTimestamp
  private Timestamp creationDate;

  @Column(name = "update_date")
  @UpdateTimestamp
  private Timestamp updateDate;

  @Column private String collection;

  @Column private String category;

  @Enumerated(EnumType.STRING)
  private Availability availability;

  @Enumerated(EnumType.STRING)
  private Format format;

  @ManyToMany
  @JoinTable(
      name = "book_language",
      joinColumns = @JoinColumn(name = "book_id"),
      inverseJoinColumns = @JoinColumn(name = "language_id"))
  private Set<Language> languages;

  @ManyToMany
  @JoinTable(
      name = "book_author",
      joinColumns = @JoinColumn(name = "book_id"),
      inverseJoinColumns = @JoinColumn(name = "author_id"))
  private Set<Author> authors;

  @ManyToMany
  @JoinTable(
      name = "book_tag",
      joinColumns = @JoinColumn(name = "book_id"),
      inverseJoinColumns = @JoinColumn(name = "tag_id"))
  private Set<BookTag> bookTags;

  @Column(name = "stock_available", nullable = false)
  private int stockAvailable;
}
