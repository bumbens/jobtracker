package com.jobtracker.jobtracker_backend.dto;

import jakarta.validation.constraints.NotBlank;

/** Payload for {@code POST /api/applications/{applicationId}/contacts}. */
public record CreateContactRequest(
    @NotBlank String name,
    String role,
    String email,
    String linkedInUrl
) {}
