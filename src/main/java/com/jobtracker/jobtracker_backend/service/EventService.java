package com.jobtracker.jobtracker_backend.service;


import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jobtracker.jobtracker_backend.dto.ApplicationEventResponse;
import com.jobtracker.jobtracker_backend.dto.CreateEventRequest;
import com.jobtracker.jobtracker_backend.dto.UpdateEventRequest;
import com.jobtracker.jobtracker_backend.exception.ResourceNotFoundException;
import com.jobtracker.jobtracker_backend.model.ApplicationEvent;
import com.jobtracker.jobtracker_backend.model.JobApplication;
import com.jobtracker.jobtracker_backend.repository.ApplicationEventRepository;
import com.jobtracker.jobtracker_backend.repository.JobApplicationRepository;



/**
 * CRUD operations for application events (status-change history) nested
 * under a job application. Same two-level ownership check as ContactService:
 * job application must belong to the caller, event must belong to that job
 * application, both failures return an identical 404.
 */
@Service
@Transactional (readOnly = true)
public class EventService {
    
    private final ApplicationEventRepository applicationEventRepository;
    private final JobApplicationRepository jobApplicationRepository;

    public EventService(ApplicationEventRepository applicationEventRepository, JobApplicationRepository jobApplicationRepository) {
        this.applicationEventRepository = applicationEventRepository;
        this.jobApplicationRepository = jobApplicationRepository;
    }

    private JobApplication findOwnedJobApplication(UUID userId, UUID jobApplicationId) {
        JobApplication jobApplication = jobApplicationRepository.findById(jobApplicationId)
                .orElseThrow(() -> new ResourceNotFoundException("Job application not found"));
        if (!jobApplication.getUser().getId().equals(userId)) {
            throw new ResourceNotFoundException("Job application not found");
        }

        return jobApplication;
    }

    

    private ApplicationEvent findOwnedEvent(UUID userId, UUID jobApplicationId, UUID eventId) {
        findOwnedJobApplication(userId, jobApplicationId);

        ApplicationEvent applicationEvent = applicationEventRepository.findById(eventId)
                .orElseThrow(() -> new ResourceNotFoundException("Event not found"));
        if (!applicationEvent.getJobApplication().getId().equals(jobApplicationId)) {
            throw new ResourceNotFoundException("Event not found");
        }
        return applicationEvent;
    }

    private ApplicationEventResponse toResponse(ApplicationEvent applicationEvent) {
        return new ApplicationEventResponse(
            applicationEvent.getId(),
            applicationEvent.getApplicationStatus(),
            applicationEvent.getDate(),
            applicationEvent.getNotes()
        );
    }

    public List<ApplicationEventResponse> getAll(UUID userId, UUID jobApplicationId) {
        findOwnedJobApplication(userId, jobApplicationId);
        return applicationEventRepository.findByJobApplicationIdOrderByDateDesc(jobApplicationId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public ApplicationEventResponse getById(UUID userId, UUID jobApplicationId, UUID eventId) {
        ApplicationEvent applicationEvent = findOwnedEvent(userId, jobApplicationId, eventId);
        return toResponse(applicationEvent);
    }

    @Transactional 
    public ApplicationEventResponse create(UUID userId, UUID jobApplicationId, CreateEventRequest request) {
        JobApplication jobApplication = findOwnedJobApplication(userId, jobApplicationId);
        
        ApplicationEvent newEvent = new ApplicationEvent();
        newEvent.setJobApplication(jobApplication);
        newEvent.setApplicationStatus(request.status());
        newEvent.setDate(request.eventDate());
        newEvent.setNotes(request.notes());

        applicationEventRepository.save(newEvent);
        return toResponse(newEvent);
    }

    @Transactional 
    public ApplicationEventResponse update(UUID userId, UUID jobApplicationId, UUID eventId, UpdateEventRequest request) {
        ApplicationEvent applicationEvent = findOwnedEvent(userId, jobApplicationId, eventId);
        applicationEvent.setApplicationStatus(request.status());
        applicationEvent.setDate(request.eventDate());
        applicationEvent.setNotes(request.notes());

        applicationEventRepository.save(applicationEvent);
        return toResponse(applicationEvent);
    }

    @Transactional 
    public void delete(UUID userId, UUID jobApplicationId, UUID eventId) {
        ApplicationEvent applicationEvent = findOwnedEvent(userId, jobApplicationId, eventId);
        applicationEventRepository.delete(applicationEvent);
    }
    
}
