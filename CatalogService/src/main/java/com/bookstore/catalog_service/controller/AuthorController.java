package com.bookstore.catalog_service.controller;

import com.bookstore.catalog_service.model.dto.AuthorDto;
import com.bookstore.catalog_service.model.entity.Author;
import com.bookstore.catalog_service.service.AuthorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(value = "/authors")
@Tag(name = "Author endpoints")
public class AuthorController {

  @Autowired AuthorService authorService;

  @Operation(summary = "Get all authors.")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description =
                "Show a list of authors. If no author is registered in database, an empty list is returned.",
            content = {
              @Content(
                  mediaType = "application/json",
                  array = @ArraySchema(schema = @Schema(implementation = AuthorDto.class)))
            }),
        @ApiResponse(responseCode = "204", description = "No authors in database.")
      })
  @GetMapping
  public ResponseEntity<List<AuthorDto>> getAllAuthors() {
    List<AuthorDto> authors = authorService.getAllAuthors();

    if (!authors.isEmpty()) {
      return ResponseEntity.status(HttpStatus.OK).body(authorService.getAllAuthors());
    } else return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
  }

  @Operation(summary = "Get an author by identifier.")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "Author found.",
            content = {
              @Content(
                  mediaType = "application/json",
                  schema = @Schema(implementation = AuthorDto.class))
            }),
        @ApiResponse(
            responseCode = "404",
            description = "Author not found.",
            content = {
              @Content(
                  mediaType = "application/json",
                  schema = @Schema(implementation = String.class))
            })
      })
  @GetMapping("/{id}")
  public ResponseEntity<AuthorDto> getAuthorByID(@PathVariable int id) {
    return ResponseEntity.status(HttpStatus.OK).body(authorService.getAuthorByID(id));
  }

  @Operation(summary = "Get an author by name.")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "Author found.",
            content = {
              @Content(
                  mediaType = "application/json",
                  schema = @Schema(implementation = AuthorDto.class))
            }),
        @ApiResponse(
            responseCode = "404",
            description = "Author not found.",
            content = {
              @Content(
                  mediaType = "application/json",
                  schema = @Schema(implementation = String.class))
            })
      })
  @GetMapping("/name/{name}")
  public ResponseEntity<List<AuthorDto>> getAuthorByName(@PathVariable String name) {
    return ResponseEntity.status(HttpStatus.OK).body(authorService.getAuthorByName(name));
  }

  @Operation(summary = "Add an author to database.")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "201",
            description = "Author added.",
            content = {
              @Content(
                  mediaType = "application/json",
                  schema = @Schema(implementation = AuthorDto.class))
            }),
        @ApiResponse(
            responseCode = "401",
            description = "The user is not authenticated.",
            content = {
              @Content(
                  mediaType = "application/json",
                  schema = @Schema(implementation = String.class))
            }),
        @ApiResponse(
            responseCode = "403",
            description = "The user doesn't have permission to create authors.",
            content = {
              @Content(
                  mediaType = "application/json",
                  schema = @Schema(implementation = String.class))
            })
      })
  @SecurityRequirement(name = "admin-only")
  @PostMapping
  public ResponseEntity<Author> addNewAuthor(@RequestBody AuthorDto authorDto) {
    return ResponseEntity.status(HttpStatus.CREATED).body(authorService.addNewAuthor(authorDto));
  }

  @Operation(summary = "Update an existing author.")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "Author updated.",
            content = {
              @Content(
                  mediaType = "application/json",
                  schema = @Schema(implementation = AuthorDto.class))
            }),
        @ApiResponse(
            responseCode = "401",
            description = "The user is not authenticated.",
            content = {
              @Content(
                  mediaType = "application/json",
                  schema = @Schema(implementation = String.class))
            }),
        @ApiResponse(
            responseCode = "403",
            description = "The user is not authorized to update the author.",
            content = {
              @Content(
                  mediaType = "application/json",
                  schema = @Schema(implementation = String.class))
            })
      })
  @SecurityRequirement(name = "admin-only")
  @PutMapping("/update")
  public ResponseEntity<String> updateAuthor(@RequestBody AuthorDto authorDto) {
    return ResponseEntity.status(HttpStatus.OK).body(authorService.updateAuthor(authorDto));
  }
}
