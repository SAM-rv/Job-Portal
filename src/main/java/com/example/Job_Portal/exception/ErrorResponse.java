package com.example.Job_Portal.exception;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.LocalDateTime;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ErrorResponse(
        int status,
        String error,
        String message,
        Map<String, String> fieldErrors,
        LocalDateTime timestamp
) {
    // Constructor for general single-message exceptions
    public ErrorResponse(int status, String error, String message) {
        this(status, error, message, null, LocalDateTime.now());
    }

    // Constructor for @Valid validation errors
    public ErrorResponse(int status, String error, Map<String, String> fieldErrors) {
        this(status, error, "Validation failed for request fields", fieldErrors, LocalDateTime.now());
    }
}