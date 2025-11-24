package com.southernwavebank.account_service.events;

import com.southernwavebank.account_service.model.externaldto.TransactionDto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TransactionEvent {
    private final TransactionDto transactionDto;
}
