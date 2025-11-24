package com.southernwavebank.account_service.kafka;


import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;

import com.southernwavebank.account_service.kafka.TransactionEventProducer;
import com.southernwavebank.account_service.model.externaldto.TransactionDto;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TransactionEventProducerTest {

    @Mock
    private KafkaTemplate<String, TransactionDto> kafkaTemplate;

    @InjectMocks
    private TransactionEventProducer transactionEventProducer;

    @Test
    void testSendTransactionEvent_success() {
        TransactionDto transactionDto = new TransactionDto();
        transactionDto.setAccountNumber("ACC123");
        transactionDto.setAmount(new java.math.BigDecimal("100.00"));

        // Mock KafkaTemplate send
        when(kafkaTemplate.send(eq("transaction-happened"), any(TransactionDto.class)))
                .thenReturn(null); // Returning null as no chaining/future handling

        transactionEventProducer.sendTransactionEvent(transactionDto);

        verify(kafkaTemplate, times(1)).send("transaction-happened", transactionDto);
    }

    @Test
    void testSendTransactionEvent_failure_logsError() {
        TransactionDto transactionDto = new TransactionDto();
        transactionDto.setAccountNumber("ACC999");
        transactionDto.setAmount(new java.math.BigDecimal("500.00"));

        // Simulate failure during Kafka send
        doThrow(new RuntimeException("Kafka unavailable"))
                .when(kafkaTemplate).send(anyString(), any(TransactionDto.class));

        transactionEventProducer.sendTransactionEvent(transactionDto);

        verify(kafkaTemplate, times(1)).send(anyString(), any(TransactionDto.class));
    }
}

