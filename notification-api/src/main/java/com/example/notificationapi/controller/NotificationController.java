package com.example.notificationapi.controller;

import com.example.notificationapi.dto.NotificationDto;
import com.example.notificationapi.dto.NotificationRequest;
import com.example.notificationapi.dto.NotificationResponse;
import com.example.notificationapi.service.NotificationService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    private final NotificationService service;
    private static final int MAX_PAGE_SIZE = 200;

    public NotificationController(NotificationService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<NotificationResponse> submit(
            @Valid @RequestBody NotificationRequest req,
            UriComponentsBuilder uriBuilder) {

        NotificationResponse resp = service.createNotification(req);

        String location = uriBuilder.path("/api/notifications/{id}")
                .buildAndExpand(resp.getId()).toUriString();

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.LOCATION, location);
        return ResponseEntity.status(HttpStatus.ACCEPTED).headers(headers).body(resp);
    }


    @GetMapping
    public ResponseEntity<Page<NotificationDto>> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        if (size < 1) size = 1;
        if (size > MAX_PAGE_SIZE) size = MAX_PAGE_SIZE;

        Page<NotificationDto> pageDto = service.getAll(PageRequest.of(page, size));
        return ResponseEntity.ok(pageDto);
    }

    @GetMapping("/{id}")
    public ResponseEntity<NotificationDto> getById(@PathVariable Long id) {
        return service.getById(id)
                .map(dto -> ResponseEntity.ok(dto))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
