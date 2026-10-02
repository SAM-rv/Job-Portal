package com.example.Job_Portal.exception;

public class UnAuthorizedOperationException extends RuntimeException {
    public UnAuthorizedOperationException(String message) {
        super(message);
    }
}
