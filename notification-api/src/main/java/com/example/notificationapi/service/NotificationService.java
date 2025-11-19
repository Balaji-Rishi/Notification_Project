package com.example.notificationapi.service;

import com.example.notificationapi.dto.NotificationDto;
import com.example.notificationapi.dto.NotificationRequest;
import com.example.notificationapi.dto.NotificationResponse;
import com.example.notificationapi.entity.Channel;
import com.example.notificationapi.entity.Notification;
import com.example.notificationapi.entity.Status;
import com.example.notificationapi.repository.NotificationRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.regex.Pattern;

@Service
public class NotificationService {

    private final NotificationRepository repo;

    // E.164 phone regex (recommended)
    private static final Pattern E164 = Pattern.compile("^\\+[1-9]\\d{1,14}$");
    // Practical email regex (covers most typical emails)
    private static final Pattern EMAIL = Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");

    public NotificationService(NotificationRepository repo) {
        this.repo = repo;
    }

    /**
     * Create a new notification, validate recipient depending on the channel,
     * persist it and return a NotificationResponse (id + status).
     */
    @Transactional
    public NotificationResponse createNotification(NotificationRequest req) {
        Channel channel;
        try {
            channel = Channel.valueOf(req.getChannel().toUpperCase());
        } catch (Exception ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "channel must be EMAIL or SMS");
        }

        validateRecipient(channel, req.getRecipient());

        Notification n = new Notification();
        n.setChannel(channel);
        n.setRecipient(req.getRecipient());
        n.setSubject(req.getSubject());
        n.setBody(req.getBody());
        if (req.getMaxRetries() != null) n.setMaxRetries(req.getMaxRetries());
        n.setStatus(Status.QUEUED);
        n.setRetryCount(0);
        // nextAttemptAt / processingStartedAt are null initially

        Notification saved = repo.save(n);
        return new NotificationResponse(saved.getId(), saved.getStatus());
    }

    private void validateRecipient(Channel channel, String recipient) {
        if (recipient == null || recipient.trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "recipient is required");
        }

        if (channel == Channel.EMAIL) {
            if (!EMAIL.matcher(recipient).matches()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "invalid email format");
            }
        } else { // SMS
            if (!E164.matcher(recipient).matches()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "invalid phone format (must be E.164, e.g. +1234567890)");
            }
        }
    }

    // Returns a Page<NotificationDto> used by your controller
    public Page<NotificationDto> getAll(Pageable pageable) {
        Page<Notification> page = repo.findAll(pageable);
        return page.map(this::toDto);
    }

    // Get by id -> returns Optional<NotificationDto> used by controller
    public Optional<NotificationDto> getById(Long id) {
        return repo.findById(id).map(this::toDto);
    }

    // Optional helper used by worker
    public java.util.List<Notification> findNextQueued(int batchSize) {
        return repo.findNextQueued(LocalDateTime.now(), org.springframework.data.domain.PageRequest.of(0, batchSize));
    }

    private NotificationDto toDto(Notification n) {
        NotificationDto dto = new NotificationDto();
        dto.setId(n.getId());
        dto.setRecipient(n.getRecipient());
        dto.setChannel(n.getChannel());
        dto.setSubject(n.getSubject());
        dto.setBody(n.getBody());
        dto.setStatus(n.getStatus());
        dto.setRetryCount(n.getRetryCount());
        dto.setMaxRetries(n.getMaxRetries());
        dto.setLastError(n.getLastError());
        dto.setNextAttemptAt(n.getNextAttemptAt());
        dto.setProcessingStartedAt(n.getProcessingStartedAt());
        dto.setCreatedAt(n.getCreatedAt());
        dto.setUpdatedAt(n.getUpdatedAt());
        return dto;
    }
}
