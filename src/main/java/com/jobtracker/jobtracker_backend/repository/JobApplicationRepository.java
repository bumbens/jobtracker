package com.jobtracker.jobtracker_backend.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.jobtracker.jobtracker_backend.model.ApplicationStatus;
import com.jobtracker.jobtracker_backend.model.JobApplication;

public interface JobApplicationRepository extends JpaRepository<JobApplication, UUID> {
    List<JobApplication> findByUserId(UUID userId);
    List<JobApplication> findByUserIdAndStatus(UUID userId, ApplicationStatus status);
    long countByUserIdAndStatus(UUID userId, ApplicationStatus status);
}
