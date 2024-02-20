package com.bookstore.catalog_service.specifications;

import com.bookstore.catalog_service.dto.enums.Availability;
import com.bookstore.catalog_service.model.Author;
import com.bookstore.catalog_service.model.Book;
import com.bookstore.catalog_service.model.Language;
import com.bookstore.catalog_service.model.Tag;
import jakarta.persistence.criteria.Join;
import org.springframework.data.jpa.domain.Specification;

public class BookSpecifications {

  /**
   * Used to query the database to retrieve the books with a determined availability.
   *
   * @param availability the availability.
   * @return A specification to be used to get a list of books with a determined availability.
   */
  public static Specification<Book> hasAvailability(Availability availability) {
    return (book, query, criteriaBuilder) ->
        criteriaBuilder.equal(book.get("availability"), availability);
  }

  public static Specification<Book> hasPredicate(String columnName, String predicate) {
    return (book, query, criteriaBuilder) ->
            criteriaBuilder.equal(book.get(columnName), predicate);
  }

  /**
   * Used to get all the books from a determined author (it will query the join table book_author).
   *
   * @param author the author.
   * @return a specification to be used to get a list of books from the determined author.
   */
  public static Specification<Book> allBooksFromAuthor(Author author) {
    return (book, query, criteriaBuilder) -> {
      Join<Book, Author> authorsBooks = book.join("authors");
      return criteriaBuilder.equal(authorsBooks.get("id"), author.getId());
    };
  }

  /**
   * Used to get all the books from a determined language (it will query the join table book_language).
   *
   * @param language the author.
   * @return a specification to be used to get a list of books from the determined author.
   */
  public static Specification<Book> allBooksFromLanguage(Language language) {
    return (book, query, criteriaBuilder) -> {
      Join<Book, Language> languageBooks = book.join("languages");
      return criteriaBuilder.equal(languageBooks.get("id"), language.getId());
    };
  }

  /**
   * Used to get all the books from a determined language (it will query the join table book_language).
   *
   * @param language the author.
   * @return a specification to be used to get a list of books from the determined author.
   */
  public static Specification<Book> allBooksFromTag(Tag language) {
    return (book, query, criteriaBuilder) -> {
      Join<Book, Language> languageBooks = book.join("languages");
      return criteriaBuilder.equal(languageBooks.get("id"), language.getId());
    };
  }
}
