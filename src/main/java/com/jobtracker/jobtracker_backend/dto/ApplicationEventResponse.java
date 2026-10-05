package com.jobtracker.jobtracker_backend.dto;

import java.time.LocalDate;
import java.util.UUID;

import com.jobtracker.jobtracker_backend.model.ApplicationStatus;

public record ApplicationEventResponse(
    UUID id,
    ApplicationStatus applicationStatus,
    LocalDate date,
    String notes
) {
    
}
