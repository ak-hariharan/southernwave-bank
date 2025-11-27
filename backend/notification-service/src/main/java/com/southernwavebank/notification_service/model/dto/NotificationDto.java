package com.southernwavebank.notification_service.model.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.southernwavebank.notification_service.model.AccountStatus;
import com.southernwavebank.notification_service.model.AccountType;
import com.southernwavebank.notification_service.model.TransactionType;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class NotificationDto {
	
	private String accountNumber;
	  
	private AccountType accountType;
	
	private AccountStatus accountStatus;
	
	private BigDecimal availableBalance;
	
	private Long userId;
	
	private String username;
	
	private String emailId;
	
	private String contactNumber;

	private TransactionType transactionType;

	private BigDecimal amount;

	private LocalDateTime transactionTime;
	
	private int otp;
	
	private LocalDateTime time;
	
}
