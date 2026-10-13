package com.jobtracker.jobtracker_backend.dto;

import java.util.UUID;

/** API-facing view of a {@link com.jobtracker.jobtracker_backend.model.Contact}. */
public record ContactResponse(
    UUID id,
    String name,
    String role,
    String email,
    String linkedInUrl
) {}
