package com.southernwavebank.user_service.exception;

public class DuplicateFieldException extends RuntimeException {

    private final String identifier;

    public DuplicateFieldException(String message, String identifier) {
        super(message);
        this.identifier = identifier;
    }

    public String getIdentifier() {
        return identifier;
    }
}
