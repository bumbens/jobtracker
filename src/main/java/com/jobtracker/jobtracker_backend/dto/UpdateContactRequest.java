package com.jobtracker.jobtracker_backend.dto;

import jakarta.validation.constraints.NotBlank;

public record UpdateContactRequest(
        @NotBlank String name,
        String role,
        String email,
        String linkedInUrl) {
}
