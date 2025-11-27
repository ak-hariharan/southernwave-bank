package com.southernwavebank.notification_service.service;
//package com.bankofindia.notification_service.service;
//
//import static org.junit.jupiter.api.Assertions.assertEquals;
//
//
//import static org.junit.jupiter.api.Assertions.assertNotNull;
//import static org.junit.jupiter.api.Assertions.assertThrows;
//import static org.junit.jupiter.api.Assertions.assertTrue;
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.ArgumentMatchers.anyString;
//import static org.mockito.Mockito.doAnswer;
//import static org.mockito.Mockito.doNothing;
//import static org.mockito.Mockito.doThrow;
//import static org.mockito.Mockito.mock;
//import static org.mockito.Mockito.never;
//import static org.mockito.Mockito.times;
//import static org.mockito.Mockito.verify;
//import static org.mockito.Mockito.when;
//
//import java.math.BigDecimal;
//import java.time.LocalDateTime;
//import java.util.List;
//
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.ArgumentCaptor;
//import org.mockito.Captor;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.Spy;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.mail.MailSendException;
//import org.springframework.mail.SimpleMailMessage;
//import org.springframework.mail.javamail.JavaMailSender;
//
//import com.bankofindia.notification_service.circuitbreaker.NotificationCircuitBreaker;
//import com.bankofindia.notification_service.exception.NotificationFailedException;
//import com.bankofindia.notification_service.external.ExternalAccountServiceClient;
//import com.bankofindia.notification_service.external.ExternalReportServiceClient;
//import com.bankofindia.notification_service.external.ExternalUserServiceClient;
//import com.bankofindia.notification_service.model.AccountStatus;
//import com.bankofindia.notification_service.model.NotificationStatus;
//import com.bankofindia.notification_service.model.NotificationType;
//import com.bankofindia.notification_service.model.TransactionType;
//import com.bankofindia.notification_service.model.dto.NotificationDto;
//import com.bankofindia.notification_service.model.entity.Notification;
//import com.bankofindia.notification_service.model.externaldto.TransactionDto;
//import com.bankofindia.notification_service.model.response.Response;
//import com.bankofindia.notification_service.repository.NotificationRepository;
//import com.bankofindia.notification_service.serviceimpl.NotificationServiceImpl;
//import com.bankofindia.notification_service.utils.FetchTimeAndDate;
//
//import jakarta.mail.MessagingException;
//import jakarta.mail.internet.MimeMessage;
//
//import org.mockito.junit.jupiter.MockitoExtension;
//
//@ExtendWith(MockitoExtension.class)
//class NotificationServiceImplTest {
//
//	@Spy
//    @InjectMocks
//    private NotificationServiceImpl notificationService;
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
//    @Mock
//    private NotificationCircuitBreaker notificationCircuitBreaker;
//
//    @Captor
//    ArgumentCaptor<Notification> notificationCaptor;
//
//    @Test
//    void testSendEmail_UserCreatedEvent_Success() {
//        // Given
//        NotificationDto dto = new NotificationDto();
//        dto.setEmailId("test@example.com");
//        dto.setUsername("JohnDoe");
//        dto.setContactNumber("9876543210");
//        dto.setTransactionTime(LocalDateTime.now());
//
//        SimpleMailMessage expectedMessage = new SimpleMailMessage();
//        expectedMessage.setTo("test@example.com");
//        expectedMessage.setSubject("Greetings!");
//        expectedMessage.setText("Dear JohnDoe,\n\n"
//            + "You have been successfully registered with Bank of India.\n"
//            + "Your account creation confirmation will follow shortly.\n\n"
//            + "Your password is a combination of:\n"
//            + "- First 4 letters of your username\n"
//            + "- Date of registration (yyyyMMdd)\n"
//            + "- '@' symbol\n"
//            + "- Last 2 digits of your contact number\n\n"
//            + "Thank you for choosing Bank of India!");
//
//        // When
//        notificationService.sendEmail(dto, "USER_CREATED");
//
//        // Then
//        verify(mailSender, times(1)).send(any(SimpleMailMessage.class));
//        verify(notificationRepository, times(1)).save(notificationCaptor.capture());
//
//        Notification savedNotification = notificationCaptor.getValue();
//        assertEquals(NotificationStatus.SENT, savedNotification.getNotificationStatus());
//        assertEquals(dto.getEmailId(), savedNotification.getEmail());
//        assertEquals(dto.getContactNumber(), savedNotification.getPhoneNumber());
//        assertEquals(NotificationType.EMAIL, savedNotification.getNotificationType());
//        assertNotNull(savedNotification.getMessage());
//    }
//    
//    
//    @Test
//    void testSendEmail_AccountCreatedEvent_Success() {
//        // Given
//        NotificationDto dto = new NotificationDto();
//        dto.setEmailId("test@example.com");
//        dto.setUsername("JohnDoe");
//        dto.setAccountNumber("1234567890");
//
//        // When
//        notificationService.sendEmail(dto, "ACCOUNT_CREATED");
//
//        // Then
//        verify(mailSender, times(1)).send(any(SimpleMailMessage.class));
//        verify(notificationRepository, times(1)).save(notificationCaptor.capture());
//
//        Notification savedNotification = notificationCaptor.getValue();
//        assertEquals(NotificationStatus.SENT, savedNotification.getNotificationStatus());
//        assertEquals(dto.getEmailId(), savedNotification.getEmail());
//        assertEquals(NotificationType.EMAIL, savedNotification.getNotificationType());
//        assertTrue(savedNotification.getMessage().contains(dto.getAccountNumber()));
//    }
//    
//    @Test
//    void testSendEmail_AccountStatusActive_Success() {
//        // Given
//        NotificationDto dto = new NotificationDto();
//        dto.setUserId(123L);
//        dto.setAccountNumber("9876543210");
//        dto.setAccountStatus(AccountStatus.ACTIVE);
//
//        Response response = new Response();
//        response.setData("test@example.com");
//        ResponseEntity<Response> responseEntity = new ResponseEntity<>(response, HttpStatus.OK);
//
//        when(externalUserServiceClient.getUserEmail(123L)).thenReturn(responseEntity);
//
//        // When
//        notificationService.sendEmail(dto, "ACCOUNT_STATUS");
//
//        // Then
//        verify(mailSender, times(1)).send(any(SimpleMailMessage.class));
//        verify(notificationRepository, times(1)).save(notificationCaptor.capture());
//
//        Notification savedNotification = notificationCaptor.getValue();
//        assertEquals(NotificationStatus.SENT, savedNotification.getNotificationStatus());
//        assertEquals("test@example.com", savedNotification.getEmail());
//        assertEquals(NotificationType.EMAIL, savedNotification.getNotificationType());
//        assertTrue(savedNotification.getMessage().contains("is Now Active"));
//        assertTrue(savedNotification.getMessage().contains("9876543210"));
//    }
//
//
//    @Test
//    void testSendEmail_AccountStatusClosed_Success() {
//        // Given
//        NotificationDto dto = new NotificationDto();
//        dto.setUserId(123L);
//        dto.setAccountNumber("9876543210");
//        dto.setAccountStatus(AccountStatus.CLOSED);
//
//        Response userResponse = new Response();
//        userResponse.setData("test@example.com");
//        ResponseEntity<Response> responseEntity = new ResponseEntity<>(userResponse, HttpStatus.OK);
//
//        when(externalUserServiceClient.getUserEmail(123L)).thenReturn(responseEntity);
//
//        // When
//        notificationService.sendEmail(dto, "ACCOUNT_STATUS");
//
//        // Then
//        verify(mailSender, times(1)).send(any(SimpleMailMessage.class));
//        verify(notificationRepository, times(1)).save(notificationCaptor.capture());
//
//        Notification savedNotification = notificationCaptor.getValue();
//        assertEquals(NotificationStatus.SENT, savedNotification.getNotificationStatus());
//        assertEquals("test@example.com", savedNotification.getEmail());
//        assertEquals(NotificationType.EMAIL, savedNotification.getNotificationType());
//        assertTrue(savedNotification.getMessage().contains("is Closed"));
//    }
//    
//    @Test
//    void testSendEmail_TransactionEvent_Success() {
//        // Given
//        NotificationDto dto = new NotificationDto();
//        dto.setAccountNumber("123456789012");
//        dto.setTransactionType(TransactionType.WITHDRAW);
//        dto.setAmount(new BigDecimal(1000.0));
//        dto.setTransactionTime(LocalDateTime.now());
//
//        Long userId = 789L;
//        String email = "user@example.com";
//        String contact = "9876543210";
//        
//        String formattedTime = FetchTimeAndDate.getFormattedTime(dto.getTransactionTime());
//
//        // Mock externalAccountServiceClient.getUserId()
//        when(externalAccountServiceClient.getUserId("123456789012")).thenReturn(userId);
//
//        // Mock externalUserServiceClient.getUserEmail()
//        Response userResponse = new Response();
//        userResponse.setData(email);
//        ResponseEntity<Response> emailResponse = new ResponseEntity<>(userResponse, HttpStatus.OK);
//        when(externalUserServiceClient.getUserEmail(userId)).thenReturn(emailResponse);
//
//        // Mock externalUserServiceClient.getContactNumber()
//        when(externalUserServiceClient.getContactNumber(userId)).thenReturn(contact);
//
//        // When
//        notificationService.sendEmail(dto, "TRANSACTION");
//
//        // Then
//        verify(mailSender, times(1)).send(any(SimpleMailMessage.class));
//        verify(notificationRepository, times(1)).save(notificationCaptor.capture());
//
//        Notification savedNotification = notificationCaptor.getValue();
//        assertEquals(NotificationStatus.SENT, savedNotification.getNotificationStatus());
//        assertEquals(email, savedNotification.getEmail());
//        assertEquals(contact, savedNotification.getPhoneNumber());
//        assertEquals(NotificationType.EMAIL, savedNotification.getNotificationType());
//        assertTrue(savedNotification.getMessage().contains("withdraw")
//                && savedNotification.getMessage().contains("1000")
//                && savedNotification.getMessage().contains("AC XXXX9012")
//                && savedNotification.getMessage().contains(formattedTime));
//
//    }
//
//    @Test
//    void testSendEmail_BalanceEnquiryEvent_Success() {
//        // Given
//        NotificationDto dto = new NotificationDto();
//        dto.setAccountNumber("123456789012");
//        dto.setAvailableBalance(new BigDecimal(5000));
//        dto.setTransactionTime(LocalDateTime.now());
//
//        Long userId = 123L;
//        String email = "test@example.com";
//        String contactNumber = "9876543210";
//
//        // Mock externalAccountServiceClient.getUserId()
//        when(externalAccountServiceClient.getUserId("123456789012")).thenReturn(userId);
//
//        // Mock externalUserServiceClient.getUserEmail()
//        Response emailResponse = new Response();
//        emailResponse.setData(email);
//        ResponseEntity<Response> responseEntity = new ResponseEntity<>(emailResponse, HttpStatus.OK);
//        when(externalUserServiceClient.getUserEmail(userId)).thenReturn(responseEntity);
//
//        // Mock externalUserServiceClient.getContactNumber()
//        when(externalUserServiceClient.getContactNumber(userId)).thenReturn(contactNumber);
//
//        // When
//        notificationService.sendEmail(dto, "BALANCE");
//
//        // Then
//        verify(mailSender, times(1)).send(any(SimpleMailMessage.class));
//        verify(notificationRepository, times(1)).save(notificationCaptor.capture());
//
//        Notification savedNotification = notificationCaptor.getValue();
//
//        String formattedTime = FetchTimeAndDate.getFormattedTime(dto.getTransactionTime());
//        assertTrue(savedNotification.getMessage().contains("available balance of 5000"));
//        assertTrue(savedNotification.getMessage().contains("XXXX9012"));
//        assertTrue(savedNotification.getMessage().contains(formattedTime));
//    }
//
//    @Test
//    void testSendEmail_NoTransactionEvent_WithTransactionTime_Success() {
//        // Given
//        NotificationDto dto = new NotificationDto();
//        dto.setAccountNumber("123456789012");
//        dto.setTransactionTime(LocalDateTime.now());
//
//        Long userId = 123L;
//        String email = "test@example.com";
//        String contactNumber = "9876543210";
//
//        when(externalAccountServiceClient.getUserId("123456789012")).thenReturn(userId);
//
//        Response emailResponse = new Response();
//        emailResponse.setData(email);
//        ResponseEntity<Response> responseEntity = new ResponseEntity<>(emailResponse, HttpStatus.OK);
//        when(externalUserServiceClient.getUserEmail(userId)).thenReturn(responseEntity);
//
//        when(externalUserServiceClient.getContactNumber(userId)).thenReturn(contactNumber);
//
//        // When
//        notificationService.sendEmail(dto, "NO_TRANSACTION");
//
//        // Then
//        verify(mailSender, times(1)).send(any(SimpleMailMessage.class));
//        verify(notificationRepository, times(1)).save(notificationCaptor.capture());
//
//        Notification savedNotification = notificationCaptor.getValue();
//
//        assertEquals(email, savedNotification.getEmail());
//        assertEquals(contactNumber, savedNotification.getPhoneNumber());
//        assertTrue(savedNotification.getMessage().contains("No transactions were recorded"));
//        assertTrue(savedNotification.getMessage().contains("XXXX9012"));
//    }
//    
//    @Test
//    void testSendEmail_NoTransactionEvent_WithoutTransactionTime_Success() {
//        // Given
//        NotificationDto dto = new NotificationDto();
//        dto.setAccountNumber("123456789012");
//        dto.setTransactionTime(null); // No time
//
//        Long userId = 123L;
//        String email = "test@example.com";
//        String contactNumber = "9876543210";
//
//        when(externalAccountServiceClient.getUserId("123456789012")).thenReturn(userId);
//
//        Response  emailResponse = new Response();
//        emailResponse.setData(email);
//        ResponseEntity<Response> responseEntity = new ResponseEntity<>(emailResponse, HttpStatus.OK);
//        when(externalUserServiceClient.getUserEmail(userId)).thenReturn(responseEntity);
//
//        when(externalUserServiceClient.getContactNumber(userId)).thenReturn(contactNumber);
//
//        // When
//        notificationService.sendEmail(dto, "NO_TRANSACTION");
//
//        // Then
//        verify(mailSender, times(1)).send(any(SimpleMailMessage.class));
//        verify(notificationRepository, times(1)).save(notificationCaptor.capture());
//
//        Notification savedNotification = notificationCaptor.getValue();
//
//        assertEquals(email, savedNotification.getEmail());
//        assertEquals(contactNumber, savedNotification.getPhoneNumber());
//        assertTrue(savedNotification.getMessage().contains("No transactions happened at all"));
//        assertTrue(savedNotification.getMessage().contains("XXXX9012"));
//    }
//    
//    @Test
//    void testSendEmail_OtpEvent_Success() {
//        // Given
//        NotificationDto dto = new NotificationDto();
//        dto.setEmailId("otpuser@example.com");
//        dto.setOtp(123456); // assume this is the OTP set in DTO
//
//        // When
//        notificationService.sendEmail(dto, "OTP");
//
//        // Then
//        verify(mailSender, times(1)).send(any(SimpleMailMessage.class));
//        verify(notificationRepository, times(1)).save(notificationCaptor.capture());
//
//        Notification savedNotification = notificationCaptor.getValue();
//
//        assertEquals("otpuser@example.com", savedNotification.getEmail());
//        assertTrue(savedNotification.getMessage().contains("Your OTP for password reset is: 123456"));
//        assertTrue(savedNotification.getMessage().contains("Please do not share this OTP with anyone"));
//    }
//
//    @Test
//    void testSendEmail_PasswordEvent_Success() {
//        // Given
//        NotificationDto dto = new NotificationDto();
//        dto.setEmailId("secureuser@example.com");
//
//        // When
//        notificationService.sendEmail(dto, "PASSWORD");
//
//        // Then
//        verify(mailSender, times(1)).send(any(SimpleMailMessage.class));
//        verify(notificationRepository, times(1)).save(notificationCaptor.capture());
//
//        Notification savedNotification = notificationCaptor.getValue();
//
//        assertEquals("secureuser@example.com", savedNotification.getEmail());
//        assertTrue(savedNotification.getMessage().contains("your password has been successfully updated"));
//        assertTrue(savedNotification.getMessage().contains("please contact our customer support immediately"));
//        assertTrue(savedNotification.getMessage().contains("Bank Of India"));
//    }
//
//
//
//    @Test
//    void testSendEmail_UnknownEventType_NoActionTaken() {
//        // Given
//        NotificationDto dto = new NotificationDto();
//
//        // When
//        notificationService.sendEmail(dto, "UNKNOWN_EVENT_TYPE");
//
//        // Then
//        verify(mailSender, never()).send(any(SimpleMailMessage.class));
//        verify(notificationRepository, never()).save(any(Notification.class));
//    }
//    
//    @Test
//    void testSendEmail_WhenNotificationFails_ShouldSaveWithFailedStatusAndThrowException() {
//        // Given
//        NotificationDto dto = new NotificationDto();
//        dto.setEmailId("fail@example.com");
//        dto.setUsername("FailureUser");
//        dto.setContactNumber("9999999999");
//        dto.setTransactionTime(LocalDateTime.now());
//
//        // Simulate failure during mail sending
//        doThrow(new NotificationFailedException("Simulated mail failure"))
//            .when(mailSender).send(any(SimpleMailMessage.class));
//
//        // When & Then
//        NotificationFailedException exception = assertThrows(NotificationFailedException.class, () -> {
//            notificationService.sendEmail(dto, "USER_CREATED");
//        });
//
//        assertEquals("Failure in sending the notification", exception.getMessage());
//
//        // Verify that a notification was saved with status FAILED
//        verify(notificationRepository).save(notificationCaptor.capture());
//        Notification savedNotification = notificationCaptor.getValue();
//
//        assertEquals(NotificationStatus.FAILED, savedNotification.getNotificationStatus());
//        assertEquals(dto.getEmailId(), savedNotification.getEmail());
//        assertEquals(dto.getContactNumber(), savedNotification.getPhoneNumber());
//    }
//
//    @Test
//    void testSendEmailWithTransactionHistoryAttachment_Success() throws MessagingException {
//        // Given
//        TransactionDto transactionDto = new TransactionDto();
//        transactionDto.setAccountNumber("123456789012");
//        transactionDto.setTransactionTime(LocalDateTime.now());
//        List<TransactionDto> transactions = List.of(transactionDto);
//
//        String email = "customer@example.com";
//        String contactNumber = "9876543210";
//        String filePath = "/path/to/generated/report.pdf";
//        Long userId = 1L;
//
//        // Mock external service responses
//        when(externalAccountServiceClient.getUserId("123456789012")).thenReturn(userId);
//        when(externalUserServiceClient.getUserEmail(userId))
//            .thenReturn(ResponseEntity.ok(new Response("200", "Success", email)));
//        when(externalUserServiceClient.getContactNumber(userId)).thenReturn(contactNumber);
//        
//        when(notificationCircuitBreaker.genearteFromUserService(transactions, email) ).thenReturn(filePath);
//
//        // Mock MimeMessage creation and sending
//        MimeMessage mimeMessage = mock(MimeMessage.class);
//        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
//        doNothing().when(mailSender).send(any(MimeMessage.class));
//
//        // When
//        notificationService.sendEmail(transactions, LocalDateTime.now());
//
//        // Then
//        verify(notificationRepository).save(notificationCaptor.capture());
//        Notification savedNotification = notificationCaptor.getValue();
//
//        assertEquals(email, savedNotification.getEmail());
//        assertEquals("Dear Customer, Please find your transaction history attached. Thank you!", savedNotification.getMessage());
//        assertEquals(contactNumber, savedNotification.getPhoneNumber());
//        assertEquals(NotificationStatus.SENT, savedNotification.getNotificationStatus());
//    }
//    
//    @Test
//    void testSendEmailWhenMessagingExceptionOccurs() throws Exception {
//        // Given
//        TransactionDto transactionDto = new TransactionDto();
//        transactionDto.setAccountNumber("123456789012");
//        transactionDto.setTransactionTime(LocalDateTime.now());
//        List<TransactionDto> transactions = List.of(transactionDto);
//
//        Long userId = 123L;
//        String email = "test@example.com";
//        String contact = "9876543210";
//        String filePath = "/fake/path/to/report.pdf";
//
//        // Mocks for user data
//        when(externalAccountServiceClient.getUserId("123456789012")).thenReturn(userId);
//        when(externalUserServiceClient.getUserEmail(userId))
//                .thenReturn(ResponseEntity.ok(new Response("200", "OK", email)));
//        when(externalUserServiceClient.getContactNumber(userId)).thenReturn(contact);
//
//        // ✅ Corrected Mock for circuit breaker
//        when(notificationCircuitBreaker.genearteFromUserService(transactions, email)).thenReturn(filePath);
//
//        // Mock mail sending with exception
//        MimeMessage mimeMessage = mock(MimeMessage.class);
//        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
//        doThrow(new MailSendException("Simulated mail failure"))
//                .when(mailSender).send(any(MimeMessage.class));
//
//        // When & Then
//        NotificationFailedException exception = assertThrows(NotificationFailedException.class, () -> {
//            notificationService.sendEmail(transactions, LocalDateTime.now());
//        });
//
//        assertEquals("Failure in sending the notification", exception.getMessage());
//        verify(notificationRepository).save(notificationCaptor.capture());
//
//        Notification savedNotification = notificationCaptor.getValue();
//        assertEquals(NotificationStatus.FAILED, savedNotification.getNotificationStatus());
//        assertEquals(email, savedNotification.getEmail());
//        assertEquals(contact, savedNotification.getPhoneNumber());
//    }
//
//    @Test
//    void testSendEmail_WhenReportGenerationFails_ShouldSendFallbackEmail() {
//        // Given
//        TransactionDto transactionDto = new TransactionDto();
//        transactionDto.setAccountNumber("123456789012");
//        transactionDto.setTransactionTime(LocalDateTime.now());
//        List<TransactionDto> transactions = List.of(transactionDto);
//
//        String email = "customer@example.com";
//        String contactNumber = "9876543210";
//        Long userId = 1L;
//
//        // Mock required service responses
//        when(externalAccountServiceClient.getUserId("123456789012")).thenReturn(userId);
//        when(externalUserServiceClient.getUserEmail(userId))
//            .thenReturn(ResponseEntity.ok(new Response("200", "Success", email)));
//        when(notificationCircuitBreaker.genearteFromUserService(transactions,email)).thenReturn(null); // simulate failure
//        when(externalUserServiceClient.getContactNumber(userId)).thenReturn(contactNumber);
//
//        // When
//        notificationService.sendEmail(transactions, LocalDateTime.now());
//
//        // Then
//        verify(notificationRepository).save(notificationCaptor.capture());
//        Notification savedNotification = notificationCaptor.getValue();
//
//        assertEquals(NotificationStatus.SENT, savedNotification.getNotificationStatus());
//        assertEquals(email, savedNotification.getEmail());
//        assertEquals(contactNumber, savedNotification.getPhoneNumber());
//
//        assertEquals(
//            "Dear Customer,\n\nDue to an unexpected system issue, we are currently unable " +
//            "to generate your transaction report. " +
//            "Please try again later. We apologize for the inconvenience.\n\nRegards,\nBank Of India",
//            savedNotification.getMessage()
//        );
//    }
//
//
//   
//}
