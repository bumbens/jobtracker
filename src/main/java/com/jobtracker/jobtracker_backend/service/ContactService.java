package com.jobtracker.jobtracker_backend.service;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jobtracker.jobtracker_backend.dto.ContactResponse;
import com.jobtracker.jobtracker_backend.dto.CreateContactRequest;
import com.jobtracker.jobtracker_backend.dto.UpdateContactRequest;
import com.jobtracker.jobtracker_backend.exception.ResourceNotFoundException;
import com.jobtracker.jobtracker_backend.model.Contact;
import com.jobtracker.jobtracker_backend.model.JobApplication;
import com.jobtracker.jobtracker_backend.repository.ContactRepository;
import com.jobtracker.jobtracker_backend.repository.JobApplicationRepository;

/**
 * CRUD operations for contacts nested under a job application. Ownership is
 * checked two levels deep: the job application must belong to the caller,
 * and the contact must belong to that job application. Both failure cases
 * return an identical 404 so a non-owner can't tell "doesn't exist" apart
 * from "exists, but isn't yours".
 */
@Service
@Transactional (readOnly = true)
public class ContactService {

    private final ContactRepository contactRepository;
    private final JobApplicationRepository jobApplicationRepository;

    public ContactService(ContactRepository contactRepository, JobApplicationRepository jobApplicationRepository) {
        this.contactRepository = contactRepository;
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

    private Contact findOwnedContact(UUID userId, UUID jobApplicationId, UUID contactId) {
        findOwnedJobApplication(userId, jobApplicationId);

        Contact contact = contactRepository.findById(contactId)
                .orElseThrow(() -> new ResourceNotFoundException("Contact not found"));

        if(!contact.getJobApplication().getId().equals(jobApplicationId)) {
            throw new ResourceNotFoundException("Contact not found");
        }

        return contact;
    }

    private ContactResponse toResponse(Contact contact) {
        return new ContactResponse(
            contact.getId(),
            contact.getName(),
            contact.getRole(),
            contact.getEmail(),
            contact.getLinkedInUrl()
        );
    }

    public List<ContactResponse> getAll(UUID userId, UUID jobApplicationId) {
        findOwnedJobApplication(userId, jobApplicationId);
        return contactRepository.findByJobApplicationId(jobApplicationId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public ContactResponse getById(UUID userId, UUID jobApplicationId, UUID contactId) {
        Contact contact = findOwnedContact(userId, jobApplicationId, contactId);
        return toResponse(contact);
    }

    @Transactional 
    public ContactResponse create(UUID userId, UUID jobApplicationId, CreateContactRequest request){
        JobApplication jobApplication = findOwnedJobApplication(userId, jobApplicationId);

        Contact contact = new Contact();
        contact.setJobApplication(jobApplication);
        contact.setName(request.name());
        contact.setRole(request.role());
        contact.setEmail(request.email());
        contact.setLinkedInUrl(request.linkedInUrl());

        contactRepository.save(contact);
        return toResponse(contact);
    }

    @Transactional
    public ContactResponse update(UUID userId, UUID jobApplicationId, UUID contactId, UpdateContactRequest request) {
        Contact contact = findOwnedContact(userId, jobApplicationId, contactId);

        contact.setName(request.name());
        contact.setRole(request.role());
        contact.setEmail(request.email());
        contact.setLinkedInUrl(request.linkedInUrl());

        contactRepository.save(contact);
        return toResponse(contact);
    }

    @Transactional
    public void delete(UUID userId, UUID jobApplicationId, UUID contactId) {
        Contact contact = findOwnedContact(userId, jobApplicationId, contactId);
        contactRepository.delete(contact);
    }

}
