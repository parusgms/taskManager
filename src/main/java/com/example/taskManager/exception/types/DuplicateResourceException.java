package com.example.taskManager.exception.types;

public class DuplicateResourceException extends RuntimeException {
    public DuplicateResourceException (String message) {
        super(message);
    }
}
