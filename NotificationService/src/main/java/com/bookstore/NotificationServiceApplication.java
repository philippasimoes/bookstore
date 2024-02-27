package com.bookstore;

import com.bookstore.notification_service.model.entity.Notification;
import com.bookstore.notification_service.service.NotificationService;
import jakarta.annotation.PostConstruct;
import org.jobrunr.configuration.JobRunr;
import org.jobrunr.scheduling.BackgroundJob;
import org.jobrunr.scheduling.JobScheduler;
import org.jobrunr.scheduling.cron.Cron;
import org.jobrunr.storage.InMemoryStorageProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import java.util.List;
import java.util.stream.Collectors;

import static org.jobrunr.scheduling.RecurringJobBuilder.aRecurringJob;

@SpringBootApplication
@EnableTransactionManagement
@EnableScheduling
public class NotificationServiceApplication {

    public static void main(String[] args) {

        SpringApplication.run(NotificationServiceApplication.class, args);
    }


}