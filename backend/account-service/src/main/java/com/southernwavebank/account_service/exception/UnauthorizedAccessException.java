package com.southernwavebank.account_service.exception;

public class UnauthorizedAccessException extends RuntimeException {
	private String accountNumber;
	private String role;

	public UnauthorizedAccessException(String accountNumber, String role) {
		super("Access denied: You can only access your own account");
		this.accountNumber = accountNumber;
		this.role = role;
	}
	
	public UnauthorizedAccessException(String role) {
		super("Access denied: Officer can only acess");
		this.role = role;
	}

	public String getAccountNumber() {
		return accountNumber;
	}
	
	public String getRole() {
		return role;
	}
}

