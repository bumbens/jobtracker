package com.jobtracker.jobtracker_backend.exception;

/** Thrown on login failure. Mapped to 401; message is deliberately generic (no "wrong password" vs "no such user" distinction). */
public class InvalidCredentialsException extends RuntimeException {
    public InvalidCredentialsException(String message) {
        super(message);
    }
    
}
