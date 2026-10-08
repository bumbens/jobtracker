package com.jobtracker.jobtracker_backend.dto;

import java.time.LocalDate;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/** Payload for {@code POST /api/applications}. Status is server-assigned (always APPLIED), not part of this request. */
public record CreateJobApplicationRequest(
    @NotBlank String company,
    @NotBlank String position,
    String jobUrl,
    String source,
    @NotNull LocalDate applicationDate,
    String salaryRange,
    String notes
) {}
