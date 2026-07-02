package com.example.taskManager.exception.types;

public class PermissionDeniedException extends RuntimeException {
    public PermissionDeniedException (String message) {
        super(message);
    }
}
