package com.jobtracker.jobtracker_backend.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.jobtracker.jobtracker_backend.model.Contact;

public interface ContactRepository extends JpaRepository<Contact, UUID>{
    List<Contact> findByJobApplicationId(UUID jobApplicationId);
}
