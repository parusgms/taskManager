package com.example.taskManager.exception.types;

public class GroupNotFoundException extends RuntimeException {
    public GroupNotFoundException (String message) {
        super(message);
    }
}
