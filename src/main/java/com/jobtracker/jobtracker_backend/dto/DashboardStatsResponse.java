package com.jobtracker.jobtracker_backend.dto;

import java.util.Map;

import com.jobtracker.jobtracker_backend.model.ApplicationStatus;

public record DashboardStatsResponse(
    Map<ApplicationStatus, Long> countsByStatus,
    long totalApplications,
    double responseRate,
    Double averageResponse
) {
    
}
