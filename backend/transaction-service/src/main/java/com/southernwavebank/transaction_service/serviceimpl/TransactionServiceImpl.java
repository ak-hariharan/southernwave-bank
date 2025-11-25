package com.southernwavebank.transaction_service.serviceimpl;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClient.Builder;

import com.southernwavebank.transaction_service.exception.*;
import com.southernwavebank.transaction_service.kafka.NoTransactionHistoryEventProducer;
import com.southernwavebank.transaction_service.kafka.TransactionEventProducer;
import com.southernwavebank.transaction_service.kafka.TransactionHistoryEventProducer;
import com.southernwavebank.transaction_service.model.TransactionStatus;
import com.southernwavebank.transaction_service.model.dto.TransactionDto;
import com.southernwavebank.transaction_service.model.entity.CustomPrincipal;
import com.southernwavebank.transaction_service.model.entity.Transaction;
import com.southernwavebank.transaction_service.model.externaldto.NotificationDto;
import com.southernwavebank.transaction_service.model.response.Response;
import com.southernwavebank.transaction_service.repository.TransactionRepository;
import com.southernwavebank.transaction_service.service.TransactionService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Service implementation responsible for persisting transactions and publishing related events.
 * Handles deposit/withdraw workflows and retrieval of transaction history for accounts.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class TransactionServiceImpl implements TransactionService {
	
	private TransactionRepository transactionRepository;
	private ModelMapper modelMapper;
	private TransactionEventProducer transactionEventProducer;
	private TransactionHistoryEventProducer transHistoryEventProducer;
	private NoTransactionHistoryEventProducer noTransactionHistoryEventProducer;
	private WebClient.Builder webClientBuilder;
	
	@Autowired
	public TransactionServiceImpl(TransactionRepository transactionRepository, ModelMapper modelMapper,
			TransactionEventProducer transactionEventProducer,
			TransactionHistoryEventProducer transHistoryEventProducer,
			NoTransactionHistoryEventProducer noTransactionHistoryEventProducer, Builder webClientBuilder) {
		this.transactionRepository = transactionRepository;
		this.modelMapper = modelMapper;
		this.transactionEventProducer = transactionEventProducer;
		this.transHistoryEventProducer = transHistoryEventProducer;
		this.noTransactionHistoryEventProducer = noTransactionHistoryEventProducer;
		this.webClientBuilder = webClientBuilder;
	}

	/**
	 * Persist a deposit transaction and publish notification events when applicable.
	 * @returns void (transaction saved or ignored if duplicate).
	 * @throws none (logs and returns early on idempotent duplicates; runtime exceptions may propagate).
	 */
	@Override
	@Transactional
	public void depositMoney(TransactionDto transactionDto) {
	    log.info("Starting depositMoney method execution.");
	    // Idempotency check
	    if (transactionRepository.existsByTransactionReference(transactionDto.getTransactionReference())) {
	    	 log.warn("Transaction {} already exists. Skipping.", transactionDto.getTransactionReference());
	         log.info("Response: Transaction already processed"); // just log
	         return; 
	    }

	    Transaction transaction = new Transaction();
	    modelMapper.map(transactionDto, transaction);

	    transactionRepository.save(transaction);
	    log.info("Transaction saved successfully.");

	    if (TransactionStatus.SUCCESS.equals(transactionDto.getStatus())) {
	    	log.info("Transaction status is SUCCESS. Preparing to send notification event.");
	        NotificationDto notificationDto = new NotificationDto();
	        notificationDto.setUserId(transactionDto.getUserId());
	        modelMapper.map(transaction, notificationDto);
	        transactionEventProducer.sendTransactionHappenedEvent(notificationDto);
	        log.info("Notification event sent successfully for transaction.");
	    }

	    String message = transactionDto.getStatus() == TransactionStatus.SUCCESS ? 
	                     "Transaction completed successfully." : 
	                     "Transaction saved but not successful.";

        Response response = Response.builder().data(transaction).message(message).build();
	    
	    log.info("Transaction processed", response);
	}


	/**
	 * Persist a withdrawal transaction and publish notification events when applicable.
	 * @returns void (transaction saved or ignored if duplicate).
	 * @throws none (logs and returns early on idempotent duplicates; runtime exceptions may propagate).
	 */
	@Override
	@Transactional
	public void withdrawMoney(TransactionDto transactionDto) {
		log.info("Starting withdrawMoney method execution.");
		// Idempotency check
		if (transactionRepository.existsByTransactionReference(transactionDto.getTransactionReference())) {
			log.warn("Transaction {} already exists. Skipping.", transactionDto.getTransactionReference());
			log.info("Response: Transaction already processed"); // just log
			return;
		}
		
		Transaction transaction = new Transaction();
		modelMapper.map(transactionDto, transaction);
		
		transactionRepository.save(transaction);
		log.info("Transaction saved successfully.");
		
		if (TransactionStatus.SUCCESS.equals(transactionDto.getStatus())) {
			log.info("Transaction status is SUCCESS. Preparing to send notification event.");
			NotificationDto notificationDto = new NotificationDto();
			notificationDto.setUserId(transactionDto.getUserId());
			modelMapper.map(transaction, notificationDto);
			transactionEventProducer.sendTransactionHappenedEvent(notificationDto);
			log.info("Notification event sent successfully for transaction.");
		}

		String message = transactionDto.getStatus() == TransactionStatus.SUCCESS ? "Transaction completed successfully."
				: "Transaction saved but not successful.";

		Response response = Response.builder().data(transaction).message(message).build();
		log.info("Transaction processed", response);

	}
	
	/**
	 * Fetch transaction history for an account, optionally within a date range.
	 * Chooses the appropriate overload depending on whether dates are provided.
	 * @returns ResponseEntity<Response> with transaction list or empty list if none found.
	 * @throws UnauthorizedAccessException when a consumer requests another account's history.
	 */
	@Override
	public ResponseEntity<Response> fetchTransactions(String accountNumber, LocalDateTime startDate, LocalDateTime endDate,
			CustomPrincipal principal, LocalDateTime time) {
		 if (startDate == null || endDate == null) {
		        return fetchTransactionsOfAccount(accountNumber, principal, time);
		    }
		 return fetchTransactionsOfAccount(accountNumber, startDate, endDate, principal, time);
	}


	/**
	 * Retrieve all transactions for an account and publish history/no-history events to notification service.
	 * @returns ResponseEntity<Response> with all transactions for the account.
	 * @throws UnauthorizedAccessException when a consumer is unauthorized to view the account.
	 */
	public ResponseEntity<Response> fetchTransactionsOfAccount(String accountNumber, CustomPrincipal principal, LocalDateTime time) {

		String actualAccountNumber = "";
		String userId = principal.getUserId();
		String role = principal.getAuthorities().iterator().next().getAuthority();

		log.info("Executing fetchTransactionsOfAccount");
		if ("CONSUMER".equals(role)) {
			log.info("Fetching actual account number matching with current consumer.");
			ResponseEntity<String> entity = webClientBuilder.build().get()
					.uri("http://account-service/swb/account/accountnumber/{userId}", userId).retrieve()
					.toEntity(String.class).block();

			log.info("Received account service response");
			if (entity == null || !entity.getStatusCode().is2xxSuccessful() || entity.getBody() == null) {
				log.warn("Account number fetching failed: Account service returned {}",
						(entity != null && entity.getBody() != null) ? entity.getStatusCode() : "null");

				String errorMessage = (entity != null && entity.getBody() != null) ? entity.getBody()
						: "Response is null";
				throw new RuntimeException(errorMessage);
			}

			actualAccountNumber = entity.getBody();

			if (!accountNumber.equals(actualAccountNumber)) {
				log.warn("Unauthorized access attempt detected for CONSUMER role.");
				throw new UnauthorizedAccessException(accountNumber);
			}
		}

		if (!transactionRepository.existsByAccountNumber(accountNumber)) {
			log.info("No transaction records found in DB for the provided account.");

			if ("CONSUMER".equals(role)) {
				NotificationDto notificationDto = new NotificationDto();
				notificationDto.setAccountNumber(actualAccountNumber);
				notificationDto.setUserId(Long.valueOf(userId));
				notificationDto.setTime(time);
				noTransactionHistoryEventProducer.sendNoTransactionHappenedEvent(notificationDto);
			}

			return ResponseEntity.status(HttpStatus.OK).body(Response.builder().data(Collections.emptyList())
					.message("No transactions found for this account").build());

		}

		log.info("Fetching transaction records from DB.");
		List<Transaction> listOfTransactions = transactionRepository.findByAccountNumber(accountNumber);
		
		List<TransactionDto> listOfTransactionHistory = listOfTransactions.stream().map(transaction -> {
			TransactionDto dto = modelMapper.map(transaction, TransactionDto.class);
			dto.setUserId(Long.valueOf(userId)); 
			return dto;
		}).collect(Collectors.toList());

		// Sending transaction history event to notification service
		if ("CONSUMER".equals(role)) {
			log.info("Sending transaction history event for CONSUMER.");
			transHistoryEventProducer.sendTransactionHistoryEvent(listOfTransactionHistory);
		}
		log.info("Returning successful response for transaction history fetch.");
		return ResponseEntity.status(HttpStatus.OK).body(Response.builder().data(listOfTransactionHistory)
				.message("Transaction history retrieved successfully").build());

	}

	/**
	 * Retrieve transactions for an account within a given date range and publish history/no-history events.
	 * @returns ResponseEntity<Response> with transactions in the specified range or an empty list.
	 * @throws UnauthorizedAccessException when a consumer is unauthorized to view the account.
	 */
	public ResponseEntity<Response> fetchTransactionsOfAccount(String accountNumber, LocalDateTime startDate,
			LocalDateTime endDate, CustomPrincipal principal, LocalDateTime time) {

		String actualAccountNumber = "";
		String userId = principal.getUserId();
		String role = principal.getAuthorities().iterator().next().getAuthority();

		log.info("Executing fetchTransactionsOfAccount in a given period");
		if ("CONSUMER".equals(role)) {
			log.info("Fetching actual account number matching with current consumer.");
			ResponseEntity<String> entity = webClientBuilder.build().get()
					.uri("http://account-service/swb/account/accountnumber/{userId}", userId).retrieve()
					.toEntity(String.class).block();

			log.info("Received account service response");
			if (entity == null || !entity.getStatusCode().is2xxSuccessful() || entity.getBody() == null) {
				log.warn("Account number fetching failed: Account service returned {}",
						(entity != null && entity.getBody() != null) ? entity.getStatusCode() : "null");

				String errorMessage = (entity != null && entity.getBody() != null) ? entity.getBody()
						: "Response is null";
				throw new RuntimeException(errorMessage);
			}

			actualAccountNumber = entity.getBody();

			if (!accountNumber.equals(actualAccountNumber)) {
				log.warn("Unauthorized access attempt detected for CONSUMER role.");
				throw new UnauthorizedAccessException(accountNumber);
			}
		}

		log.info("Checking if transactions exist for the provided account.");
		if (!transactionRepository.existsByAccountNumber(accountNumber)) {
			log.info("No transactions found in DB");

			if ("CONSUMER".equals(role)) {
				NotificationDto notificationDto = new NotificationDto();
				notificationDto.setAccountNumber(actualAccountNumber);
				notificationDto.setUserId(Long.valueOf(userId));
				notificationDto.setTime(time);
				noTransactionHistoryEventProducer.sendNoTransactionHappenedEvent(notificationDto);
			}

			return ResponseEntity.status(HttpStatus.OK).body(Response.builder().data(Collections.emptyList())
					.message("No transactions found for this account").build());

		}

		log.info("Fetching transactions from DB between specified date range.");
		List<Transaction> listOfTransactions = transactionRepository
				.findByAccountNumberAndTransactionTimeBetween(accountNumber, startDate, endDate);

		if (listOfTransactions.isEmpty()) {
			log.info("No transactions found in the specified date range.");
			if ("CONSUMER".equals(role)) {
				log.info("Sending no-transaction history event for CONSUMER.");
				NotificationDto notificationDto = new NotificationDto();
				notificationDto.setAccountNumber(accountNumber);
				notificationDto.setTime(time);
				notificationDto.setUserId(Long.valueOf(userId));
				noTransactionHistoryEventProducer.sendNoTransactionHappenedEvent(notificationDto);
			}
			return ResponseEntity.status(HttpStatus.OK).body(Response.builder().data(Collections.emptyList())
					.message("No transactions found for the given date range").build());
		}

		List<TransactionDto> listOfTransactionHistory = listOfTransactions.stream().map(transaction -> {
			TransactionDto dto = modelMapper.map(transaction, TransactionDto.class);
			dto.setUserId(Long.valueOf(userId));
			return dto;
		}).collect(Collectors.toList());
		// Sending transaction history event to notification service
		if ("CONSUMER".equals(role)) {
			log.info("Sending transaction history event for CONSUMER.");
			transHistoryEventProducer.sendTransactionHistoryEvent(listOfTransactionHistory);
		}

		log.info("Transaction history retrieval completed successfully.");
		return ResponseEntity.status(HttpStatus.OK).body(Response.builder().data(listOfTransactionHistory)
				.message("Transactions history retrieved successfully").build());

	}

}
