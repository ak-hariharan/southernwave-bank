package com.southernwavebank.notification_service.circuitbreaker;
//package com.bankofindia.notification_service.circuitbreaker;
//
//import static org.junit.jupiter.api.Assertions.assertEquals;
//import static org.junit.jupiter.api.Assertions.assertNull;
//import static org.junit.jupiter.api.Assertions.assertTrue;
//import static org.mockito.Mockito.verify;
//import static org.mockito.Mockito.when;
//
//import java.util.List;
//
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.ArgumentCaptor;
//import org.mockito.Captor;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//import org.springframework.http.ResponseEntity;
//import org.springframework.mail.SimpleMailMessage;
//import org.springframework.mail.javamail.JavaMailSender;
//
//import com.bankofindia.notification_service.external.ExternalAccountServiceClient;
//import com.bankofindia.notification_service.external.ExternalReportServiceClient;
//import com.bankofindia.notification_service.external.ExternalUserServiceClient;
//import com.bankofindia.notification_service.model.externaldto.TransactionDto;
//import com.bankofindia.notification_service.model.response.Response;
//import com.bankofindia.notification_service.repository.NotificationRepository;
//
//@ExtendWith(MockitoExtension.class)
//class NotificationCircuitBreakerTest {
//
//    @InjectMocks
//    private NotificationCircuitBreaker notificationCircuitBreaker;
//
//    @Mock
//    private JavaMailSender mailSender;
//
//    @Mock
//    private NotificationRepository notificationRepository;
//
//    @Mock
//    private ExternalUserServiceClient externalUserServiceClient;
//
//    @Mock
//    private ExternalAccountServiceClient externalAccountServiceClient;
//
//    @Mock
//    private ExternalReportServiceClient externalReportServiceClient;
//
//    @Captor
//    private ArgumentCaptor<SimpleMailMessage> mailCaptor;
//
//    @Test
//    void testFallbackGenerateReportFromReportService_SendsFallbackEmail() {
//        // Given
//        TransactionDto transaction = new TransactionDto();
//        transaction.setAccountNumber("123456789012");
//        List<TransactionDto> transactions = List.of(transaction);
//
//        Long userId = 1L;
//        String email = "user@example.com";
//        String contact = "9876543210";
//
//        when(externalAccountServiceClient.getUserId("123456789012")).thenReturn(userId);
//        Response response = new Response("200", "OK", email);
//        when(externalUserServiceClient.getUserEmail(userId)).thenReturn(ResponseEntity.ok(response));
//        when(externalUserServiceClient.getContactNumber(userId)).thenReturn(contact);
//
//        // When
//        String result = notificationCircuitBreaker.fallbackGenerateReportFromReportService(transactions,email, new RuntimeException("Simulated failure"));
//
//        // Then
//        assertNull(result); // fallback returns null
//        verify(mailSender).send(mailCaptor.capture());
//
//        SimpleMailMessage sentMessage = mailCaptor.getValue();
//        assertEquals(email, sentMessage.getTo()[0]);
//        assertEquals("Transaction Report Unavailable", sentMessage.getSubject());
//        assertTrue(sentMessage.getText().contains("unable to generate your transaction report"));
//    }
//}
