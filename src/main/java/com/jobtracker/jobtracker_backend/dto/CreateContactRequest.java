package com.jobtracker.jobtracker_backend.dto;

import jakarta.validation.constraints.NotBlank;

public record CreateContactRequest(
    @NotBlank String name,
    String role,
    String email,
    String linkedInUrl
) {}
