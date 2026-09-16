package com.jobtracker.jobtracker_backend.dto;

import java.time.LocalDate;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateJobApplicationRequest(
    @NotBlank String company,
    @NotBlank String position,
    String jobUrl,
    String source,
    @NotNull LocalDate applicationDate,
    String salaryRange,
    String notes
) {}
