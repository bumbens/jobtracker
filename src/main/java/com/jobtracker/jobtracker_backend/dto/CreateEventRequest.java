package com.jobtracker.jobtracker_backend.dto;

import java.time.LocalDate;

import com.jobtracker.jobtracker_backend.model.ApplicationStatus;

import jakarta.validation.constraints.NotNull;

public record CreateEventRequest(
        @NotNull ApplicationStatus status,
        @NotNull LocalDate eventDate,
        String notes) {
}
