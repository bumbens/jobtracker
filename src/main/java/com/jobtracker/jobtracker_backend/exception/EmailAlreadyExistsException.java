package com.jobtracker.jobtracker_backend.exception;

/** Thrown on registration when the email is already taken. Mapped to 409 Conflict. */
public class EmailAlreadyExistsException extends RuntimeException {
    public EmailAlreadyExistsException(String message) {
        super(message);
    }
}
