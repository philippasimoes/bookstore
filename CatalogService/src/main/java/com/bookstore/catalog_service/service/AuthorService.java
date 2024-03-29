package com.bookstore.catalog_service.service;

import com.bookstore.catalog_service.exception.ResourceNotFoundException;
import com.bookstore.catalog_service.model.dto.AuthorDto;
import com.bookstore.catalog_service.model.entity.Author;
import com.bookstore.catalog_service.model.mapper.AuthorMapper;
import com.bookstore.catalog_service.repository.AuthorRepository;
import jakarta.transaction.Transactional;

import java.util.HashSet;
import java.util.List;
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

  @Autowired AuthorRepository authorRepository;

  @Autowired AuthorMapper authorMapper;

  public AuthorDto getAuthorByID(int id) {
    Author author =
        authorRepository
            .findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Author not found"));

    return authorMapper.toDto(author);
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

  @Transactional
  public Author addNewAuthor(AuthorDto authorDto) {
    return authorRepository.save(authorMapper.toEntity(authorDto));
  }

  @Transactional
  public String updateAuthor(AuthorDto authorDto) {
    if (authorRepository.findById(authorDto.getId()).isPresent()) {
      authorRepository.save(authorMapper.toEntity(authorDto));
      return "Author with id " + authorDto.getId() + " updated";
    } else throw new ResourceNotFoundException("User not found");
  }
}
