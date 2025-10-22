package com.hng.Analyser.Service.errors;

public class NotFoundException extends RuntimeException {
        public NotFoundException(String message) {
            super(message);
        }

}
