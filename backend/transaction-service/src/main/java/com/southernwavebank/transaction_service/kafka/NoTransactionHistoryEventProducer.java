package com.southernwavebank.transaction_service.kafka;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.southernwavebank.transaction_service.model.externaldto.NotificationDto;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class NoTransactionHistoryEventProducer {
	private final KafkaTemplate<String, NotificationDto> kafkaTemplate;

	public NoTransactionHistoryEventProducer(KafkaTemplate<String, NotificationDto> kafkaTemplate) {
	        this.kafkaTemplate = kafkaTemplate;
	    }

	public void sendNoTransactionHappenedEvent(NotificationDto notificationDto) {
		log.info("Producing 'no-transaction-notification' event");
		try {
			kafkaTemplate.send("no-transaction-notification", notificationDto);
			log.info("'no-transaction-notification' event successfully sent to Kafka topic");
		} catch (Exception e) {
			log.error("Failed to send 'no-transaction-notification' event to Kafka", e);
		}
	}
}
