package com.jobtracker.jobtracker_backend.dto;

import java.util.Map;

import com.jobtracker.jobtracker_backend.model.ApplicationStatus;

/**
 * Aggregate stats for the authenticated user's applications: counts per status,
 * response rate (share moved past APPLIED), and average days to first response.
 */
public record DashboardStatsResponse(
    Map<ApplicationStatus, Long> countsByStatus,
    long totalApplications,
    double responseRate,
    Double averageResponse
) {
    
}
