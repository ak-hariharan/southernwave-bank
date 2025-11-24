package com.southernwavebank.account_service.controller;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import com.southernwavebank.account_service.model.AccountStatus;
import com.southernwavebank.account_service.model.entity.CustomPrincipal;
import com.southernwavebank.account_service.model.externaldto.TransactionDto;
import com.southernwavebank.account_service.model.response.Response;
import com.southernwavebank.account_service.service.AccountService;

import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.slf4j.Slf4j;


/**
 * REST controller exposing account management endpoints for balance, transactions and status updates.
 * Delegates business logic to AccountService and handles request/response mapping.
 */
@RestController
@RequestMapping("/swb/account")
@Slf4j
public class AccountController {
	
	
	private AccountService accountService;

	@Autowired
	public AccountController(AccountService accountService) {
		this.accountService = accountService;
	}

	/**
	 * Get account details for the specified account number.
	 * @returns ResponseEntity<Response> containing AccountDto when authorized.
	 * @throws RuntimeException when service layer raises not-found or unauthorized exceptions.
	 */
	@GetMapping("/details/{accountNumber}")
	public ResponseEntity<Response> getAccountDetails(@PathVariable String accountNumber, @AuthenticationPrincipal CustomPrincipal principal) {
		log.info("Processing the AccountDetails in service");
		ResponseEntity<Response> response = accountService.getAccountDetails(accountNumber, principal);
		log.info("Account details fetching process end");
		return response;
	}

	
	/**
	 * Update account status.
	 * @returns ResponseEntity<Response> with updated account information.
	 * @throws RuntimeException when account not found or update fails.
	 */
	@PutMapping("/update/{accountNumber}")
	public ResponseEntity<Response> updateAccountStatus(@PathVariable String accountNumber, @RequestParam AccountStatus status) {
		log.info("Update request received for account status");
		ResponseEntity<Response> response = accountService.updateAccountStatus(accountNumber, status);
		log.info("Account status updated successfully. Returning response");
		return response;
	}


	/**
	 * Retrieve available balance for the account and publish notification when applicable.
	 * @returns ResponseEntity<Response> containing balance amount.
	 * @throws RuntimeException when account not found or access denied.
	 */
	@GetMapping("/balance/{accountNumber}")
	public ResponseEntity<Response> getBalance(@PathVariable String accountNumber, @AuthenticationPrincipal CustomPrincipal principal)
	{
		log.info("Balance check initiated");
		ResponseEntity<Response> response = accountService.getBalance(accountNumber, LocalDateTime.now(), principal);
		log.info("Balance fetched successfully. Returning response");
		return response;	
	}
	
	
	/**
	 * Deposit an amount into an account.
	 * @returns ResponseEntity<Response> containing deposit confirmation and details.
	 * @throws RuntimeException when account not found or access denied.
	 */
	@PostMapping("/deposit")
	public ResponseEntity<Response> depositMoney(@Validated @RequestBody TransactionDto transaction, @AuthenticationPrincipal CustomPrincipal principal) {
		log.info("Deposit request received");
		ResponseEntity<Response> response = accountService.depositMoney(transaction, LocalDateTime.now(), principal);
		log.info("Deposit completed. Returning response");
		return response;
	}
	
	/**
	 * Withdraw an amount from an account.
	 * @returns ResponseEntity<Response> containing withdrawal confirmation and details.
	 * @throws RuntimeException when account not found, unauthorized, or insufficient balance.
	 */
	@PostMapping("/withdraw")
	public ResponseEntity<Response> withdrawMoney( @Validated @RequestBody TransactionDto transaction, @AuthenticationPrincipal CustomPrincipal principal)
	{
		log.info("Withdraw request received");
        ResponseEntity<Response> response = accountService.withdrawMoney(transaction, LocalDateTime.now(), principal);
        log.info("Withdrawal completed. Returning response");
        return response;	
    }
	
	/**
	 * Internal endpoint used by transaction service to fetch account number by user id.
	 * @returns ResponseEntity<String> with the account number for the user id.
	 * @throws RuntimeException when no account exists for the given user id.
	 */
	@GetMapping("/accountnumber/{userId}")
	@Operation(hidden = true)
	public ResponseEntity<String> fetchAccountNumberByUserId(@PathVariable String userId) {
		log.info("Fetching account number for user");
		ResponseEntity<String> accountNumber = accountService.fetchAccountNumberByUserId(userId);
		log.info("Account number fetched successfully");
		return accountNumber;
	}
	
}
