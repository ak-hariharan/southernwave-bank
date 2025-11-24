package com.southernwavebank.account_service.model.externaldto;


import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.southernwavebank.account_service.model.AccountStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class NotificationDto {
	
	private String accountNumber;
	
	private AccountStatus accountStatus;
	
	private BigDecimal availableBalance;
	
	private String emailId;
	
	private String contactNumber;
	
	private Long userId;
	
	private LocalDateTime transactionTime;
	
	private LocalDateTime time;
}
