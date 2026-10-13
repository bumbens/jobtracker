package com.jobtracker.jobtracker_backend.dto;

import jakarta.validation.constraints.NotBlank;

/** Payload for {@code PUT /api/applications/{applicationId}/contacts/{contactId}}. */
public record UpdateContactRequest(
        @NotBlank String name,
        String role,
        String email,
        String linkedInUrl) {
}
