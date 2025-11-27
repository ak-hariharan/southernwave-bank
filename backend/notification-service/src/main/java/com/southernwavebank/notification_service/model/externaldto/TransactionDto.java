package com.southernwavebank.notification_service.model.externaldto;

import java.math.BigDecimal;


import java.time.LocalDateTime;

import com.southernwavebank.notification_service.model.TransactionStatus;
import com.southernwavebank.notification_service.model.TransactionType;

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
