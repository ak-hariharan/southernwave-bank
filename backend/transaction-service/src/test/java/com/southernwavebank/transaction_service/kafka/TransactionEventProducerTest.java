package com.southernwavebank.transaction_service.kafka;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.kafka.core.KafkaTemplate;

import com.southernwavebank.transaction_service.kafka.TransactionEventProducer;
import com.southernwavebank.transaction_service.model.TransactionStatus;
import com.southernwavebank.transaction_service.model.TransactionType;
import com.southernwavebank.transaction_service.model.externaldto.NotificationDto;

public class TransactionEventProducerTest {

    private KafkaTemplate<String, NotificationDto> kafkaTemplate;
    private TransactionEventProducer eventProducer;

    @BeforeEach
    void setUp() {
        kafkaTemplate = Mockito.mock(KafkaTemplate.class);
        eventProducer = new TransactionEventProducer(kafkaTemplate);
    }

    @Test
    void sendTransactionHappenedEvent_shouldSendMessageToKafka() {
        // Arrange
        NotificationDto notificationDto = new NotificationDto();
        notificationDto.setAccountNumber("ACE123455");
        notificationDto.setAmount(BigDecimal.valueOf(500));
        notificationDto.setStatus(TransactionStatus.SUCCESS);
        notificationDto.setTransactionTime(LocalDateTime.now());
        notificationDto.setTransactionType(TransactionType.DEPOSIT);

        // Act
        eventProducer.sendTransactionHappenedEvent(notificationDto);

        // Assert
        verify(kafkaTemplate, times(1)).send("transaction-notification", notificationDto);
    }

    @Test
    void sendTransactionHappenedEvent_shouldHandleKafkaExceptionGracefully() {
        // Arrange
        NotificationDto notificationDto = new NotificationDto();
        doThrow(new RuntimeException("Kafka down"))
                .when(kafkaTemplate)
                .send("transaction-notification", notificationDto);

        // Act
        eventProducer.sendTransactionHappenedEvent(notificationDto);

        // Assert
        verify(kafkaTemplate, times(1)).send("transaction-notification", notificationDto);
        // Optional: Add log verification if using a logging test library
    }
}

