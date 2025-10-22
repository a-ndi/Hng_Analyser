package com.hng.Analyser.Service.errors;


public class NewResponseStatusException extends RuntimeException{
    public NewResponseStatusException(String message) {
        super(message);
    }
}
