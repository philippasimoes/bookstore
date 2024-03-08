package com.bookstore.stock_service.controller;

import com.bookstore.stock_service.service.StockService;
import com.bookstore.stock_service.utils.StockStatus;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Stock controller class.
 *
 * @author Filipa Simões
 */
@RestController
@RequestMapping(value = "/stock")
@Tag(name = "Stock endpoints")
public class StockController {

  @Autowired StockService stockService;

  /**
   * Create a stock entry in the database. This method is to be used by Catalog Service when
   * creating the book.
   *
   * @param bookId the book identifier.
   * @return a response entity with the appropriated code and message.
   */
  @Operation(summary = "Add a stock entry to database.")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "Stock entry added.",
            content = {
              @Content(
                  mediaType = "application/json",
                  schema = @Schema(implementation = String.class))
            }),
        @ApiResponse(
            responseCode = "302",
            description = "A stock entry for the book this book id already exists.",
            content = {
              @Content(
                  mediaType = "application/json",
                  schema = @Schema(implementation = String.class))
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
            description = "The user doesn't have permission to create stock.",
            content = {
              @Content(
                  mediaType = "application/json",
                  schema = @Schema(implementation = String.class))
            })
      })
  @SecurityRequirement(name = "admin-only")
  @PostMapping("/book/{book_id}")
  public ResponseEntity<String> createStock(@PathVariable("book_id") int bookId) {

    ResponseEntity<String> responseEntity;

    stockService.addStock(bookId);
    responseEntity = ResponseEntity.status(HttpStatus.OK).body("Stock created");

    return responseEntity;
  }

  /**
   * Update the book available units in the stock.
   *
   * @param bookId the book identifier.
   * @param units the units to be added/subtracted to the existent value.
   * @return a response entity with the appropriated code and message.
   */
  @Operation(summary = "Update a stock entry.")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "Stock entry updated.",
            content = {
              @Content(
                  mediaType = "application/json",
                  schema = @Schema(implementation = String.class))
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
            description = "The user doesn't have permission to create stock.",
            content = {
              @Content(
                  mediaType = "application/json",
                  schema = @Schema(implementation = String.class))
            }),
        @ApiResponse(
            responseCode = "412",
            description =
                "The number of units sent in the request is greater than the available units. The request cannot be processed.",
            content = {
              @Content(
                  mediaType = "application/json",
                  schema = @Schema(implementation = String.class))
            }),
        @ApiResponse(
            responseCode = "500",
            description =
                "Stock is updated but the connection with catalog service was not made due to an error building the message.",
            content = {
              @Content(
                  mediaType = "application/json",
                  schema = @Schema(implementation = String.class))
            })
      })
  @SecurityRequirement(name = "admin-only")
  @PutMapping("/book/{book_id}")
  public ResponseEntity<String> updateStock(
      @PathVariable("book_id") int bookId, @RequestParam("units") int units) {

    return validateStockStatus(stockService.updateStock(bookId, units));
  }

  /**
   * To be called by an external service (ex: Notification Service).
   *
   * @param bookId the book identifier.
   * @return a response entity with status 200 and the available book units in the stock (if the
   *     book units is 0 a 302 response code is retrieved).
   */
  @Operation(summary = "Update a stock entry.")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "Number of book units is above zero.",
            content = {
              @Content(
                  mediaType = "application/json",
                  schema = @Schema(implementation = Integer.class))
            }),
        @ApiResponse(
            responseCode = "302",
            description = "Number of book units is zero.",
            content = {
              @Content(
                  mediaType = "application/json",
                  schema = @Schema(implementation = Integer.class))
            })
      })
  @SecurityRequirement(name = "admin-only")
  @GetMapping("/{bookId}")
  public ResponseEntity<Integer> stockIsAboveZero(@PathVariable("bookId") int bookId) {

    return ResponseEntity.ok(stockService.getStockByBookId(bookId).getUnits());
  }

  /**
   * Validate stock status enum and return an adequate response entity.
   *
   * @param stockStatus the stock status {@link StockStatus}
   * @return a response entity.
   */
  private ResponseEntity<String> validateStockStatus(StockStatus stockStatus) {
    ResponseEntity<String> responseEntity;

    switch (stockStatus) {
      case UPDATED -> responseEntity = ResponseEntity.status(HttpStatus.OK).body("Stock updated");
      case SOLD_OUT ->
          responseEntity =
              ResponseEntity.status(HttpStatus.OK).body("Stock updated - book sold out");
      case INSUFFICIENT_STOCK ->
          responseEntity =
              ResponseEntity.status(HttpStatus.PRECONDITION_FAILED)
                  .body("Insufficient stock - operation not permitted");
      case BOOK_NOT_FOUND -> responseEntity = ResponseEntity.notFound().build();
      case MESSAGE_ERROR ->
          responseEntity =
              ResponseEntity.internalServerError()
                  .body(
                      "Stock is updated but the "
                          + "connection with catalog service was not made due to an error building the message.");
      default -> responseEntity = ResponseEntity.internalServerError().build();
    }

    return responseEntity;
  }
}
