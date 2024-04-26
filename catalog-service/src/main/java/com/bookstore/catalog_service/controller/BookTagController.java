package com.bookstore.catalog_service.controller;

import com.bookstore.catalog_service.model.dto.AuthorDto;
import com.bookstore.catalog_service.model.dto.BookTagDto;
import com.bookstore.catalog_service.model.entity.BookTag;
import com.bookstore.catalog_service.service.BookTagService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.Set;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Rest Controller for Tag Service.
 *
 * @author Filipa Simões
 */
@RestController
@RequestMapping("/tags")
@Tag(name = "Book Tag endpoints")
public class BookTagController {

  private final BookTagService bookTagService;

  public BookTagController(BookTagService bookTagService) {

    this.bookTagService = bookTagService;
  }

  @Operation(summary = "Get all tags.")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "Show a list of tags.",
            content = {
              @Content(
                  mediaType = "application/json",
                  array = @ArraySchema(schema = @Schema(implementation = BookTagDto.class)))
            }),
        @ApiResponse(responseCode = "204", description = "No tags in database.")
      })
  @GetMapping
  public ResponseEntity<Set<BookTagDto>> getAllTags() {
    Set<BookTagDto> languages = bookTagService.getAllBookTags();

    if (!languages.isEmpty()) {
      return ResponseEntity.ok(languages);
    } else return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
  }

  @Operation(summary = "Add a book tag to database.")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "201",
            description = "Tag added.",
            content = {
              @Content(
                  mediaType = "application/json",
                  schema = @Schema(implementation = AuthorDto.class))
            }),
        @ApiResponse(responseCode = "302", description = "Tag already exists in database."),
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
            description = "The user doesn't have permission to create tags.",
            content = {
              @Content(
                  mediaType = "application/json",
                  schema = @Schema(implementation = String.class))
            })
      })
  @SecurityRequirement(name = "admin-only")
  @PostMapping
  public ResponseEntity<BookTag> addNewTag(@RequestBody BookTagDto bookTagDto) {
    return ResponseEntity.status(HttpStatus.CREATED).body(bookTagService.addNewBookTag(bookTagDto));
  }
}
