package com.southernwavebank.account_service.exception;

import java.math.BigDecimal;

public class InsufficientBalanceException extends RuntimeException {
	private BigDecimal amount;

	public InsufficientBalanceException(BigDecimal amount) {
		super("Failed to process the withdraw amount, your available balance is low");
		this.amount = amount;
	}

	public BigDecimal getAmount() {
		return amount;
	}
}
