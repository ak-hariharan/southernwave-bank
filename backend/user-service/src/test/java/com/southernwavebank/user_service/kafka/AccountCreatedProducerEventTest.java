package com.southernwavebank.user_service.kafka;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.kafka.core.KafkaTemplate;

import com.southernwavebank.user_service.kafka.AccountCreatedEventProducer;
import com.southernwavebank.user_service.model.dto.UserDto;

public class AccountCreatedProducerEventTest {

	private KafkaTemplate<String, UserDto> kafkaTemplate;
	private AccountCreatedEventProducer eventProducer;

	@BeforeEach
	void setUp() {
		kafkaTemplate = Mockito.mock(KafkaTemplate.class);
		eventProducer = new AccountCreatedEventProducer(kafkaTemplate);
	}

	@Test
	void sendAccountCreatedEvent_shouldSendMessageToKafka() {
		// Arrange
		UserDto userDto = new UserDto();
		userDto.setUserId(1L);
		userDto.setUsername("John Doe");
		userDto.setEmailId("john@example.com");
		userDto.setContactNumber("1234567890");

		// Act
		eventProducer.sendAccountCreatedEvent(userDto);

		// Assert
		verify(kafkaTemplate, times(1)).send("account-create-initate", userDto);
	}
	
	@Test
    void sendAccountCreatedEvent_shouldLogErrorOnKafkaException() {
        // Arrange
        UserDto userDto = new UserDto();
        userDto.setUserId(2L);
        userDto.setUsername("Jane Doe");
        userDto.setEmailId("jane@example.com");
        userDto.setContactNumber("0987654321");

        doThrow(new RuntimeException("Kafka down")).when(kafkaTemplate).send(anyString(), any(UserDto.class));

        // Act
        eventProducer.sendAccountCreatedEvent(userDto);

        // Assert
        verify(kafkaTemplate, times(1)).send("account-create-initate", userDto);
        // We can't verify logs directly here without a logging framework assertion.
        // But this verifies the exception path is exercised.
    }

}
