package com.southernwavebank.user_service.kafka;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.southernwavebank.user_service.model.externaldto.NotificationDto;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class UserEventProducer {
	
    private final KafkaTemplate<String, NotificationDto> kafkaTemplate;

    public UserEventProducer(KafkaTemplate<String, NotificationDto> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }
    
    public void sendUserCreatedEvent(NotificationDto notificationDto) {
    	log.info("Producing 'user-created' event");
    	try {
    		kafkaTemplate.send("user-created", notificationDto);
    		log.info("'user-created' event successfully sent to Kafka topic");
		} catch (Exception e) {
			log.error("Failed to send 'account-created' event to Kafka", e);
		}
    }
}