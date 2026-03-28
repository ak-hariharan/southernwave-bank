package com.southernwavebank.user_service.kafka;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.southernwavebank.user_service.model.externaldto.NotificationDto;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class OfficerEventProducer {
	
    private final KafkaTemplate<String, NotificationDto> kafkaTemplate;

    public OfficerEventProducer(KafkaTemplate<String, NotificationDto> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }
    
    public void sendOfficerCreatedEvent(NotificationDto notificationDto) {
    	log.info("Producing 'officer-created' event");
    	try {
    		kafkaTemplate.send("officer-created", notificationDto);
    		log.info("'officer-created' event successfully sent to Kafka topic");
		} catch (Exception e) {
			log.error("Failed to send 'officer-created' event to Kafka", e);
		}
    }
}
