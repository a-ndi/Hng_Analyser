package com.hng.Analyser.Service.errors;


// 409 Conflict
public class ResourceConflictException extends RuntimeException {
    public ResourceConflictException(String message) {
        super(message);
    }
}
