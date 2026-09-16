package com.jobtracker.jobtracker_backend.service;

import java.util.List;
import java.util.UUID;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jobtracker.jobtracker_backend.dto.CreateJobApplicationRequest;
import com.jobtracker.jobtracker_backend.dto.JobApplicationResponse;
import com.jobtracker.jobtracker_backend.dto.UpdateJobApplicationRequest;
import com.jobtracker.jobtracker_backend.exception.ResourceNotFoundException;
import com.jobtracker.jobtracker_backend.model.ApplicationStatus;
import com.jobtracker.jobtracker_backend.model.JobApplication;
import com.jobtracker.jobtracker_backend.repository.JobApplicationRepository;
import com.jobtracker.jobtracker_backend.repository.UserRepository;

@Service 
@Transactional (readOnly = true)
public class JobApplicationService {

    private final JobApplicationRepository jobApplicationRepository;
    private final UserRepository userRepository;

    public JobApplicationService(JobApplicationRepository jobApplicationRepository, UserRepository userRepository) {
        this.jobApplicationRepository = jobApplicationRepository;
        this.userRepository = userRepository;
    }

    private JobApplicationResponse toResponse(JobApplication jobApplication) {
        return new JobApplicationResponse(
            jobApplication.getId(),
            jobApplication.getCompany(),
            jobApplication.getPosition(),
            jobApplication.getJobUrl(),
            jobApplication.getSource(),
            jobApplication.getStatus(),
            jobApplication.getAppliedDate(),
            jobApplication.getSalaryRange(),
            jobApplication.getNotes(),
            jobApplication.getCreatedAt(),
            jobApplication.getUpdatedAt()
        );
    }

    private JobApplication findOwned(UUID userId, UUID applicationId) {
        JobApplication jobApplication = jobApplicationRepository.findById(applicationId)
                .orElseThrow(() -> new ResourceNotFoundException("Job application not found"));
        if (!jobApplication.getUser().getId().equals(userId)) {
            throw new AccessDeniedException("Job application does not belong to user");
        }
        return jobApplication;
    }
    
    @Transactional 
    public JobApplicationResponse create (UUID userId, CreateJobApplicationRequest request) {
        JobApplication jobApplication = new JobApplication();
        jobApplication.setUser(userRepository.getReferenceById(userId));
        jobApplication.setCompany(request.company());
        jobApplication.setPosition(request.position());
        jobApplication.setJobUrl(request.jobUrl());
        jobApplication.setSource(request.source());
        jobApplication.setStatus(ApplicationStatus.APPLIED);
        jobApplication.setAppliedDate(request.applicationDate());
        jobApplication.setSalaryRange(request.salaryRange());
        jobApplication.setNotes(request.notes());

        jobApplicationRepository.saveAndFlush(jobApplication);
        return toResponse(jobApplication);
    }


    public JobApplicationResponse getById(UUID userId, UUID applicationId) {
        return toResponse(findOwned(userId, applicationId));
    }
    
    public List<JobApplicationResponse> getAllApplications(UUID userId, ApplicationStatus status) {
        List<JobApplication> applications = (status != null)
            ? jobApplicationRepository.findByUserIdAndStatus(userId, status)
            : jobApplicationRepository.findByUserId(userId);

        return applications.stream()
            .map(this::toResponse)
            .toList();
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
        return toResponse(jobApplication);
    }

    @Transactional 
    public void delete(UUID userId, UUID applicationId) {
        JobApplication jobApplication = findOwned(userId, applicationId);
        jobApplicationRepository.delete(jobApplication);
    }
    
}
