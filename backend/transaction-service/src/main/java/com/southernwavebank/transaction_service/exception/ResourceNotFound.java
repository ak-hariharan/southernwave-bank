package com.southernwavebank.transaction_service.exception;

public class ResourceNotFound extends RuntimeException {
	
	private String identifier;
	
	public ResourceNotFound(String identifier) {
		super("Account not found for account number");
		this.identifier = identifier;
	}
	
	public String getIdentifier() {
		return identifier;
	}
}
