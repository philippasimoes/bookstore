package com.bookstore.catalog_service.service;

import com.bookstore.catalog_service.exception.ResourceNotFoundException;
import com.bookstore.catalog_service.model.dto.AuthorDto;
import com.bookstore.catalog_service.model.dto.BookDto;
import com.bookstore.catalog_service.model.entity.Author;
import com.bookstore.catalog_service.model.mapper.AuthorMapper;
import com.bookstore.catalog_service.repository.AuthorRepository;
import jakarta.transaction.Transactional;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Author service class.
 *
 * @author Filipa Simões
 */
@Service
@Transactional
public class AuthorService {
  private static final String AUTHOR_NOT_FOUND_MESSAGE = "Author not found.";

  @Autowired AuthorRepository authorRepository;
  @Autowired AuthorMapper authorMapper;

  public AuthorDto getAuthorDtoById(int id) {
    Author author =
        authorRepository
            .findById(id)
            .orElseThrow(() -> new ResourceNotFoundException(AUTHOR_NOT_FOUND_MESSAGE));

    return authorMapper.toDto(author);
  }

  public Author getAuthorById(int id) {

    return authorRepository
        .findById(id)
        .orElseThrow(() -> new ResourceNotFoundException(AUTHOR_NOT_FOUND_MESSAGE));
  }

  public List<AuthorDto> getAuthorByName(String name) {
    List<Author> authors = authorRepository.findByNameLike(name);

    if (!authors.isEmpty()) {
      return authorMapper.toDtoList(authors);
    } else throw new ResourceNotFoundException("No results");
  }

  public Set<AuthorDto> getAllAuthors() {
    return authorMapper.toDtoSet(new HashSet<>(authorRepository.findAll()));
  }

  public Author addNewAuthor(AuthorDto authorDto) {
    return authorRepository.save(authorMapper.toEntity(authorDto));
  }

  public String updateAuthor(AuthorDto authorDto) {
    if (authorRepository.findById(authorDto.getId()).isPresent()) {
      authorRepository.save(authorMapper.toEntity(authorDto));
      return "Author with id " + authorDto.getId() + " updated";
    } else throw new ResourceNotFoundException(AUTHOR_NOT_FOUND_MESSAGE);
  }

  /**
   * Get authors from a book - if the author does not exist in database it will be created.
   *
   * @param bookDto the book (data transfer object) sent in request.
   * @return a set of authors.
   */
  public Set<Author> getAuthorsFromBook(BookDto bookDto) {

    Set<Author> authorSet = new HashSet<>();

    for (AuthorDto authorDto : bookDto.getAuthors()) {
      Optional<Author> author = authorRepository.findByIsni(authorDto.getIsni());

      if (author.isPresent()) {
        authorSet.add(author.get());
      } else {
        Author savedAuthor = authorRepository.save(authorMapper.toEntity(authorDto));
        authorSet.add(savedAuthor);
      }
    }

    return authorSet;
  }
}
