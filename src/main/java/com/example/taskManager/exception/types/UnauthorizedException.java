package com.example.taskManager.exception.types;

public class UnauthorizedException extends RuntimeException {
    public UnauthorizedException (String message) {
        super(message);
    }
}
