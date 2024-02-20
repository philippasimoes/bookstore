package com.bookstore.catalog_service.service;

import com.bookstore.catalog_service.dto.AuthorDto;
import com.bookstore.catalog_service.exception.ResourceNotFoundException;
import com.bookstore.catalog_service.model.Author;
import com.bookstore.catalog_service.repository.AuthorRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class AuthorService {

  @Autowired AuthorRepository authorRepository;

  public AuthorDto getAuthorByID(int id) {
    Author author =
        authorRepository
            .findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Author not found"));

    AuthorDto authorDto = new AuthorDto();
    BeanUtils.copyProperties(author, authorDto);
    return authorDto;
  }

  public List<AuthorDto> getAuthorByName(String name) {
    List<Author> authors = authorRepository.findByName(name);

    List<AuthorDto> authorDtos = new ArrayList<>();

    if (!authors.isEmpty()) {
      for (Author author : authors) {
        AuthorDto authorDto = new AuthorDto();
        BeanUtils.copyProperties(author, authorDto);
        authorDtos.add(authorDto);
      }
      return authorDtos;
    } else throw new ResourceNotFoundException("No results");
  }

  public List<Author> getAllAuthors() {
    return authorRepository.findAll();
  }

  @Transactional
  public Author addNewAuthor(AuthorDto authorDto) {
    Author author = new Author();
    BeanUtils.copyProperties(authorDto, author);
    return authorRepository.save(author); // falta proteção para erros -> try catch
  }

  @Transactional
  public String updateAuthor(AuthorDto authorDto) {
    if (authorRepository.findById(authorDto.getId()).isPresent()) {
      Author author = new Author();
      BeanUtils.copyProperties(authorDto, author);
      authorRepository.save(author);
      return "Author with id " + authorDto.getId() + " updated";
    } else throw new ResourceNotFoundException("User not found");
  }
}
