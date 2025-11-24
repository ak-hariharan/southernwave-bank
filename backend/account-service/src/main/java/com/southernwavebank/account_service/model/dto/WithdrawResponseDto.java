package com.southernwavebank.account_service.model.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class WithdrawResponseDto {

    private String accountNumber;
    private BigDecimal amount;
    private LocalDateTime transactionTime;
}
