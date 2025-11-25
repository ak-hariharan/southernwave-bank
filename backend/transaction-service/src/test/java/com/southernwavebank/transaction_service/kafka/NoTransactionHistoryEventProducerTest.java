package com.southernwavebank.transaction_service.kafka;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.kafka.core.KafkaTemplate;

import com.southernwavebank.transaction_service.kafka.NoTransactionHistoryEventProducer;
import com.southernwavebank.transaction_service.model.TransactionStatus;
import com.southernwavebank.transaction_service.model.TransactionType;
import com.southernwavebank.transaction_service.model.externaldto.NotificationDto;

public class NoTransactionHistoryEventProducerTest {

    private KafkaTemplate<String, NotificationDto> kafkaTemplate;
    private NoTransactionHistoryEventProducer eventProducer;

    @BeforeEach
    void setUp() {
        kafkaTemplate = Mockito.mock(KafkaTemplate.class);
        eventProducer = new NoTransactionHistoryEventProducer(kafkaTemplate);
    }

    @Test
    void sendNoTransactionHappenedEvent_shouldSendMessageToKafka() {
        // Arrange
        NotificationDto notificationDto = new NotificationDto();
        notificationDto.setAccountNumber("ACE123455");

        // Act
        eventProducer.sendNoTransactionHappenedEvent(notificationDto);

        // Assert
        verify(kafkaTemplate, times(1)).send("no-transaction-notification", notificationDto);
    }

    @Test
    void sendNoTransactionHappenedEvent_shouldHandleKafkaExceptionGracefully() {
        // Arrange
        NotificationDto notificationDto = new NotificationDto();
        doThrow(new RuntimeException("Kafka unavailable"))
                .when(kafkaTemplate)
                .send("no-transaction-notification", notificationDto);

        // Act
        eventProducer.sendNoTransactionHappenedEvent(notificationDto);

        // Assert
        verify(kafkaTemplate, times(1)).send("no-transaction-notification", notificationDto);
        // Optionally, verify logging if using a log capturing library
    }
}
