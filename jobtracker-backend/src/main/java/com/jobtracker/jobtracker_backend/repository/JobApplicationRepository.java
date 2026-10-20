package com.jobtracker.jobtracker_backend.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.jobtracker.jobtracker_backend.model.ApplicationStatus;
import com.jobtracker.jobtracker_backend.model.JobApplication;

/** Data access for {@link JobApplication} entities, including per-status counts for the dashboard. */
public interface JobApplicationRepository extends JpaRepository<JobApplication, UUID> {
    List<JobApplication> findByUserId(UUID userId);
    Page<JobApplication> findByUserId(UUID userId, Pageable pageable);
    Page<JobApplication> findByUserIdAndStatus(UUID userId, ApplicationStatus status, Pageable pageable);
    long countByUserIdAndStatus(UUID userId, ApplicationStatus status);
}
