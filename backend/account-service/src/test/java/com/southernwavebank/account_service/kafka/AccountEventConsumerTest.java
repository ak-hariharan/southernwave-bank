package com.southernwavebank.account_service.kafka;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.southernwavebank.account_service.kafka.AccountCreatedEventConsumer;
import com.southernwavebank.account_service.model.externaldto.UserDto;
import com.southernwavebank.account_service.service.AccountService;

@ExtendWith(MockitoExtension.class)
public class AccountEventConsumerTest {

    @InjectMocks
    private AccountCreatedEventConsumer accountEventConsumer;

    @Mock
    private AccountService accountService;

    @Test
    void testHandleUserCreated_shouldInvokeCreateAccount() {
        // Given
        UserDto userDto = new UserDto();
        userDto.setUserId(1001L);
        userDto.setUsername("John Doe");
        userDto.setEmailId("john.doe@example.com");

        // When
        accountEventConsumer.handleUserCreated(userDto);

        // Then
        // We don’t control the time inside the method, so we just verify the method is called
        verify(accountService, times(1)).createAccount(eq(userDto), any(LocalDateTime.class));
    }
}

