package com.bookstore.notification_service.controller;

import com.bookstore.notification_service.model.dto.OrderDto;
import com.bookstore.notification_service.service.OrderNotificationService;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
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
  private static final Logger LOGGER = LogManager.getLogger(NotificationController.class);

  /** Notification Service injection to access the service layer. */
  private final StockNotificationService stockNotificationService;

  private final OrderNotificationService orderNotificationService;

  /** Job Scheduler injection to run background jobs. */
  private final JobScheduler jobScheduler;

  public NotificationController(
      StockNotificationService stockNotificationService,
      OrderNotificationService orderNotificationService,
      JobScheduler jobScheduler) {

    this.stockNotificationService = stockNotificationService;
    this.orderNotificationService = orderNotificationService;
    this.jobScheduler = jobScheduler;
  }

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

    return ResponseEntity.status(HttpStatus.OK).body("Stock notification created " + customerEmail);
  }

  @PostMapping("/order")
  public ResponseEntity<String> createOrderNotification(
      @RequestBody OrderDto order,
      @RequestParam("customer_email") String customerEmail,
      @RequestParam("tracking_number") String trackingNumber) {
    orderNotificationService.createNotification(order, customerEmail, trackingNumber);

    return ResponseEntity.status(HttpStatus.OK).body("Order notification created " + customerEmail);
  }

  /** Background job that validates which notifications need to be updated. */
  @PostConstruct
  public void verifyNotifications() {

    jobScheduler.scheduleRecurrently(
        Cron.minutely(), stockNotificationService::verifyUnsentNotifications);
    LOGGER.info("Notification job has started");
  }
}
