package com.jobtracker.jobtracker_backend.model;

/** Lifecycle stages of a job application, in the order they're expected to occur. */
public enum ApplicationStatus {
    APPLIED,
    SCREENING,
    INTERVIEW,
    OFFER,
    REJECTED,
    WITHDRAWN
}
