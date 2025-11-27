package com.southernwavebank.notification_service.kafka;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.southernwavebank.notification_service.model.dto.NotificationDto;
import com.southernwavebank.notification_service.model.externaldto.TransactionDto;
import com.southernwavebank.notification_service.service.NotificationService;

import lombok.extern.slf4j.Slf4j;

//@Slf4j
//@Service
//public class NotificationConsumer {
//	
//	private ObjectMapper objectMapper;
//	private NotificationService notificationService;

//    public NotificationConsumer() {
//        this.objectMapper = new ObjectMapper();
//        this.objectMapper.registerModule(new JavaTimeModule()); // Ensure LocalDateTime works
//    }
	
//	@Autowired
//	public NotificationConsumer(ObjectMapper objectMapper, NotificationService notificationService) {
//		super();
//		this.objectMapper = objectMapper;
//		this.notificationService = notificationService;
//	}
//
//	
//	// User created events
//	@KafkaListener(topics = "user-created", groupId = "notification-group")
//	public void consumerUserCreation(@Payload NotificationDto notificationDto) {
//		log.info("Received user creation event");
//		notificationService.sendEmail(notificationDto, "USER_CREATED");
//	}
//	
//	 // Account Creation Events
//    @KafkaListener(topics = "account-created", groupId = "notification-group")
//    public void consumeAccountCreation(@Payload NotificationDto notificationDto) {
//    	log.info("Received account creation event");
//    	notificationService.sendEmail(notificationDto, "ACCOUNT_CREATED");
//    }
//    
//    // Account status Events
//    @KafkaListener(topics = "account-status", groupId = "notification-group")
//    public void consumeAccountStatus(@Payload NotificationDto notificationDto) {
//    	log.info("Received account status update event");
//    	notificationService.sendEmail(notificationDto, "ACCOUNT_STATUS");
//    }
//    
//	  //Transaction Events
//    @KafkaListener(topics = "transaction-notification", groupId = "notification-group")
//    public void consumeTransaction(@Payload NotificationDto notificationDto) {
//    	log.info("Received transaction notification event");
//        notificationService.sendEmail(notificationDto, "TRANSACTION");
//    }
//    
//    // Balance Enquiry Events
//    @KafkaListener(topics = "balance-enquiry", groupId = "notification-group")
//    public void consumeBalanceEnquiry(@Payload NotificationDto notificationDto) {
////    	System.out.println(notificationDto);
//    	log.info("Received balance enquiry event");
//    	notificationService.sendEmail(notificationDto, "BALANCE");
//    }
//
//    // Transaction History Events
//    @KafkaListener(topics = "transhistory-notification", groupId = "notification-group")
//    public void consumeTransactionHistory(@Payload List<LinkedHashMap<String, Object>> transactionsMap) {
//    	log.info("Received transaction history event");
//        List<TransactionDto> transactions = transactionsMap.stream()
//            .map(map -> objectMapper.convertValue(map, TransactionDto.class))
//            .collect(Collectors.toList());
//        Collections.reverse(transactions);
//        notificationService.sendEmail(transactions, LocalDateTime.now());
//    }
//    
//    // No Transaction History Events
//    @KafkaListener(topics = "no-transaction-notification", groupId = "notitification-group")
//    public void consumeNoTransactionHistory(@Payload NotificationDto notificationDto) {
//    	log.info("Received no transaction history event");
//    	notificationService.sendEmail(notificationDto, "NO_TRANSACTION");
//    }
//    
//    // 	OTP Notification Events
//    @KafkaListener(topics = "otp-notification", groupId = "notification-group")
//    public void consumeOtpNotification(@Payload NotificationDto notificationDto) {
//        log.info("Received OTP notification event");
//        notificationService.sendEmail(notificationDto, "OTP");
//    }
//    
//    // 	Password Updation Notification Events
//    @KafkaListener(topics = "password-update", groupId = "notification-group")
//    public void consumePasswordUpdateNotification(@Payload NotificationDto notificationDto) {
//        log.info("Received OTP notification event");
//        notificationService.sendEmail(notificationDto, "PASSWORD");
//    }
//
//}

@Slf4j
@Service
public class NotificationConsumer {

    private final ObjectMapper objectMapper;
    private final NotificationService notificationService;

    @Autowired
    public NotificationConsumer(ObjectMapper objectMapper, NotificationService notificationService) {
        this.objectMapper = objectMapper;
        this.notificationService = notificationService;
    }

    // User created events
    @KafkaListener(
            topics = "user-created",
            groupId = "notification-group",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void consumerUserCreation(@Payload NotificationDto notificationDto) {
        processNotification(notificationDto, "USER_CREATED");
    }

    // Account Creation Events
    @KafkaListener(
            topics = "account-created",
            groupId = "notification-group",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void consumeAccountCreation(@Payload NotificationDto notificationDto) {
        processNotification(notificationDto, "ACCOUNT_CREATED");
    }

    // Account Status Events
    @KafkaListener(
            topics = "account-status",
            groupId = "notification-group",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void consumeAccountStatus(@Payload NotificationDto notificationDto) {
        processNotification(notificationDto, "ACCOUNT_STATUS");
    }

    // Transaction Eventsm
    @KafkaListener(
            topics = "transaction-notification",
            groupId = "notification-group",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void consumeTransaction(@Payload NotificationDto notificationDto) {
        processNotification(notificationDto, "TRANSACTION");
    }

    // Balance Enquiry Events
    @KafkaListener(
            topics = "balance-enquiry",
            groupId = "notification-group",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void consumeBalanceEnquiry(@Payload NotificationDto notificationDto) {
        processNotification(notificationDto, "BALANCE");
    }

    // No Transaction History Events
    @KafkaListener(
            topics = "no-transaction-notification",
            groupId = "notification-group",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void consumeNoTransactionHistory(@Payload NotificationDto notificationDto) {
        processNotification(notificationDto, "NO_TRANSACTION");
    }

    // OTP Notification Events
    @KafkaListener(
            topics = "otp-notification",
            groupId = "notification-group",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void consumeOtpNotification(@Payload NotificationDto notificationDto) {
        processNotification(notificationDto, "OTP");
    }

    // Password Update Events
    @KafkaListener(
            topics = "password-update",
            groupId = "notification-group",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void consumePasswordUpdateNotification(@Payload NotificationDto notificationDto) {
        processNotification(notificationDto, "PASSWORD");
    }

    // Transaction History Events (List payload)
    @KafkaListener(
            topics = "transhistory-notification",
            groupId = "notification-group",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void consumeTransactionHistory(@Payload List<LinkedHashMap<String, Object>> transactionsMap) {
        log.info("Received transaction history event");
        try {
            List<TransactionDto> transactions = transactionsMap.stream()
                    .map(map -> objectMapper.convertValue(map, TransactionDto.class))
                    .collect(Collectors.toList());
            Collections.reverse(transactions);

            notificationService.sendEmail(transactions, LocalDateTime.now());
        } catch (Exception e) {
            log.error("Failed to process transaction history notification: {}", e.getMessage(), e);
            throw e; // rethrow for Kafka retry
        }
    }

    // Helper method to process single NotificationDto events
    private void processNotification(NotificationDto notificationDto, String eventType) {
        log.info("Received {} event", eventType);
        try {
            notificationService.sendEmail(notificationDto, eventType);
        } catch (Exception e) {
            log.error("Failed to send {} notification: {}", eventType, e.getMessage(), e);
            throw e; // rethrow for Kafka retry
        }
    }

}

