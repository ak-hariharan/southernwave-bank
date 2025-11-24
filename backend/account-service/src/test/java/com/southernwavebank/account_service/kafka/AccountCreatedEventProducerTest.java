package com.southernwavebank.account_service.kafka;

import static org.mockito.ArgumentMatchers.any;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;

import com.southernwavebank.account_service.kafka.AccountCreatedEventProducer;
import com.southernwavebank.account_service.model.externaldto.NotificationDto;

@ExtendWith(MockitoExtension.class)
public class AccountCreatedEventProducerTest {

    @Mock
    private KafkaTemplate<String, NotificationDto> kafkaTemplate;

    @InjectMocks
    private AccountCreatedEventProducer producer;

    @Test
    void testSendAccountCreatedEvent_success() {
        NotificationDto notificationDto = new NotificationDto();
        // set any fields if needed

        // Mock the send method to return a successful future (or just doNothing for simplicity)
        when(kafkaTemplate.send(eq("account-created"), any(NotificationDto.class)))
                .thenReturn(null); // or use mock of ListenableFuture if chaining needed

        producer.sendAccountCreatedEvent(notificationDto);

        verify(kafkaTemplate, times(1)).send("account-created", notificationDto);
    }

    @Test
    void testSendAccountCreatedEvent_failure_logsError() {
        NotificationDto notificationDto = new NotificationDto();

        // Simulate exception during Kafka send
        doThrow(new RuntimeException("Kafka unavailable"))
            .when(kafkaTemplate).send(anyString(), any(NotificationDto.class));

        // No exception should propagate, just log
        producer.sendAccountCreatedEvent(notificationDto);

        verify(kafkaTemplate, times(1)).send(anyString(), any(NotificationDto.class));
    }
}
