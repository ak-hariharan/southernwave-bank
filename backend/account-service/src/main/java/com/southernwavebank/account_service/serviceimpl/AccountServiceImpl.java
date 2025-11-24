package com.southernwavebank.account_service.serviceimpl;

import static com.southernwavebank.account_service.model.Constants.ACC_PREFIX;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.southernwavebank.account_service.events.*;
import com.southernwavebank.account_service.exception.*;
import com.southernwavebank.account_service.kafka.*;
import com.southernwavebank.account_service.model.*;
import com.southernwavebank.account_service.model.dto.*;
import com.southernwavebank.account_service.model.entity.*;
import com.southernwavebank.account_service.model.externaldto.*;
import com.southernwavebank.account_service.model.response.Response;
import com.southernwavebank.account_service.repository.AccountRepository;
import com.southernwavebank.account_service.service.AccountService;

import lombok.extern.slf4j.Slf4j;

/**
 * Account service implementation handling account lifecycle, balance and transactions.
 * Coordinates repository operations, events, and notifications for account flows.
*/
@Slf4j
@Service  
public class AccountServiceImpl implements AccountService {
	
	private AccountRepository accountRepo;
	private ModelMapper modelMapper;
	private AccountCreatedEventProducer accountCreatedEventProducer;
	private AccountStatusProducer accountStatusProducer;
	private BalanceNotificationProducer balanceNotificationProducer;
	private ApplicationEventPublisher eventPublisher;
	
	@Autowired
	public AccountServiceImpl(AccountRepository accountRepo,AccountCreatedEventProducer accountCreatedEventProducer, AccountStatusProducer accountStatusProducer,
			BalanceNotificationProducer balanceNotificationProducer, ModelMapper modelMapper, ApplicationEventPublisher eventPublisher) {
		this.accountRepo = accountRepo;
		this.accountCreatedEventProducer = accountCreatedEventProducer;
		this.accountStatusProducer = accountStatusProducer;
		this.balanceNotificationProducer = balanceNotificationProducer;
		this.modelMapper = modelMapper;
		this.eventPublisher = eventPublisher;
	}
	

	/**
	 * Create a new savings account for a user and publish related events.
	 * @returns Response with created Account entity on success.
	 * @throws ResourceConflict when an account of the same type already exists for user.
	 */
	@Override
	@Transactional
	public Response createAccount(UserDto userDto, LocalDateTime time) {
		log.info("Starting account creation process");
		log.debug("Checking for existing account with same type for userId");
		Long userId = userDto.getUserId();
        accountRepo.findAccountByUserIdAndAccountType(userId, AccountType.SAVINGS)
                .ifPresent(account -> {
                	log.error("Account already exists for userId");
                    throw new ResourceConflict(userDto);
                });
        
        log.info("No existing account found. Proceeding to create new account");
        
        Account account = new Account();
        account.setAccountId(null);
        long number = 1_000_000_000L + Math.abs(new Random().nextLong() % 9_000_000_000L); 
        account.setAccountNumber(ACC_PREFIX + number);
        account.setAccountStatus(AccountStatus.PENDING);
        account.setAvailableBalance(BigDecimal.valueOf(0));
        account.setAccountType(AccountType.SAVINGS);
        account.setUserId(userId);
        
        //-----------sending the account created event to consumers----------------
        log.debug("Publishing account created event to Kafka topic");
        NotificationDto notificationDto = new NotificationDto(); 
        notificationDto.setAccountNumber(account.getAccountNumber());
        notificationDto.setEmailId(userDto.getEmailId());
        notificationDto.setContactNumber(userDto.getContactNumber());
        notificationDto.setTime(time);
        notificationDto.setUserId(userId);
        System.out.println(notificationDto);
        accountCreatedEventProducer.sendAccountCreatedEvent(notificationDto);
        //------------------------------------------------------------------------
        
        log.info("Saving new account to the repository");
        accountRepo.save(account);
        
        log.info("Account creation process completed successfully");
        return Response.builder()
                .data(account)
                .message("Account created successfully")
                .build();
    }
	

	/**
	 * Retrieve account details for a given account number after access checks.
	 * @returns ResponseEntity<Response> containing AccountDto if authorized.
	 * @throws ResourceNotFound when account is not found, UnauthorizedAccessException when access denied.
	 */
	@Override
	public ResponseEntity<Response> getAccountDetails(String accountNumber, CustomPrincipal principal) {
			log.info("Received request to fetch account details");

			String userId = principal.getUserId();
			String role = principal.getAuthorities().iterator().next().getAuthority();

			Optional<Account> accountOptional = accountRepo.findByAccountNumber(accountNumber);

			if (accountOptional.isEmpty()) {
				log.warn("Account not found for the provided account number");
				throw new ResourceNotFound(accountNumber);
			}

			log.debug("Account found. Validating access for role");
			Account account = accountOptional.get();

			if ("CONSUMER".equals(role) && !String.valueOf(account.getUserId()).equals(userId)) {
				log.error("Unauthorized access attempt by userId");
				throw new UnauthorizedAccessException(accountNumber, role);
			}
			AccountDto accountDto = modelMapper.map(account, AccountDto.class);
			log.info("Access validated. Returning account details for authorized request");
			return ResponseEntity.status(HttpStatus.OK).body(
					Response.builder().message("Account details retrieved successfully").data(accountDto).build());
	}
	
