package com.bookstore.catalog_service.controller;

import com.bookstore.catalog_service.dto.AuthorDto;
import com.bookstore.catalog_service.model.Author;
import com.bookstore.catalog_service.service.AuthorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import java.util.UUID;

@RestController
@RequestMapping(value = "/authors")
public class AuthorController {

  @Autowired
  AuthorService authorService;

  @GetMapping("/all")
  public ResponseEntity<List<Author>> getAllAuthors() {
    List<Author> authors = authorService.getAllAuthors();

    if (authors.isEmpty()) {
      return ResponseEntity.status(HttpStatus.NO_CONTENT).body(null);
    } else {
      return ResponseEntity.status(HttpStatus.OK).body(authorService.getAllAuthors());
    }
  }

  @GetMapping("/{id}")
  public ResponseEntity<AuthorDto> getAuthorByID(@PathVariable int id) {
    return ResponseEntity.status(HttpStatus.OK).body(authorService.getAuthorByID(id));
  }

  @GetMapping("/name/{name}")
  public ResponseEntity<List<AuthorDto>> getAuthorByName(@PathVariable String name) {
    return ResponseEntity.status(HttpStatus.OK).body(authorService.getAuthorByName(name));
  }

  @PostMapping
  public ResponseEntity<Author> addNewAuthor(@RequestBody AuthorDto authorDto) {
    return ResponseEntity.status(HttpStatus.CREATED).body(authorService.addNewAuthor(authorDto));
  }

  @PutMapping("/update")
  public ResponseEntity<String> updateAuthorName(@RequestBody AuthorDto authorDto) {
    return ResponseEntity.status(HttpStatus.OK).body(authorService.updateAuthor(authorDto));
  }
}
