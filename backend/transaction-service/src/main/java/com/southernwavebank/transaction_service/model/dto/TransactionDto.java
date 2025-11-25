package com.southernwavebank.transaction_service.model.dto;

import java.math.BigDecimal;

import java.time.LocalDateTime;

import com.southernwavebank.transaction_service.model.TransactionStatus;
import com.southernwavebank.transaction_service.model.TransactionType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TransactionDto {
	
    private String accountNumber;

    private TransactionType transactionType;  

    private BigDecimal amount;

    private TransactionStatus status;

    private LocalDateTime transactionTime;
    
    private Long userId;
    
    private String transactionReference;
}
