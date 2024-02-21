package com.bookstore.catalog_service.service;

import com.bookstore.catalog_service.dto.AuthorDto;
import com.bookstore.catalog_service.exception.ResourceNotFoundException;
import com.bookstore.catalog_service.mapper.AuthorMapper;
import com.bookstore.catalog_service.model.Author;
import com.bookstore.catalog_service.repository.AuthorRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AuthorService {

  @Autowired AuthorRepository authorRepository;

  @Autowired
  AuthorMapper authorMapper;

  public AuthorDto getAuthorByID(int id) {
    Author author =
        authorRepository
            .findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Author not found"));

    return authorMapper.authorToAuthorDto(author);
  }

  public List<AuthorDto> getAuthorByName(String name) {
    List<Author> authors = authorRepository.findByName(name);

    if (!authors.isEmpty()) {
      return authorMapper.authorLisToAuthorDtoList(authors);
    } else throw new ResourceNotFoundException("No results");
  }

  public List<Author> getAllAuthors() {
    return authorRepository.findAll();
  }

  @Transactional
  public Author addNewAuthor(AuthorDto authorDto) {
    return authorRepository.save(authorMapper.authorDtoToAuthor(authorDto)); // falta proteção para erros -> try catch
  }

  @Transactional
  public String updateAuthor(AuthorDto authorDto) {
    if (authorRepository.findById(authorDto.getId()).isPresent()) {
      authorRepository.save(authorMapper.authorDtoToAuthor(authorDto));
      return "Author with id " + authorDto.getId() + " updated";
    } else throw new ResourceNotFoundException("User not found");
  }
}
