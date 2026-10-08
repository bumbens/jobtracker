package com.jobtracker.jobtracker_backend.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.jobtracker.jobtracker_backend.dto.ApplicationEventResponse;
import com.jobtracker.jobtracker_backend.dto.CreateEventRequest;
import com.jobtracker.jobtracker_backend.dto.UpdateEventRequest;
import com.jobtracker.jobtracker_backend.service.EventService;

import jakarta.validation.Valid;

/** REST endpoints for status-change events nested under a job application. */
@RestController
@RequestMapping("/api/applications/{applicationId}/events")
public class EventController {

    private final EventService eventService;

    public EventController(EventService eventService) {
        this.eventService = eventService;
    }

    @GetMapping
    public List<ApplicationEventResponse> getAll(@AuthenticationPrincipal UUID userId,
            @PathVariable UUID applicationId) {
        return eventService.getAll(userId, applicationId);
    }

    @PostMapping
    public ResponseEntity<ApplicationEventResponse> create(@AuthenticationPrincipal UUID userId,
            @PathVariable UUID applicationId, @Valid @RequestBody CreateEventRequest request) {
        ApplicationEventResponse response = eventService.create(userId, applicationId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{eventId}")
    public ApplicationEventResponse get(@AuthenticationPrincipal UUID userId, @PathVariable UUID applicationId,
            @PathVariable UUID eventId) {
        return eventService.getById(userId, applicationId, eventId);
    }

    @PutMapping("/{eventId}")
    public ApplicationEventResponse update(@AuthenticationPrincipal UUID userId, @PathVariable UUID applicationId,
            @PathVariable UUID eventId, @Valid @RequestBody UpdateEventRequest request) {
        return eventService.update(userId, applicationId, eventId, request);
    }

    @DeleteMapping("/{eventId}")
    public ResponseEntity<Void> delete(@AuthenticationPrincipal UUID userId, @PathVariable UUID applicationId,
            @PathVariable UUID eventId) {
        eventService.delete(userId, applicationId, eventId);
        return ResponseEntity.noContent().build();
    }
}
