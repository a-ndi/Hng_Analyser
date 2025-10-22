package com.hng.Analyser.Service.errors;


// 422 Unprocessable Entity
public class InvalidDataTypeException extends RuntimeException {
    public InvalidDataTypeException(String message) {
        super(message);
    }
}