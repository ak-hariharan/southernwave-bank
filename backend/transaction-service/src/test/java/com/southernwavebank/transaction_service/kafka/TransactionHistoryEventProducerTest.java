package com.southernwavebank.transaction_service.kafka;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.kafka.core.KafkaTemplate;

import com.southernwavebank.transaction_service.kafka.TransactionHistoryEventProducer;
import com.southernwavebank.transaction_service.model.TransactionStatus;
import com.southernwavebank.transaction_service.model.TransactionType;
import com.southernwavebank.transaction_service.model.dto.TransactionDto;

public class TransactionHistoryEventProducerTest {

    private KafkaTemplate<String, List<TransactionDto>> kafkaTemplate;
    private TransactionHistoryEventProducer eventProducer;

    @BeforeEach
    void setUp() {
        kafkaTemplate = Mockito.mock(KafkaTemplate.class);
        eventProducer = new TransactionHistoryEventProducer(kafkaTemplate);
    }

    @Test
    void sendTransactionHistoryEvent_shouldSendMessageToKafka() {
        // Arrange
        TransactionDto firstTransaction = new TransactionDto();
        firstTransaction.setAccountNumber("ACE42112");
        firstTransaction.setAmount(BigDecimal.valueOf(500));
        firstTransaction.setTransactionType(TransactionType.DEPOSIT);
        firstTransaction.setStatus(TransactionStatus.SUCCESS);
        firstTransaction.setTransactionTime(LocalDateTime.parse("2023-03-25T10:15:30"));

        TransactionDto secondTransaction = new TransactionDto();
        firstTransaction.setAccountNumber("ACE42112");
        firstTransaction.setAmount(BigDecimal.valueOf(1500));
        firstTransaction.setTransactionType(TransactionType.WITHDRAW);
        firstTransaction.setStatus(TransactionStatus.SUCCESS);
        firstTransaction.setTransactionTime(LocalDateTime.parse("2023-02-10T02:50:30"));

        List<TransactionDto> transactionHistory = Arrays.asList(firstTransaction, secondTransaction);

        // Act
        eventProducer.sendTransactionHistoryEvent(transactionHistory);

        // Assert
        verify(kafkaTemplate, times(1)).send("transhistory-notification", transactionHistory);
    }

    @Test
    void sendTransactionHistoryEvent_shouldHandleKafkaExceptionGracefully() {
        // Arrange
        TransactionDto firstTransaction = new TransactionDto();
        firstTransaction.setAccountNumber("ACE42112");

        List<TransactionDto> transactionHistory = List.of(firstTransaction);

        doThrow(new RuntimeException("Kafka unavailable"))
                .when(kafkaTemplate)
                .send("transhistory-notification", transactionHistory);

        // Act
        eventProducer.sendTransactionHistoryEvent(transactionHistory);

        // Assert
        verify(kafkaTemplate, times(1)).send("transhistory-notification", transactionHistory);
    }
}

