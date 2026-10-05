package com.jobtracker.jobtracker_backend.dto;

import java.util.UUID;

public record ContactResponse(
    UUID id,
    String name,
    String role,
    String email,
    String linkedInUrl
) {}
