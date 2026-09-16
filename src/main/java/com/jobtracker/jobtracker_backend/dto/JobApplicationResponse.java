package com.jobtracker.jobtracker_backend.dto;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

import com.jobtracker.jobtracker_backend.model.ApplicationStatus;

public record JobApplicationResponse(
    UUID id,
    String company,
    String position,
    String jobUrl,
    String source,
    ApplicationStatus status,
    LocalDate applicationDate,
    String salaryRange,
    String notes,
    Instant createdAt,
    Instant updatedAt
) {}
