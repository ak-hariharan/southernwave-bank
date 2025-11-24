package com.southernwavebank.account_service.kafka;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.southernwavebank.account_service.model.externaldto.NotificationDto;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class AccountStatusProducer {
	
	private final KafkaTemplate<String, NotificationDto> kafkaTemplate;

	public AccountStatusProducer(KafkaTemplate<String, NotificationDto> kafkaTemplate) {
	        this.kafkaTemplate = kafkaTemplate;
	    }

	public void sendAccountStatusEvent(NotificationDto notificationDto) {
		log.info("Producing 'account-status' event");

		try {
			kafkaTemplate.send("account-status", notificationDto);
			log.info("'account-status' event successfully sent to Kafka topic");
		} catch (Exception e) {
			log.error("Failed to send 'account-status' event to Kafka", e);
		}
	}
}
