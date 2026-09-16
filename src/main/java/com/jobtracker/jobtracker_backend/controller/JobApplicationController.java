package com.jobtracker.jobtracker_backend.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.jobtracker.jobtracker_backend.dto.CreateJobApplicationRequest;
import com.jobtracker.jobtracker_backend.dto.JobApplicationResponse;
import com.jobtracker.jobtracker_backend.dto.UpdateJobApplicationRequest;
import com.jobtracker.jobtracker_backend.dto.UpdateStatusRequest;
import com.jobtracker.jobtracker_backend.model.ApplicationStatus;
import com.jobtracker.jobtracker_backend.service.JobApplicationService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/applications")
public class JobApplicationController {

    private final JobApplicationService jobApplicationService;

    public JobApplicationController(JobApplicationService jobApplicationService) {
        this.jobApplicationService = jobApplicationService;
    }

    @GetMapping
    public List<JobApplicationResponse> getAllApplications(
            @AuthenticationPrincipal UUID userId,
            @RequestParam(required = false) ApplicationStatus status) {
        return jobApplicationService.getAllApplications(userId, status);
    }

    @PostMapping
    public ResponseEntity<JobApplicationResponse> create(
            @AuthenticationPrincipal UUID userId,
            @Valid @RequestBody CreateJobApplicationRequest request) {
        JobApplicationResponse response = jobApplicationService.create(userId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    public JobApplicationResponse getById(
            @AuthenticationPrincipal UUID userId,
            @PathVariable UUID id) {
        return jobApplicationService.getById(userId, id);
    }

    @PutMapping("/{id}")
    public JobApplicationResponse update(
            @AuthenticationPrincipal UUID userId,
            @PathVariable UUID id,
            @Valid @RequestBody UpdateJobApplicationRequest request) {
        return jobApplicationService.update(userId, id, request);
    }

    @PatchMapping("/{id}/status")
    public JobApplicationResponse updateStatus(
            @AuthenticationPrincipal UUID userId,
            @PathVariable UUID id,
            @Valid @RequestBody UpdateStatusRequest request) {
        return jobApplicationService.updateStatus(userId, id, request.status());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @AuthenticationPrincipal UUID userId,
            @PathVariable UUID id) {
        jobApplicationService.delete(userId, id);
        return ResponseEntity.noContent().build();
    }
}
