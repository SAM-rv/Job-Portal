package com.example.Job_Portal.exception;

public class ClosedJobException extends RuntimeException {
    public ClosedJobException(String message) {
        super(message);
    }
}
