package com.southernwavebank.account_service.model.entity;

import java.math.BigDecimal;

import com.southernwavebank.account_service.model.AccountStatus;
import com.southernwavebank.account_service.model.AccountType;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Account {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long accountId;
	
	private String accountNumber;
	  
	@Enumerated(EnumType.STRING) 
	private AccountType accountType;
	
	@Enumerated(EnumType.STRING) 
	private AccountStatus accountStatus;
	
	private BigDecimal availableBalance;

	private Long userId;
}
