package com.example.notificationapi.dto;

import com.example.notificationapi.entity.Status;

public class NotificationResponse {
    private Long id;
    private Status status;

    public NotificationResponse() {}
    public NotificationResponse(Long id, Status status) {
        this.id = id;
        this.status = status;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Status getStatus() { return status; }
    public void setStatus(Status status) { this.status = status; }
}
