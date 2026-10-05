package com.jobtracker.jobtracker_backend.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.jobtracker.jobtracker_backend.dto.ContactResponse;
import com.jobtracker.jobtracker_backend.dto.CreateContactRequest;
import com.jobtracker.jobtracker_backend.dto.UpdateContactRequest;
import com.jobtracker.jobtracker_backend.service.ContactService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/applications/{applicationId}/contacts")
public class ContactController {
    private final ContactService contactService;

    public ContactController(ContactService contactService) {
        this.contactService = contactService;
    }

    @GetMapping
    public List<ContactResponse> getAll(@AuthenticationPrincipal UUID userId, @PathVariable UUID applicationId) {
        return contactService.getAll(userId, applicationId);
    }

    @PostMapping
    public ResponseEntity<ContactResponse> create(@AuthenticationPrincipal UUID userId,
            @PathVariable UUID applicationId, @Valid @RequestBody CreateContactRequest request) {
        ContactResponse response = contactService.create(userId, applicationId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{contactId}")
    public ContactResponse get(
            @AuthenticationPrincipal UUID userId,
            @PathVariable UUID applicationId, @PathVariable UUID contactId) {
        return contactService.getById(userId, applicationId, contactId);
    }

    @PutMapping ("/{contactId}")
    public ContactResponse update(
            @AuthenticationPrincipal UUID userId,
            @PathVariable UUID applicationId, @PathVariable UUID contactId,
            @Valid @RequestBody UpdateContactRequest request) {
        return contactService.update(userId, applicationId, contactId, request);
    }

    @DeleteMapping ("/{contactId}")
    public ResponseEntity<Void> delete(
            @AuthenticationPrincipal UUID userId,
            @PathVariable UUID applicationId, @PathVariable UUID contactId) {
        contactService.delete(userId, applicationId, contactId);
        return ResponseEntity.noContent().build();
    }
}
