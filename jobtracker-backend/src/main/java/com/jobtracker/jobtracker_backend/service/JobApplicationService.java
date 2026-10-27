package com.jobtracker.jobtracker_backend.service;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jobtracker.jobtracker_backend.dto.CreateEventRequest;
import com.jobtracker.jobtracker_backend.dto.CreateJobApplicationRequest;
import com.jobtracker.jobtracker_backend.dto.JobApplicationResponse;
import com.jobtracker.jobtracker_backend.dto.UpdateJobApplicationRequest;
import com.jobtracker.jobtracker_backend.exception.ResourceNotFoundException;
import com.jobtracker.jobtracker_backend.model.ApplicationEvent;
import com.jobtracker.jobtracker_backend.model.ApplicationStatus;
import com.jobtracker.jobtracker_backend.model.JobApplication;
import com.jobtracker.jobtracker_backend.repository.ApplicationEventRepository;
import com.jobtracker.jobtracker_backend.repository.JobApplicationRepository;
import com.jobtracker.jobtracker_backend.repository.UserRepository;

/**
 * CRUD operations for job applications. Every operation is scoped to the
 * authenticated user via {@link #findOwned}; a non-owner gets the same 404
 * as a non-existent application, never a 403 that would confirm ownership.
 */
@Service
@Transactional(readOnly = true)
public class JobApplicationService {

    private final JobApplicationRepository jobApplicationRepository;
    private final ApplicationEventRepository applicationEventRepository;
    private final UserRepository userRepository;

    public JobApplicationService(JobApplicationRepository jobApplicationRepository, UserRepository userRepository, ApplicationEventRepository applicationEventRepository) {
        this.jobApplicationRepository = jobApplicationRepository;
        this.userRepository = userRepository;
        this.applicationEventRepository = applicationEventRepository;
    }

    @Transactional
    public JobApplicationResponse create(UUID userId, CreateJobApplicationRequest jobApplicationRequest) {
        JobApplication jobApplication = new JobApplication();
        jobApplication.setUser(userRepository.getReferenceById(userId));
        jobApplication.setCompany(jobApplicationRequest.company());
        jobApplication.setPosition(jobApplicationRequest.position());
        jobApplication.setJobUrl(jobApplicationRequest.jobUrl());
        jobApplication.setCandidateProfileUrl(jobApplicationRequest.candidateProfileUrl());
        jobApplication.setSource(jobApplicationRequest.source());
        jobApplication.setStatus(ApplicationStatus.APPLIED);
        jobApplication.setAppliedDate(jobApplicationRequest.applicationDate());
        jobApplication.setSalaryRange(jobApplicationRequest.salaryRange());
        jobApplication.setNotes(jobApplicationRequest.notes());

        jobApplicationRepository.saveAndFlush(jobApplication);

        ApplicationEvent applicationEvent = new ApplicationEvent();
        applicationEvent.setJobApplication(jobApplication);
        applicationEvent.setApplicationStatus(ApplicationStatus.APPLIED);
        applicationEvent.setDate(jobApplicationRequest.applicationDate());

        applicationEventRepository.save(applicationEvent);
        
        return toResponse(jobApplication);
    }

    public JobApplicationResponse getById(UUID userId, UUID applicationId) {
        return toResponse(findOwned(userId, applicationId));
    }

    public Page<JobApplicationResponse> getAllApplications(UUID userId, ApplicationStatus status, Pageable pageable) {
        Page<JobApplication> applications = (status != null)
                ? jobApplicationRepository.findByUserIdAndStatus(userId, status, pageable)
                : jobApplicationRepository.findByUserId(userId, pageable);

        return applications.map(this::toResponse);
    }

    @Transactional
    public JobApplicationResponse update(UUID userId, UUID applicationId, UpdateJobApplicationRequest request) {
        JobApplication jobApplication = findOwned(userId, applicationId);

        jobApplication.setCompany(request.company());
        jobApplication.setPosition(request.position());
        jobApplication.setJobUrl(request.jobUrl());
        jobApplication.setSource(request.source());
        jobApplication.setAppliedDate(request.applicationDate());
        jobApplication.setSalaryRange(request.salaryRange());
        jobApplication.setNotes(request.notes());

        jobApplicationRepository.flush();
        return toResponse(jobApplication);
    }

    @Transactional
    public JobApplicationResponse updateStatus(UUID userId, UUID applicationId, ApplicationStatus status) {
        JobApplication jobApplication = findOwned(userId, applicationId);
        jobApplication.setStatus(status);

        jobApplicationRepository.flush();

        ApplicationEvent applicationEvent = new ApplicationEvent();
        applicationEvent.setJobApplication(jobApplication);
        applicationEvent.setApplicationStatus(status);
        applicationEvent.setDate(LocalDate.now());
        
        applicationEventRepository.save(applicationEvent);

        return toResponse(jobApplication);
    }

    @Transactional
    public void delete(UUID userId, UUID applicationId) {
        JobApplication jobApplication = findOwned(userId, applicationId);
        jobApplicationRepository.delete(jobApplication);
    }

    private JobApplicationResponse toResponse(JobApplication jobApplication) {
        return new JobApplicationResponse(
                jobApplication.getId(),
                jobApplication.getCompany(),
                jobApplication.getPosition(),
                jobApplication.getJobUrl(),
                jobApplication.getCandidateProfileUrl(),
                jobApplication.getSource(),
                jobApplication.getStatus(),
                jobApplication.getAppliedDate(),
                jobApplication.getSalaryRange(),
                jobApplication.getNotes(),
                jobApplication.getCreatedAt(),
                jobApplication.getUpdatedAt());
    }

    private JobApplication findOwned(UUID userId, UUID applicationId) {
        JobApplication jobApplication = jobApplicationRepository.findById(applicationId)
                .orElseThrow(() -> new ResourceNotFoundException("Job application not found"));
        if (!jobApplication.getUser().getId().equals(userId)) {
            throw new ResourceNotFoundException("Job application not found");
        }
        return jobApplication;
    }

}