	/**
	 * Update an account's status and return updated account details.
	 * @returns ResponseEntity<Response> containing updated AccountDto on success.
	 * @throws ResourceNotFound when account is not found.
	 */
	@Override
	@Transactional
	public ResponseEntity<Response> updateAccountStatus(String accountNumber, AccountStatus status) {
			log.info("Received request to update account status");
			Account account = accountRepo.findByAccountNumber(accountNumber).orElseThrow(() -> {
				log.warn("Account not found during status update attempt");
				return new ResourceNotFound(accountNumber);
			});
			log.debug("Account found. Updating status");
			account.setAccountStatus(status);
			Account updatedAccount = accountRepo.save(account);
			log.info("Account has been updated");
			AccountDto accountDto = modelMapper.map(updatedAccount, AccountDto.class);
			return ResponseEntity.status(HttpStatus.OK)
					.body(Response.builder().message("Account status updated successfully").data(accountDto).build());
	}
	
	/**
	 * Return available balance for an account and optionally publish a notification event.
	 * @returns ResponseEntity<Response> containing the available balance.
	 * @throws ResourceNotFound when account is not found, UnauthorizedAccessException when access denied.
	 */
	@Override
	public ResponseEntity<Response> getBalance(String accountNumber, LocalDateTime time, CustomPrincipal principal) {
			log.info("Received request to retrieve account balance");
			String userId = principal.getUserId();
			String role = principal.getAuthorities().iterator().next().getAuthority();
			Optional<Account> accountOptional = accountRepo.findByAccountNumber(accountNumber);

			if (accountOptional.isEmpty()) {
				log.warn("Account not found during balance enquiry");
				throw new ResourceNotFound(accountNumber);
			}

			Account account = accountOptional.get();
			log.debug("Account found. Validating access for role");

			if ("CONSUMER".equals(role) && !String.valueOf(account.getUserId()).equals(userId)) {
				log.error("Unauthorized account access attempt by userId");
				throw new UnauthorizedAccessException(accountNumber, role);
			}

			log.info("Retrieving balance in given account");
			BigDecimal balance = account.getAvailableBalance();
			// -----------------------------------------------------
			if (!"OFFICER".equals(role)) {
				log.debug("Publishing balance enquiry notification to Kafka topic");
				NotificationDto notificationDto = new NotificationDto();
				notificationDto.setAccountNumber(accountNumber);
				notificationDto.setAvailableBalance(balance);
				notificationDto.setTime(time);
				notificationDto.setUserId(Long.valueOf(userId));
				balanceNotificationProducer.sendBalanceEnquiryEvent(notificationDto);
			}
			// -----------------------------------------------------
			log.info("Balance enquiry completed successfully");
			return ResponseEntity.status(HttpStatus.OK)
					.body(Response.builder().message("Available Balance").data(balance).build());
	}

