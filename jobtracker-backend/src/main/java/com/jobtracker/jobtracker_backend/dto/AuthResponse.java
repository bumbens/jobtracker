package com.jobtracker.jobtracker_backend.dto;

import java.util.UUID;

/** Response for both register and login: issued JWT plus basic profile info. */
public record AuthResponse(
    String token,
    UUID userId,
    String email,
    String displayName
) {}
