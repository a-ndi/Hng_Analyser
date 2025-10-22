package com.hng.Analyser.Service;


public class NewResponseStatusException extends RuntimeException{
    public NewResponseStatusException(String message) {
        super(message);
    }

    public NewResponseStatusException(String message, Throwable cause) {
        super(message, cause);
    }
}
