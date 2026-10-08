package com.jobtracker.jobtracker_backend.exception;

/** Thrown for a missing resource, and reused (with an identical message) for ownership
 * violations so a non-owner can't distinguish "doesn't exist" from "not yours". */
public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String message) {
        super(message);
    }
}
