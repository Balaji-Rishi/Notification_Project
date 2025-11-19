package com.example.notificationworker.service;

import com.example.notificationworker.adapter.DeliveryAdapter;
import com.example.notificationworker.entity.Notification;
import com.example.notificationworker.entity.Status;
import com.example.notificationworker.repository.NotificationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;

@Service
public class WorkerService {

    private static final Logger log = LoggerFactory.getLogger(WorkerService.class);

    private final NotificationRepository repo;
    private final DeliveryAdapter adapter;
    private final Random rnd = new Random();

    @Value("${worker.batch-size:10}")
    private int batchSize;

    @Value("${worker.base-retry-seconds:30}")
    private long baseRetrySeconds;

    @Value("${worker.max-backoff-seconds:86400}") // 24 hours cap
    private long maxBackoffSeconds;

    public WorkerService(NotificationRepository repo, DeliveryAdapter adapter) {
        this.repo = repo;
        this.adapter = adapter;
    }

    @Scheduled(fixedDelayString = "${worker.poll-ms:5000}")
    public void pollAndProcess() {
        List<Notification> items = repo.findNextQueued(LocalDateTime.now(), PageRequest.of(0, batchSize));
        for (Notification n : items) {
            tryClaimAndProcess(n.getId());
        }
    }

    @Transactional
    protected void tryClaimAndProcess(Long id) {
        int updated = repo.claimForProcessing(id, LocalDateTime.now());
        if (updated == 0) return;

        // reload the fresh entity (now marked PROCESSING)
        Notification n = repo.findById(id).orElse(null);
        if (n == null) return;

        process(n); // transactional
    }

    @Transactional
    protected void process(Notification n) {
        try {
            adapter.send(n);
            n.setStatus(Status.SENT);
            n.setProcessingStartedAt(null);
            n.setLastError(null);
            n.setNextAttemptAt(null);
            repo.save(n);
            log.info("Worker: SENT id={} recipient={} channel={}", n.getId(), n.getRecipient(), n.getChannel());
        } catch (Exception ex) {
            handleFailure(n, ex.getMessage());
        }
    }

    private void handleFailure(Notification n, String errorMsg) {
        String err = errorMsg == null ? "unknown error" : errorMsg;
        if (err.length() > 1000) err = err.substring(0, 1000);

        int retries = n.getRetryCount() + 1;
        n.setRetryCount(retries);
        n.setLastError(err);

        if (retries >= n.getMaxRetries()) {
            n.setStatus(Status.FAILED);
            n.setProcessingStartedAt(null);
            repo.save(n);
            log.warn("Worker: FAILED id={} attempts={} error={}", n.getId(), retries, err);
            return;
        }

        long backoff = baseRetrySeconds * (1L << (retries - 1));
        if (backoff > maxBackoffSeconds) backoff = maxBackoffSeconds;

        long jitter = (long) (rnd.nextDouble() * Math.min(60, backoff));
        LocalDateTime next = LocalDateTime.now().plusSeconds(backoff + jitter);

        n.setNextAttemptAt(next);
        n.setStatus(Status.QUEUED);
        n.setProcessingStartedAt(null);
        repo.save(n);

        log.info("Worker: RETRY id={} retries={} nextAttemptAt={} backoff={}s jitter={}s", n.getId(), retries, next, backoff, jitter);
    }
}
