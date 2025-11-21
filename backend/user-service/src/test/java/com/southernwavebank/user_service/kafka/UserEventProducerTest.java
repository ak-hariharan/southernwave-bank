package com.southernwavebank.user_service.kafka;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.time.LocalDateTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.kafka.core.KafkaTemplate;

import com.southernwavebank.user_service.kafka.UserEventProducer;
import com.southernwavebank.user_service.model.externaldto.NotificationDto;

public class UserEventProducerTest {

    private KafkaTemplate<String, NotificationDto> kafkaTemplate;
    private UserEventProducer userEventProducer;

    @BeforeEach
    void setUp() {
        kafkaTemplate = Mockito.mock(KafkaTemplate.class);
        userEventProducer = new UserEventProducer(kafkaTemplate);
    }

    @Test
    void sendUserCreatedEvent_shouldSendEventSuccessfully() {
        // Arrange
        NotificationDto dto = new NotificationDto();
        dto.setUsername("John Doe");
        dto.setEmailId("john@example.com");
        dto.setContactNumber("1234567890");
        dto.setTime(LocalDateTime.now());

        // Act
        userEventProducer.sendUserCreatedEvent(dto);

        // Assert
        verify(kafkaTemplate, times(1)).send("user-created", dto);
    }

    @Test
    void sendUserCreatedEvent_shouldLogErrorWhenKafkaFails() {
        // Arrange
        NotificationDto dto = new NotificationDto();
        dto.setUsername("Jane Doe");
        dto.setEmailId("jane@example.com");
        dto.setContactNumber("9876543210");
        dto.setTime(LocalDateTime.now());

        // Simulate Kafka failure
        doThrow(new RuntimeException("Kafka error")).when(kafkaTemplate)
                .send(anyString(), any(NotificationDto.class));

        // Act
        userEventProducer.sendUserCreatedEvent(dto);

        // Assert
        verify(kafkaTemplate, times(1)).send("user-created", dto);
        // Verifies that the catch block is triggered when exception occurs
    }
}