package com.southernwavebank.transaction_service.kafka;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

import com.southernwavebank.transaction_service.model.dto.TransactionDto;
import com.southernwavebank.transaction_service.service.TransactionService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class TransactionEventConsumer {

	private TransactionService transactionService;
	
	@Autowired
	public TransactionEventConsumer(TransactionService transactionService) {
		super();
		this.transactionService = transactionService;
	}
	
	@KafkaListener(
		    topics = "transaction-happened", 
		    groupId = "transaction-service-group", 
		    containerFactory = "kafkaListenerContainerFactory"
		)
		public void handleTransaction(@Payload TransactionDto transactionDto) {
	        log.info("Received transaction-happened event");

	        try {
	            switch (transactionDto.getTransactionType()) {
	                case DEPOSIT:
	                    log.debug("Initiating deposit operation.");
	                    transactionService.depositMoney(transactionDto);
	                    log.info("Deposit operation completed.");
	                    break;

	                case WITHDRAW:
	                    log.debug("Initiating withdraw operation.");
	                    transactionService.withdrawMoney(transactionDto);
	                    log.info("Withdraw operation completed.");
	                    break;

	                default:
	                    log.warn("Unknown transaction type");
	            }

	            // Manually commit the offset only if processing succeeds
	            // acknowledgment.acknowledge();

	        } catch (Exception e) {
	            log.error("❌ Error during transaction processing: {}", e.getMessage(), e);
	            // ❌ Do NOT acknowledge here - this allows retry
	        }
	    }


}
