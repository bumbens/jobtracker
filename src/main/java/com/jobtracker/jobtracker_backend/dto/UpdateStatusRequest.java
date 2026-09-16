package com.jobtracker.jobtracker_backend.dto;

import com.jobtracker.jobtracker_backend.model.ApplicationStatus;

import jakarta.validation.constraints.NotNull;

public record UpdateStatusRequest(
    @NotNull ApplicationStatus status
) {}
