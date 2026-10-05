package com.jobtracker.jobtracker_backend.service;

import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jobtracker.jobtracker_backend.dto.DashboardStatsResponse;
import com.jobtracker.jobtracker_backend.model.ApplicationEvent;
import com.jobtracker.jobtracker_backend.model.ApplicationStatus;
import com.jobtracker.jobtracker_backend.model.JobApplication;
import com.jobtracker.jobtracker_backend.repository.ApplicationEventRepository;
import com.jobtracker.jobtracker_backend.repository.JobApplicationRepository;

@Service 
public class DashboardService {
    
    private final JobApplicationRepository jobApplicationRepository;
    private final ApplicationEventRepository applicationEventRepository;

    public DashboardService(JobApplicationRepository jobApplicationRepository, ApplicationEventRepository applicationEventRepository) {
        this.jobApplicationRepository = jobApplicationRepository;
        this.applicationEventRepository = applicationEventRepository;
    }

    @Transactional (readOnly = true)
    public DashboardStatsResponse getStats(UUID userId){
        List<JobApplication> jobApplications = jobApplicationRepository.findByUserId(userId);

        Map<ApplicationStatus, Long> countsByStatus = new EnumMap<>(ApplicationStatus.class);
        for (ApplicationStatus status : ApplicationStatus.values()) {
            countsByStatus.put(status, jobApplicationRepository.countByUserIdAndStatus(userId, status));
        }

        long totalApplications = jobApplications.size();
        long respondedApplications = jobApplications.stream()
                .filter(app -> app.getStatus() != ApplicationStatus.APPLIED)
                .count();

        double responseRate = totalApplications == 0 ? 0 : (respondedApplications * 100.0) / totalApplications;
        
        List<Long> daysToResponse = new ArrayList<>();
        for (JobApplication app : jobApplications) {
            List<ApplicationEvent> events = applicationEventRepository.findByJobApplicationIdOrderByDateDesc(app.getId());

            events.stream()
                .filter(event -> event.getApplicationStatus() != ApplicationStatus.APPLIED)
                .min(Comparator.comparing(ApplicationEvent::getDate))
                .ifPresent(firstResponse -> {
                    long days = ChronoUnit.DAYS.between(app.getAppliedDate(), firstResponse.getDate());
                    daysToResponse.add(days);
                });
        }

        Double avgDaysToResponse = daysToResponse.isEmpty()
            ? null
            : daysToResponse.stream().mapToLong(Long::longValue).average().orElse(0);

        return new DashboardStatsResponse(countsByStatus, totalApplications, responseRate, avgDaysToResponse);

        }

    
    

}
