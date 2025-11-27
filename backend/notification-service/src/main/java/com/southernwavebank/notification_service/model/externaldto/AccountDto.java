package com.southernwavebank.notification_service.model.externaldto;

import java.math.BigDecimal;

import com.southernwavebank.notification_service.model.AccountStatus;
import com.southernwavebank.notification_service.model.AccountType;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class AccountDto {
	
	private String accountNumber;
	  
	private AccountType accountType;
	
	private AccountStatus accountStatus;
	
	private BigDecimal availableBalance;

	private Long userId;

}
