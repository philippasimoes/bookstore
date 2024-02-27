package com.bookstore.notification_service.controller;

import com.bookstore.notification_service.model.entity.Notification;
import com.bookstore.notification_service.service.NotificationService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import org.jobrunr.scheduling.BackgroundJob;
import org.jobrunr.scheduling.JobScheduler;
import org.jobrunr.scheduling.cron.Cron;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Pair;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.stream.Collectors;

import static org.jobrunr.scheduling.RecurringJobBuilder.aRecurringJob;

@RestController
@RequestMapping("/notification")
public class NotificationController {

    @Autowired
    NotificationService notificationService;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    JobScheduler jobScheduler;
    private final Logger LOGGER = LoggerFactory.getLogger(NotificationController.class);

    RestTemplate restTemplate = new RestTemplate();


    @PostMapping("/{bookId}")
    public ResponseEntity<String> scheduleJob(@PathVariable("bookId") int bookId, @RequestParam("customer_email") String customerEmail) throws JsonProcessingException {

        Notification notification = notificationService.createNotification(bookId, customerEmail);

        Pair<Integer, Integer> pair = Pair.of(bookId, notification.getId());
        objectMapper.writeValueAsString(pair);

        jobScheduler.createRecurrently(aRecurringJob()
                .withId(String.valueOf(notification.getId()))
                .withCron("*/2 * * * *")
                .withDetails(() -> notificationService.updateNotification(objectMapper.writeValueAsString(pair))));

        LOGGER.info(String.format("Notification with id %s created. Started recurrent job with id %s", notification.getId(), notification.getId()));
        return ResponseEntity.status(HttpStatus.OK).body("Notification created " + customerEmail);
    }

    @PostConstruct
    public void killFulfilledJobs() {

        BackgroundJob.createRecurrently(aRecurringJob()
                .withId("kill-fulfilled-jobs")
                .withCron(Cron.minutely())
                .withDetails(this::deleteFulfilledJobs));
    }

    public void deleteFulfilledJobs() {

        List<Notification> notifications = notificationService.findByAll();

        List<Notification> filteredNotifications = notifications.stream()
                .filter(Notification::isSent)
                .collect(Collectors.toList());

        for (Notification notification : filteredNotifications) {
            BackgroundJob.delete(String.valueOf(notification.getId()));
        }
    }

}