	/**
	 * Process a deposit, update balance, publish transaction event and notifications.
	 * @returns ResponseEntity<Response> containing deposit response details.
	 * @throws ResourceNotFound when account is not found, UnauthorizedAccessException when access denied.
	 */
	@Override
	@Transactional
	public ResponseEntity<Response> depositMoney(TransactionDto transactionDto, LocalDateTime time,
			CustomPrincipal principal) {
			log.info("Received request to deposit money");
			String userId = principal.getUserId();
			String role = principal.getAuthorities().iterator().next().getAuthority();
			Optional<Account> accountOptional = accountRepo.findByAccountNumber(transactionDto.getAccountNumber());

			if (accountOptional.isEmpty()) {
				log.warn("Account not found during balance enquiry");
				throw new ResourceNotFound(transactionDto.getAccountNumber());
			}

			Account account = accountOptional.get();
			log.debug("Account found. Validating access for role");

			if ("CONSUMER".equals(role) && !String.valueOf(account.getUserId()).equals(userId)) {
				log.error("Unauthorized account access attempt by userId");
				throw new UnauthorizedAccessException(transactionDto.getAccountNumber(), role);
			}
			
			log.debug("Access validated. Processing deposit for account");
			account.setAvailableBalance(account.getAvailableBalance().add(transactionDto.getAmount()));
			accountRepo.save(account);
			
			if (account.getAvailableBalance().compareTo(BigDecimal.valueOf(1000)) >= 0
					&& account.getAccountStatus().equals(AccountStatus.PENDING)) {

				log.info("Account balance threshold met. Updating account status to ACTIVE");
				account.setAccountStatus(AccountStatus.ACTIVE);

				// ---------------Making the account status as active to notification
				// service------------------
				NotificationDto notificationDto = new NotificationDto();
				String accountNumber = account.getAccountNumber();
				String maskedAccount = String.format("XXXX%s", accountNumber.substring(accountNumber.length() - 4));

				notificationDto.setAccountNumber(maskedAccount);
				notificationDto.setAccountStatus(AccountStatus.ACTIVE);
				notificationDto.setUserId(account.getUserId());
				notificationDto.setTime(time);
				log.debug("Sending account status update to notification service");
				accountStatusProducer.sendAccountStatusEvent(notificationDto);
				// -------------------------------------------------------

			}

			transactionDto.setTransactionTime(time);
			transactionDto.setTransactionType(TransactionType.DEPOSIT);
			transactionDto.setUserId(account.getUserId());
			transactionDto.setTransactionReference(UUID.randomUUID().toString());
			
			log.debug("Sending deposit event to transaction service");
			eventPublisher.publishEvent(new TransactionEvent(transactionDto));

			DepositResponseDto depositResponse = new DepositResponseDto();
			modelMapper.map(transactionDto, depositResponse);

			log.info("Deposit process completed successfully");
			return ResponseEntity.status(HttpStatus.OK)
					.body(Response.builder().message("Deposited amount").data(depositResponse).build());
	}

	/**
	 * Process a withdrawal, update balance, publish transaction event and return response.
	 * @returns ResponseEntity<Response> containing withdrawal response details.
	 * @throws ResourceNotFound when account is not found, UnauthorizedAccessException or InsufficientBalanceException on failure.
	 */
	@Override
	@Transactional
	public ResponseEntity<Response> withdrawMoney(TransactionDto transactionDto, LocalDateTime time,
			CustomPrincipal principal) {
			log.info("Received request to withdraw money");
			String userId = principal.getUserId();
			String role = principal.getAuthorities().iterator().next().getAuthority();
			Optional<Account> accountOptional = accountRepo.findByAccountNumber(transactionDto.getAccountNumber());

			if (accountOptional.isEmpty()) {
				log.warn("Account not found during withdrawal attempt");
				throw new ResourceNotFound(
						"Account not found for account number: " + transactionDto.getAccountNumber());
			}

			Account account = accountOptional.get();
			log.debug("Account found. Validating access for role");

			if ("CONSUMER".equals(role) && !String.valueOf(account.getUserId()).equals(userId)) {
				log.error("Unauthorized withdrawal attempt");
				throw new UnauthorizedAccessException(transactionDto.getAccountNumber(), role);
			}

			if (account.getAvailableBalance().subtract(transactionDto.getAmount())
					.compareTo(BigDecimal.valueOf(100)) < 0
					&& account.getAccountStatus().equals(AccountStatus.ACTIVE)) {
				log.warn("Insufficient balance or inactive account. Withdrawal denied");
				throw new InsufficientBalanceException(transactionDto.getAmount());
			}
			
			account.setAvailableBalance(account.getAvailableBalance().subtract(transactionDto.getAmount()));
			accountRepo.save(account);
			
			
			transactionDto.setTransactionTime(time);
			transactionDto.setTransactionType(TransactionType.WITHDRAW);
			transactionDto.setUserId(account.getUserId());
			transactionDto.setTransactionReference(UUID.randomUUID().toString());
		
			log.debug("Sending withdrawal event to transaction service");
			eventPublisher.publishEvent(new TransactionEvent(transactionDto));
			
			WithdrawResponseDto withdrawResponse = new WithdrawResponseDto();
			modelMapper.map(transactionDto, withdrawResponse);
		
			log.info("Withdrawal process completed successfully");
			return ResponseEntity.status(HttpStatus.OK)
					.body(Response.builder().message("Withdraw amount").data(withdrawResponse).build());
	}


	/**
	 * Fetch the account number associated with a user id for internal verification.
	 * @returns ResponseEntity<String> containing the account number.
	 * @throws ResourceNotFound when no account exists for the given user id.
	 */
	@Override
	public ResponseEntity<String> fetchAccountNumberByUserId(String userId) {
		
		Long userIdOfAccountNumber = Long.valueOf(userId);
		Account account = accountRepo.findAccountNumberByUserId(userIdOfAccountNumber).orElseThrow(() -> {
			log.warn("Account not found during status update attempt");
			return new ResourceNotFound(userIdOfAccountNumber);
		});
		
		String accountNumber = account.getAccountNumber();
		return ResponseEntity.ok(accountNumber);
	}

}
