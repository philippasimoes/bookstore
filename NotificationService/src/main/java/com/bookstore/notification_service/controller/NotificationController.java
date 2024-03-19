package com.bookstore.notification_service.controller;

import com.bookstore.notification_service.service.StockNotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.PostConstruct;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jobrunr.scheduling.JobScheduler;
import org.jobrunr.scheduling.cron.Cron;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Rest Controller for Notification Service.
 *
 * @author Filipa Simões
 */
@RestController
@RequestMapping("/notification")
@Tag(name = "Notification endpoints")
public class NotificationController {

  /** Class logger. */
  private final Logger LOGGER = LogManager.getLogger(NotificationController.class);

  /** Notification Service injection to access the service layer. */
  @Autowired
  StockNotificationService stockNotificationService;

  /** Job Scheduler injection to run background jobs. */
  @Autowired JobScheduler jobScheduler;

  /**
   * Create notification
   *
   * @param bookId the book identifier.
   * @param customerEmail the customer e-mail.
   * @return a message stating that the notification was created.
   */
  @Operation(summary = "Add a stock notification to database.")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "Notification added.",
            content = {
              @Content(
                  mediaType = "application/json",
                  schema = @Schema(implementation = String.class))
            })
      })
  @PostMapping("/stock/{book_id}")
  public ResponseEntity<String> createStockNotification(
      @Parameter(description = "Book identifier.", required = true) @PathVariable("book_id")
          int bookId,
      @Parameter(
              description =
                  "Customer e-mail, needed to notify the customer when the book is available for purchase.",
              required = true)
          @RequestParam("customer_email")
          String customerEmail) {

    stockNotificationService.createNotification(bookId, customerEmail);

    return ResponseEntity.status(HttpStatus.OK).body("Notification created " + customerEmail);
  }

  /** Background job that validates which notifications need to be updated. */
  @PostConstruct
  public void verifyNotifications() {

    jobScheduler.scheduleRecurrently(
        Cron.minutely(), () -> stockNotificationService.verifyUnsentNotifications());
    LOGGER.info("Notification job has started");
  }
}
