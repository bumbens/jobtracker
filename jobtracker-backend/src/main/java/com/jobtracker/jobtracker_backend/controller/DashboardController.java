package com.jobtracker.jobtracker_backend.controller;

import java.util.UUID;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.jobtracker.jobtracker_backend.dto.DashboardStatsResponse;
import com.jobtracker.jobtracker_backend.service.DashboardService;

/** Single read-only endpoint exposing aggregate stats for the authenticated user. */
@RestController
@RequestMapping ("/api/dashboard")
public class DashboardController {
    private final DashboardService dashboardService;

    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping ("/stats")
    public DashboardStatsResponse getStats(@AuthenticationPrincipal UUID userId) {
        return dashboardService.getStats(userId);
    }
}
