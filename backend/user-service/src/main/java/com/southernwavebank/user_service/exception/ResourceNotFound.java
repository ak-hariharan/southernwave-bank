package com.southernwavebank.user_service.exception;


public class ResourceNotFound extends RuntimeException {
	private String identifier;

	public ResourceNotFound(String identifier) {
	    super("Resource not found for: " + identifier);
	    this.identifier = identifier;
	}
	
	public ResourceNotFound(Long identifier) {
        super("Resource not found for: " + identifier);
        this.identifier = identifier.toString(); 
    }

	public String getIdentifier() {
	    return identifier;
	}

}
