package com.jobtracker.jobtracker_backend.dto;

import jakarta.validation.constraints.NotBlank;

/** Payload for {@code POST /api/auth/login}. */
public record LoginRequest(
    @NotBlank String email,
    @NotBlank String password
) {}
