package com.example.Job_Portal.exception;

public class ExpiredDeadlineException extends RuntimeException {
    public ExpiredDeadlineException(String message) {
        super(message);
    }
}
