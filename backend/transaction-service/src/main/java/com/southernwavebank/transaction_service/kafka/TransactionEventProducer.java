package com.southernwavebank.transaction_service.kafka;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.southernwavebank.transaction_service.model.externaldto.NotificationDto;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class TransactionEventProducer {
	private final KafkaTemplate<String, NotificationDto> kafkaTemplate;

	public TransactionEventProducer(KafkaTemplate<String, NotificationDto> kafkaTemplate) {
	        this.kafkaTemplate = kafkaTemplate;
	    }

	public void sendTransactionHappenedEvent(NotificationDto notificationDto) {
		log.info("Producing 'transaction-notification' event");
		try {
			kafkaTemplate.send("transaction-notification", notificationDto);
			log.info("'transaction-notification' event successfully sent to Kafka topic");
		} catch (Exception e) {
			log.error("Failed to send 'transaction-notification' event to Kafka", e);
		}
	}
}
