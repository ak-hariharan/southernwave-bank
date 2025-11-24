package com.southernwavebank.account_service.model.dto;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;
import com.southernwavebank.account_service.model.AccountStatus;
import com.southernwavebank.account_service.model.AccountType;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class AccountDto {
	
	@JsonProperty(access =  Access.WRITE_ONLY)
	private Long userId;
	
	private String accountNumber;
	  
	private AccountType accountType;
	
	private AccountStatus accountStatus;
	
	private BigDecimal availableBalance;


}
