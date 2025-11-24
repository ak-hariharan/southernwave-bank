package com.southernwavebank.account_service.model.externaldto;

import java.math.BigDecimal;

import java.time.LocalDateTime;
import com.southernwavebank.account_service.model.TransactionStatus;
import com.southernwavebank.account_service.model.TransactionType;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TransactionDto {
	
	@NotNull(message = "Account Number required")
	private String accountNumber;
		
	@NotNull(message = "Amount should be proper value")
	private BigDecimal amount;
	
	private TransactionType transactionType;
	
	private TransactionStatus status; 
	
	private LocalDateTime transactionTime;
	
	private Long userId;
	
	private String transactionReference;
}
