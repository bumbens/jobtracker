package com.jobtracker.jobtracker_backend.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.jobtracker.jobtracker_backend.model.ApplicationEvent;

public interface ApplicationEventRepository extends JpaRepository<ApplicationEvent, UUID>{
    List<ApplicationEvent> findByJobApplicationIdOrderByDateDesc(UUID jobApplicationId);    
}
