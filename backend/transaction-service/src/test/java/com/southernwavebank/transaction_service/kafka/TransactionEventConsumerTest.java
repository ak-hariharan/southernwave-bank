package com.southernwavebank.transaction_service.kafka;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.southernwavebank.transaction_service.kafka.TransactionEventConsumer;
import com.southernwavebank.transaction_service.model.TransactionType;
import com.southernwavebank.transaction_service.model.dto.TransactionDto;
import com.southernwavebank.transaction_service.service.TransactionService;

@ExtendWith(MockitoExtension.class)
public class TransactionEventConsumerTest {

    @InjectMocks
    private TransactionEventConsumer transactionEventConsumer;

    @Mock
    private TransactionService transactionService;

    private TransactionDto getTransactionDto(TransactionType type) {
        TransactionDto dto = new TransactionDto();
        dto.setAccountNumber("ACC123");
        dto.setAmount(BigDecimal.valueOf(1000));
        dto.setTransactionType(type);
        return dto;
    }

    @Test
    void handleTransaction_shouldCallDeposit_whenTransactionTypeIsDeposit() {
        // Given
        TransactionDto dto = getTransactionDto(TransactionType.DEPOSIT);

        // When
        transactionEventConsumer.handleTransaction(dto);

        // Then
        verify(transactionService, times(1)).depositMoney(dto);
        verify(transactionService, never()).withdrawMoney(any());
    }

    @Test
    void handleTransaction_shouldCallWithdraw_whenTransactionTypeIsWithdraw() {
        // Given
        TransactionDto dto = getTransactionDto(TransactionType.WITHDRAW);

        // When
        transactionEventConsumer.handleTransaction(dto);

        // Then
        verify(transactionService, times(1)).withdrawMoney(dto);
        verify(transactionService, never()).depositMoney(any());
    }

    @Test
    void handleTransaction_shouldLogWarning_whenTransactionTypeIsUnknown() {
        // Given
        TransactionDto dto = getTransactionDto(null); // Simulate unknown type

        // When
        transactionEventConsumer.handleTransaction(dto);

        // Then
        verify(transactionService, never()).depositMoney(any());
        verify(transactionService, never()).withdrawMoney(any());
    }

    @Test
    void handleTransaction_shouldCatchException_whenServiceFails() {
        // Given
        TransactionDto dto = getTransactionDto(TransactionType.DEPOSIT);
        doThrow(new RuntimeException("Simulated error")).when(transactionService).depositMoney(dto);

        // When
        transactionEventConsumer.handleTransaction(dto);

        // Then
        verify(transactionService, times(1)).depositMoney(dto);
        // exception should be caught and NOT rethrown
    }
}
