package com.southernwavebank.transaction_service.service;

import java.time.LocalDateTime;

import org.springframework.http.ResponseEntity;

import com.southernwavebank.transaction_service.model.dto.TransactionDto;
import com.southernwavebank.transaction_service.model.entity.CustomPrincipal;
import com.southernwavebank.transaction_service.model.response.Response;

/**
 * Transaction service contract for saving deposit/withdraw events and retrieving history.
 * Implementations should handle persistence, idempotency and notification publication.
 */
public interface TransactionService {
	/**
	 * Handle incoming deposit transaction payload and persist it.
	 * @returns void (persists transaction and sends events if successful).
	 * @throws none (runtime exceptions may propagate).
	 */
	public void depositMoney(TransactionDto transactionDto);

	/**
	 * Handle incoming withdrawal transaction payload and persist it.
	 * @returns void (persists transaction and sends events if successful).
	 * @throws none (runtime exceptions may propagate).
	 */
	public void withdrawMoney(TransactionDto transactionDto);

	/**
	 * Fetch transaction history for account, optionally filtered by date range.
	 * @returns ResponseEntity<Response> containing a list of TransactionDto or empty list.
	 * @throws UnauthorizedAccessException when principal is not authorized to view the account.
	 */
	public ResponseEntity<Response> fetchTransactions(String accountNumber, LocalDateTime startDate,
			LocalDateTime endDate, CustomPrincipal principal, LocalDateTime time);
}
