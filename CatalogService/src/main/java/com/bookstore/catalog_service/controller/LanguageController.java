package com.bookstore.catalog_service.controller;

import com.bookstore.catalog_service.model.dto.AuthorDto;
import com.bookstore.catalog_service.model.dto.LanguageDto;
import com.bookstore.catalog_service.model.entity.Language;
import com.bookstore.catalog_service.service.LanguageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Rest Controller for Language Service.
 *
 * @author Filipa Simões
 */
@RestController
@RequestMapping("/lang")
@Tag(name = "Language endpoints")
public class LanguageController {

  @Autowired LanguageService languageService;

  @Operation(summary = "Get all languages.")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "Show a list of languages.",
            content = {
              @Content(
                  mediaType = "application/json",
                  array = @ArraySchema(schema = @Schema(implementation = LanguageDto.class)))
            }),
        @ApiResponse(responseCode = "204", description = "No languages in database.")
      })
  @GetMapping
  public ResponseEntity<Set<LanguageDto>> getAllLanguages() {
    Set<LanguageDto> languages = languageService.getAllLanguages();

    if (!languages.isEmpty()) {
      return ResponseEntity.ok(languages);
    } else return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
  }

  @Operation(summary = "Add a language to database.")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "201",
            description = "Language added.",
            content = {
              @Content(
                  mediaType = "application/json",
                  schema = @Schema(implementation = AuthorDto.class))
            }),
        @ApiResponse(responseCode = "302", description = "Language already exists in database."),
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
            description = "The user doesn't have permission to create languages.",
            content = {
              @Content(
                  mediaType = "application/json",
                  schema = @Schema(implementation = String.class))
            })
      })
  @SecurityRequirement(name = "admin-only")
  @PostMapping
  public ResponseEntity<Language> addNewLanguage(@RequestBody LanguageDto languageDto) {
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(languageService.addNewLanguage(languageDto));
  }
}
