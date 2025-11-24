package com.southernwavebank.account_service.kafka;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.southernwavebank.account_service.model.externaldto.NotificationDto;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class AccountCreatedEventProducer {
	
	private final KafkaTemplate<String, NotificationDto> kafkaTemplate;

	public AccountCreatedEventProducer(KafkaTemplate<String, NotificationDto> kafkaTemplate) {
	        this.kafkaTemplate = kafkaTemplate;
	    }

	public void sendAccountCreatedEvent(NotificationDto notificationDto) {
		log.info("Producing 'account-created' event");

		try {
			kafkaTemplate.send("account-created", notificationDto);
			log.info("'account-created' event successfully sent to Kafka topic");
		} catch (Exception e) {
			log.error("Failed to send 'account-created' event to Kafka", e);
		}
	}
}
