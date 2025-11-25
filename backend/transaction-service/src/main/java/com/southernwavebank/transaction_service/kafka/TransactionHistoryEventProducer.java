package com.southernwavebank.transaction_service.kafka;

import java.util.List;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.southernwavebank.transaction_service.model.dto.TransactionDto;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class TransactionHistoryEventProducer {
	
	private final KafkaTemplate<String, List<TransactionDto>> kafkaTemplate;

    public TransactionHistoryEventProducer(KafkaTemplate<String, List<TransactionDto>> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendTransactionHistoryEvent(List<TransactionDto> listOfTransactionHistory) {
    	log.info("Producing 'transaction-history-notification' event");

    	try {
    		kafkaTemplate.send("transhistory-notification", listOfTransactionHistory);
    		log.info("'transhistory-notification' event successfully sent to Kafka topic");
		} catch (Exception e) {
			log.error("Failed to send 'transhistory-notification' event to Kafka", e);
		}
    }

}
