package com.example.notificationworker.service;

import com.example.notificationworker.repository.NotificationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class ResetScheduler {

    private static final Logger log = LoggerFactory.getLogger(ResetScheduler.class);
    private final NotificationRepository repo;

    @Value("${worker.processing-timeout-minutes:10}")
    private long processingTimeoutMinutes;

    public ResetScheduler(NotificationRepository repo) {
        this.repo = repo;
    }

    @Scheduled(fixedDelayString = "${worker.reset-ms:60000}")
    public void resetStuck() {
        LocalDateTime cutoff = LocalDateTime.now().minusMinutes(processingTimeoutMinutes);
        int updated = repo.resetProcessingOlderThan(cutoff);
        if (updated > 0) {
            log.info("ResetScheduler: reset {} stuck messages older than {}", updated, cutoff);
        }
    }
}
