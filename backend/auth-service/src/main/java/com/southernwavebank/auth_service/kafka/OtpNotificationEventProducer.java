package com.southernwavebank.auth_service.kafka;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.southernwavebank.auth_service.model.externaldto.NotificationDto;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class OtpNotificationEventProducer {

    private final KafkaTemplate<String, NotificationDto> kafkaTemplate;

    public OtpNotificationEventProducer(KafkaTemplate<String, NotificationDto> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendOtpNotification(NotificationDto notificationDto) {
        log.info("Producing 'otp-notification' event");

        try {
            kafkaTemplate.send("otp-notification", notificationDto);
            log.info("'otp-notification' event successfully sent to Kafka topic");
        } catch (Exception e) {
            log.error("Failed to send 'otp-notification' event to Kafka", e);
        }
    }
}

