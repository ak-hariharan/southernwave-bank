package com.southernwavebank.user_service.kafka;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.southernwavebank.user_service.model.externaldto.NotificationDto;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class PasswordUpdationEventProducer {

	private final KafkaTemplate<String, NotificationDto> kafkaTemplate;

	public PasswordUpdationEventProducer(KafkaTemplate<String, NotificationDto> kafkaTemplate) {
	        this.kafkaTemplate = kafkaTemplate;
	    }

	public void sendPasswordUpdateEvent(NotificationDto notificationDto) {
		log.info("Producing 'password-update' event");
		
		try {
			kafkaTemplate.send("password-update", notificationDto);
			log.info("'password-update' event successfully sent to Kafka topic");
		} catch (Exception e) {
			log.error("Failed to send 'password-update' event to Kafka", e);
		}
	}
}
