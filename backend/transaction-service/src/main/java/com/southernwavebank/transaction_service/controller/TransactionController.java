package com.southernwavebank.transaction_service.controller;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import com.southernwavebank.transaction_service.model.entity.CustomPrincipal;
import com.southernwavebank.transaction_service.model.response.Response;
import com.southernwavebank.transaction_service.service.TransactionService;

import lombok.extern.slf4j.Slf4j;

/**
 * REST controller exposing transaction history endpoints for deposits/withdrawals and retrieval.
 * Delegates business logic to TransactionService and handles HTTP request mapping.
 */
@RestController
@RequestMapping("/swb/transaction")
@Slf4j
public class TransactionController {
	
	private TransactionService transactionService;

	@Autowired
	public TransactionController(TransactionService transactionService) {
		this.transactionService = transactionService;
	}
	
	/**
	 * Retrieve transaction history for an account, optionally within a date range.
	 * @returns ResponseEntity<Response> containing transaction list or empty list.
	 * @throws UnauthorizedAccessException when principal is not authorized to view the account.
	 */
	@GetMapping("/history/{accountNumber}")
	public ResponseEntity<Response> getTransactions(@PathVariable("accountNumber") String accountNumber,
			@RequestParam(name = "startDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
			@RequestParam(name = "endDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
			@AuthenticationPrincipal CustomPrincipal principal) {

		log.info("Fetching transaction history");
		ResponseEntity<Response> response = transactionService.fetchTransactions(accountNumber, startDate, endDate,
				principal, LocalDateTime.now());
		return response;
	}
}
