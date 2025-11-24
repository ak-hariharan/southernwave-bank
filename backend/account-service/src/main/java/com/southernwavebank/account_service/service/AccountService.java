package com.southernwavebank.account_service.service;

import java.time.LocalDateTime;

import org.springframework.http.ResponseEntity;

import com.southernwavebank.account_service.model.AccountStatus;
import com.southernwavebank.account_service.model.entity.CustomPrincipal;
import com.southernwavebank.account_service.model.externaldto.TransactionDto;
import com.southernwavebank.account_service.model.externaldto.UserDto;
import com.southernwavebank.account_service.model.response.Response;

/**
 * Account service contract defining operations for account management, balance and transactions.
 * Implementations should handle persistence, events and notification publication.
 */
public interface AccountService {
	
	/**
	 * Create a new account for the given user data and timestamp.
	 * @returns Response containing created Account entity data.
	 * @throws ResourceConflict when an account already exists for the user/type.
	 */
	Response createAccount(UserDto Dto, LocalDateTime time);
	
	/**
	 * Get account details for an account number with principal-based access control.
	 * @returns ResponseEntity<Response> with Account details if authorized.
	 * @throws ResourceNotFound when account is not found, UnauthorizedAccessException when access denied.
	 */
	ResponseEntity<Response> getAccountDetails(String accountNumber, CustomPrincipal principal);
	
	/**
	 * Update the status of an account and return updated representation.
	 * @returns ResponseEntity<Response> containing updated AccountDto.
	 * @throws ResourceNotFound when the account cannot be found.
	 */
	ResponseEntity<Response> updateAccountStatus(String accountNumber, AccountStatus status);
	
	/**
	 * Retrieve the balance for an account and optionally emit a notification event.
	 * @returns ResponseEntity<Response> containing the available balance.
	 * @throws ResourceNotFound when the account cannot be found, UnauthorizedAccessException when access denied.
	 */
	ResponseEntity<Response> getBalance(String accountNumber, LocalDateTime time, CustomPrincipal principal);
	
	/**
	 * Deposit money into an account, persist changes and publish transaction event.
	 * @returns ResponseEntity<Response> with deposit result details.
	 * @throws ResourceNotFound when account is not found, UnauthorizedAccessException when access denied.
	 */
	ResponseEntity<Response> depositMoney(TransactionDto transactionDto, LocalDateTime time, CustomPrincipal principal);

	/**
	 * Withdraw money from an account, persist changes and publish transaction event.
	 * @returns ResponseEntity<Response> with withdrawal result details.
	 * @throws ResourceNotFound, UnauthorizedAccessException, InsufficientBalanceException on failure.
	 */
	ResponseEntity<Response> withdrawMoney(TransactionDto transactionDto, LocalDateTime time, CustomPrincipal principal);

	/**
	 * Fetch the account number belonging to the provided user id (internal use).
	 * @returns ResponseEntity<String> containing the account number.
	 * @throws ResourceNotFound when no account exists for the user.
	 */
	ResponseEntity<String> fetchAccountNumberByUserId(String userId);
	
}
