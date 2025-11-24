package com.southernwavebank.account_service.kafka;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;

import com.southernwavebank.account_service.kafka.BalanceNotificationProducer;
import com.southernwavebank.account_service.model.externaldto.NotificationDto;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class BalanceNotificationProducerTest {

    @Mock
    private KafkaTemplate<String, NotificationDto> kafkaTemplate;

    @InjectMocks
    private BalanceNotificationProducer balanceNotificationProducer;

    @Test
    void testSendBalanceEnquiryEvent_success() {
        NotificationDto notificationDto = new NotificationDto();

        // Mock KafkaTemplate send to avoid real call
        when(kafkaTemplate.send(eq("balance-enquiry"), any(NotificationDto.class)))
                .thenReturn(null); // or a mock ListenableFuture if needed

        // Act
        balanceNotificationProducer.sendBalanceEnquiryEvent(notificationDto);

        // Assert
        verify(kafkaTemplate, times(1)).send("balance-enquiry", notificationDto);
    }

    @Test
    void testSendBalanceEnquiryEvent_failure_logsError() {
        NotificationDto notificationDto = new NotificationDto();

        // Simulate Kafka send throwing exception
        doThrow(new RuntimeException("Kafka down"))
            .when(kafkaTemplate).send(anyString(), any(NotificationDto.class));

        // Act
        balanceNotificationProducer.sendBalanceEnquiryEvent(notificationDto);

        // Assert
        verify(kafkaTemplate, times(1)).send(anyString(), any(NotificationDto.class));
    }
}

