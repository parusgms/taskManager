package com.example.taskManager.exception.types;

public class InvalidCredentialsException extends  RuntimeException {
    public InvalidCredentialsException (String message) {
        super(message);
    }
}
