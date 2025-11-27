package com.southernwavebank.report_service.model.externalDto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.southernwavebank.report_service.model.TransactionStatus;
import com.southernwavebank.report_service.model.TransactionType;

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
}
