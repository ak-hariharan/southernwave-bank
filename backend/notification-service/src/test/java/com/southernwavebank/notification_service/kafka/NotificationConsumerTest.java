package com.southernwavebank.notification_service.kafka;

import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.southernwavebank.notification_service.kafka.NotificationConsumer;
import com.southernwavebank.notification_service.model.dto.NotificationDto;
import com.southernwavebank.notification_service.service.NotificationService;

@ExtendWith(MockitoExtension.class)
public class NotificationConsumerTest {

    @InjectMocks
    private NotificationConsumer notificationConsumer;

    @Mock
    private NotificationService notificationService;

    @Test
    void testConsumerUserCreation_shouldInvokeSendEmailWithUserCreatedType() {
        NotificationDto dto = new NotificationDto();
        notificationConsumer.consumerUserCreation(dto);

        verify(notificationService, times(1)).sendEmail(dto, "USER_CREATED");
    }

    @Test
    void testConsumeAccountCreation_shouldInvokeSendEmailWithAccountCreatedType() {
        NotificationDto dto = new NotificationDto();
        notificationConsumer.consumeAccountCreation(dto);

        verify(notificationService, times(1)).sendEmail(dto, "ACCOUNT_CREATED");
    }

    @Test
    void testConsumeAccountStatus_shouldInvokeSendEmailWithAccountStatusType() {
        NotificationDto dto = new NotificationDto();
        notificationConsumer.consumeAccountStatus(dto);

        verify(notificationService, times(1)).sendEmail(dto, "ACCOUNT_STATUS");
    }

    @Test
    void testConsumeTransaction_shouldInvokeSendEmailWithTransactionType() {
        NotificationDto dto = new NotificationDto();
        notificationConsumer.consumeTransaction(dto);

        verify(notificationService, times(1)).sendEmail(dto, "TRANSACTION");
    }

    @Test
    void testConsumeBalanceEnquiry_shouldInvokeSendEmailWithBalanceType() {
        NotificationDto dto = new NotificationDto();
        notificationConsumer.consumeBalanceEnquiry(dto);

        verify(notificationService, times(1)).sendEmail(dto, "BALANCE");
    }

    @Test
    void testConsumeNoTransactionHistory_shouldInvokeSendEmailWithNoTransactionType() {
        NotificationDto dto = new NotificationDto();
        notificationConsumer.consumeNoTransactionHistory(dto);

        verify(notificationService, times(1)).sendEmail(dto, "NO_TRANSACTION");
    }

    @Test
    void testConsumeTransactionHistory_shouldConvertAndSendEmail() {
        // Given a mocked list of LinkedHashMap as Kafka would send
        Map<String, Object> transactionMap = new LinkedHashMap<>();
        transactionMap.put("accountNumber", "1234567890");
        transactionMap.put("amount", 1000.0);
        transactionMap.put("status", "SUCCESS");
        transactionMap.put("transactionTime", LocalDateTime.now().toString());
        transactionMap.put("transactionType", "DEPOSIT");

        List<LinkedHashMap<String, Object>> payload = List.of(new LinkedHashMap<>(transactionMap));

        // When
        notificationConsumer.consumeTransactionHistory(payload);

        // Then
        verify(notificationService, times(1)).sendEmail(anyList(), LocalDateTime.now());
    }
    
    @Test
    void testConsumeOtpNotification() {
        NotificationDto dto = new NotificationDto();
        notificationConsumer.consumeOtpNotification(dto);

        verify(notificationService, times(1)).sendEmail(dto, "OTP");
    }
    
    @Test
    void consumePasswordUpdateNotification() {
        NotificationDto dto = new NotificationDto();
        notificationConsumer.consumePasswordUpdateNotification(dto);

        verify(notificationService, times(1)).sendEmail(dto, "PASSWORD");
    }
}
