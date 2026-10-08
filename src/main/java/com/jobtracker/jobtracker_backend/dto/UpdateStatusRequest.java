package com.jobtracker.jobtracker_backend.dto;

import com.jobtracker.jobtracker_backend.model.ApplicationStatus;

import jakarta.validation.constraints.NotNull;

/** Payload for {@code PATCH /api/applications/{id}/status} — the only way to change status. */
public record UpdateStatusRequest(
    @NotNull ApplicationStatus status
) {}
