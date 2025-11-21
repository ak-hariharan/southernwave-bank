package com.southernwavebank.user_service.kafka;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.southernwavebank.user_service.model.dto.UserDto;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class AccountCreatedEventProducer {

	private final KafkaTemplate<String, UserDto> kafkaTemplate;

	public AccountCreatedEventProducer(KafkaTemplate<String, UserDto> kafkaTemplate) {
	        this.kafkaTemplate = kafkaTemplate;
	    }

	public void sendAccountCreatedEvent(UserDto userDto) {
		log.info("Producing 'account-create-initate' event");
		
		try {
			kafkaTemplate.send("account-create-initate", userDto);
			log.info("'account-create-initate' event successfully sent to Kafka topic");
		} catch (Exception e) {
			log.error("Failed to send 'account-created' event to Kafka", e);
		}
	}
}
