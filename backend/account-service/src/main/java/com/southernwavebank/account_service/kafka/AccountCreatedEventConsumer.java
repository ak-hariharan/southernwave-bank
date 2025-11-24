package com.southernwavebank.account_service.kafka;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

import com.southernwavebank.account_service.model.externaldto.UserDto;
import com.southernwavebank.account_service.service.AccountService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class AccountCreatedEventConsumer {

	@Autowired
	private AccountService accountService;

	@KafkaListener(topics = "account-create-initate", groupId = "account-service-group")
	public void handleUserCreated(@Payload UserDto userDto) {
		LocalDateTime time = LocalDateTime.now();
		
	    log.info("Received user-created event: ", userDto);
	    accountService.createAccount(userDto, time);
	}
	
}
