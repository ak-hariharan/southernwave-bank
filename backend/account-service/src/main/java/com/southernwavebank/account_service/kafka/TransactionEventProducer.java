package com.southernwavebank.account_service.kafka;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import com.southernwavebank.account_service.model.externaldto.TransactionDto;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class TransactionEventProducer {

	private final KafkaTemplate<String, TransactionDto> kafkaTemplate;

	public TransactionEventProducer(KafkaTemplate<String, TransactionDto> kafkaTemplate) {
		this.kafkaTemplate = kafkaTemplate;
	}
	
	public void sendTransactionEvent(TransactionDto transactionDto) {
		log.info("Producing 'transaction-happened' event");
		try {
			kafkaTemplate.send("transaction-happened", transactionDto);
			log.info("'transaction-happened' event successfully sent to Kafka topic");
		} catch (Exception e) {
			log.error("Failed to send 'transaction-happened' event to Kafka", e);
		}
	}
}
