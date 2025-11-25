package com.southernwavebank.transaction_service.exception;

public class UnauthorizedAccessException extends RuntimeException {
	private String identifier;
	
	public UnauthorizedAccessException(String identifier) {
		super("Access denied: You can only access your own account.");
		this.identifier = identifier;
	}
	
	public String getIdentifier() {
	    return identifier;
	}
}
