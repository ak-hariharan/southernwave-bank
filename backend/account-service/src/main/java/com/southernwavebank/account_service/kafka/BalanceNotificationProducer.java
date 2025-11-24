package com.southernwavebank.account_service.kafka;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.southernwavebank.account_service.model.externaldto.NotificationDto;

import lombok.extern.slf4j.Slf4j;


@Service
@Slf4j
public class BalanceNotificationProducer {
	private final KafkaTemplate<String, NotificationDto> kafkaTemplate;

	public BalanceNotificationProducer(KafkaTemplate<String, NotificationDto> kafkaTemplate) {
	        this.kafkaTemplate = kafkaTemplate;
	    }

	public void sendBalanceEnquiryEvent(NotificationDto notificationDto) {
		log.info("Producing 'balance-enquiry' event");

		try {
			kafkaTemplate.send("balance-enquiry", notificationDto);
			log.info("'balance-enquiry' event successfully sent to Kafka topic");
		} catch (Exception e) {
			log.error("Failed to send 'balance-enquiry' event to Kafka", e);
		}
	}
}
