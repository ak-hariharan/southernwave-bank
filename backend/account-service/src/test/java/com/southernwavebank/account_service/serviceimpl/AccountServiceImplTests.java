package com.southernwavebank.account_service.serviceimpl;
//package com.bankofindia.account_service.serviceimpl;
//
//import java.lang.reflect.Field;
//
//import java.math.BigDecimal;
//import java.time.LocalDateTime;
//import java.util.Optional;
//
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//import org.modelmapper.ModelMapper;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.http.ResponseEntity;
//
//import static org.mockito.Mockito.when;
//import static org.mockito.ArgumentMatchers.eq;
//import static org.mockito.Mockito.any;
//import static org.mockito.Mockito.doAnswer;
//import static org.mockito.Mockito.doThrow;
//import static org.mockito.Mockito.verify;
//import static org.mockito.Mockito.never;
//import static org.mockito.Mockito.times;
//import static org.junit.jupiter.api.Assertions.*;
//
//import com.bankofindia.account_service.exception.DatabaseSaveException;
//import com.bankofindia.account_service.exception.InsufficientBalanceException;
//import com.bankofindia.account_service.exception.ResourceConflict;
//import com.bankofindia.account_service.exception.ResourceNotFound;
//import com.bankofindia.account_service.exception.UnauthorizedAccessException;
//import com.bankofindia.account_service.external.UserService;
//import com.bankofindia.account_service.kafka.AccountCreatedEventProducer;
//import com.bankofindia.account_service.kafka.AccountStatusProducer;
//import com.bankofindia.account_service.kafka.BalanceNotificationProducer;
//import com.bankofindia.account_service.kafka.TransactionEventProducer;
//import com.bankofindia.account_service.model.AccountStatus;
//import com.bankofindia.account_service.model.AccountType;
//import com.bankofindia.account_service.model.dto.AccountDto;
//import com.bankofindia.account_service.model.dto.DepositResponseDto;
//import com.bankofindia.account_service.model.dto.WithdrawResponseDto;
//import com.bankofindia.account_service.model.entity.Account;
//import com.bankofindia.account_service.model.externaldto.NotificationDto;
//import com.bankofindia.account_service.model.externaldto.TransactionDto;
//import com.bankofindia.account_service.model.externaldto.UserDto;
//import com.bankofindia.account_service.model.response.Response;
//import com.bankofindia.account_service.repository.AccountRepository;
//
//@ExtendWith(MockitoExtension.class)
//class AccountServiceImplTests {
//
//    @InjectMocks
//    private AccountServiceImpl accountService;
//
//    @Mock
//    private AccountRepository accountRepo;
//
//    @Mock
//    private UserService userService;
//
//    @Mock
//    private TransactionEventProducer transactionEventProducer;
//
//    @Mock
//    private AccountCreatedEventProducer accountCreatedEventProducer;
//
//    @Mock
//    private AccountStatusProducer accountStatusProducer;
//
//    @Mock
//    private BalanceNotificationProducer balanceNotificationProducer;
//
//    @Mock
//    private ModelMapper modelMapper;
//
//    @Value("${spring.application.ok:SUCCESS}")
//    private String success;
//    
//    @BeforeEach
//    void setUp() throws Exception {
//        Field successField = AccountServiceImpl.class.getDeclaredField("success");
//        successField.setAccessible(true);
//        successField.set(accountService, "SUCCESS");
//
//        Field notfoundField = AccountServiceImpl.class.getDeclaredField("notfound");
//        notfoundField.setAccessible(true);
//        notfoundField.set(accountService, "NOTFOUND");
//    }
//
//    //-------------------------create account starts----------------------------------------------
//
//    @Test
//    void testCreateAccount_successfullyCreatesAccount() {
//        // Given
//        UserDto userDto = new UserDto();
//        userDto.setEmailId("test@example.com");
//        userDto.setContactNumber("1234567890");
//
//        Response userServiceResponse = Response.builder().data(1L).build();
//        when(userService.getUserId("test@example.com")).thenReturn(ResponseEntity.ok(userServiceResponse));
//        when(accountRepo.findAccountByUserIdAndAccountType(1L, AccountType.SAVINGS))
//            .thenReturn(Optional.empty());
//
//        // When
//        Response response = accountService.createAccount(userDto, LocalDateTime.now());
//
//        // Then
//        assertNotNull(response);
//        assertEquals("SUCCESS", response.getResponseCode());
//        assertEquals("Account created successfully", response.getMessage());
//        verify(accountRepo).save(any(Account.class));
//        verify(accountCreatedEventProducer).sendAccountCreatedEvent(any(NotificationDto.class));
//    }
//
//    @Test
//    void testCreateAccount_userNotFound_returnsErrorResponse() {
//        // Given
//        UserDto userDto = new UserDto();
//        userDto.setEmailId("missing@example.com");
//
//        Response notFoundResponse = Response.builder().data(null).build();
//        when(userService.getUserId("missing@example.com")).thenReturn(ResponseEntity.ok(notFoundResponse));
//
//        // When
//        Response response = accountService.createAccount(userDto, LocalDateTime.now());
//
//        // Then
//        assertNotNull(response);
//        assertEquals("NOTFOUND", response.getResponseCode());
//        assertEquals("User not exist in the user service", response.getMessage());
//        verify(accountRepo, never()).save(any());
//    }
//
//    @Test
//    void testCreateAccount_existingAccount_throwsConflict() {
//        // Given
//        UserDto userDto = new UserDto();
//        userDto.setEmailId("conflict@example.com");
//
//        Response response = Response.builder().data(2L).build();
//        when(userService.getUserId("conflict@example.com")).thenReturn(ResponseEntity.ok(response));
//        when(accountRepo.findAccountByUserIdAndAccountType(2L, AccountType.SAVINGS))
//            .thenReturn(Optional.of(new Account()));
//
//        // Then
//        assertThrows(ResourceConflict.class, () -> 
//            accountService.createAccount(userDto, LocalDateTime.now())
//        );
//    }
//    
//    //-------------------------create account ends----------------------------------------------
//    
//    @Test
//    void testGetAccountDetails_withConsumerRoleAndAuthorizedUser_returnsAccount() {
//        Account account = new Account();
//        account.setAccountNumber("ACC123");
//        account.setUserId(100L);
//
//        when(accountRepo.findByAccountNumber("ACC123")).thenReturn(Optional.of(account));
//
//        Response response = accountService.getAccountDetails("ACC123", "CONSUMER", "100");
//
//        assertNotNull(response);
//        assertEquals("SUCCESS", response.getResponseCode());
//        assertEquals("Account details retrieved successfully", response.getMessage());
//        assertEquals(account, response.getData());
//    }
//    
//    @Test
//    void testGetAccountDetails_accountNotFound_throwsException() {
//        when(accountRepo.findByAccountNumber("ACC123")).thenReturn(Optional.empty());
//
//        assertThrows(ResourceNotFound.class, () -> 
//            accountService.getAccountDetails("ACC123", "CONSUMER", "100")
//        );
//    }
//    
//    @Test
//    void testGetAccountDetails_unauthorizedConsumerAccess_throwsException() {
//        Account account = new Account();
//        account.setAccountNumber("ACC123");
//        account.setUserId(200L); // different from the userId in request
//
//        when(accountRepo.findByAccountNumber("ACC123")).thenReturn(Optional.of(account));
//
//        assertThrows(UnauthorizedAccessException.class, () -> 
//            accountService.getAccountDetails("ACC123", "CONSUMER", "100")
//        );
//    }
//    
//    @Test
//    void testGetAccountDetails_withAdminRole_returnsAccount() {
//        Account account = new Account();
//        account.setAccountNumber("ACC123");
//        account.setUserId(999L); // irrelevant since role != CONSUMER
//
//        when(accountRepo.findByAccountNumber("ACC123")).thenReturn(Optional.of(account));
//
//        Response response = accountService.getAccountDetails("ACC123", "ADMIN", "any");
//
//        assertNotNull(response);
//        assertEquals("SUCCESS", response.getResponseCode());
//        assertEquals(account, response.getData());
//    }
//    
//    @Test
//    void testUpdateAccountStatus_accountExists_statusUpdatedSuccessfully() {
//        // Given
//        String accountNumber = "ACC123";
//        AccountStatus newStatus = AccountStatus.ACTIVE;
//
//        Account account = new Account();
//        account.setAccountNumber(accountNumber);
//        account.setAccountStatus(AccountStatus.PENDING); // old status
//
//        AccountDto accountDto = new AccountDto();
//        accountDto.setAccountNumber(accountNumber);
//        accountDto.setAccountStatus(newStatus);
//
//        when(accountRepo.findByAccountNumber(accountNumber)).thenReturn(Optional.of(account));
//        when(modelMapper.map(account, AccountDto.class)).thenReturn(accountDto);
//
//        // When
//        Response response = accountService.updateAccountStatus(accountNumber, newStatus);
//
//        // Then
//        assertNotNull(response);
//        assertEquals("SUCCESS", response.getResponseCode());
//        assertEquals("Account details retrieved successfully", response.getMessage());
//        assertEquals(accountDto, response.getData());
//        assertEquals(AccountStatus.ACTIVE, account.getAccountStatus());
//        verify(accountRepo).save(account);
//    }
//    
//    @Test
//    void testUpdateAccountStatus_accountNotFound_throwsException() {
//        // Given
//        String accountNumber = "NON_EXISTENT";
//        AccountStatus newStatus = AccountStatus.ACTIVE;
//
//        when(accountRepo.findByAccountNumber(accountNumber)).thenReturn(Optional.empty());
//
//        // Then
//        assertThrows(ResourceNotFound.class, () -> 
//            accountService.updateAccountStatus(accountNumber, newStatus)
//        );
//        verify(accountRepo, never()).save(any());
//    }
//    
//    @Test
//    void testGetBalance_asConsumer_successful() {
//        // Given
//        String accountNumber = "ACC123";
//        String role = "CONSUMER";
//        String userId = "1001";
//        LocalDateTime time = LocalDateTime.now();
//
//        Account account = new Account();
//        account.setAccountNumber(accountNumber);
//        account.setAvailableBalance(new BigDecimal("1500.00"));
//        account.setUserId(Long.parseLong(userId));
//
//        when(accountRepo.findByAccountNumber(accountNumber)).thenReturn(Optional.of(account));
//
//        // When
//        Response response = accountService.getBalance(accountNumber, time, role, userId);
//
//        // Then
//        assertNotNull(response);
//        assertEquals("SUCCESS", response.getResponseCode());
//        assertEquals("Available Balance", response.getMessage());
//        assertEquals(new BigDecimal("1500.00"), response.getData());
//        verify(balanceNotificationProducer).sendBalanceEnquiryEvent(any());
//    }
//    
//    @Test
//    void testGetBalance_asOfficer_doesNotSendKafka() {
//        // Given
//        String accountNumber = "ACC123";
//        String role = "OFFICER";
//        String userId = "999"; // officer's ID doesn't matter
//        LocalDateTime time = LocalDateTime.now();
//
//        Account account = new Account();
//        account.setAccountNumber(accountNumber);
//        account.setAvailableBalance(new BigDecimal("2500.00"));
//        account.setUserId(123L); // some user ID
//
//        when(accountRepo.findByAccountNumber(accountNumber)).thenReturn(Optional.of(account));
//
//        // When
//        Response response = accountService.getBalance(accountNumber, time, role, userId);
//
//        // Then
//        assertNotNull(response);
//        assertEquals("SUCCESS", response.getResponseCode());
//        assertEquals(new BigDecimal("2500.00"), response.getData());
//        verify(balanceNotificationProducer, never()).sendBalanceEnquiryEvent(any());
//    }
//    
//    @Test
//    void testGetBalance_accountNotFound_throwsException() {
//        // Given
//        when(accountRepo.findByAccountNumber("ACC404")).thenReturn(Optional.empty());
//
//        // Then
//        assertThrows(ResourceNotFound.class, () -> 
//            accountService.getBalance("ACC404", LocalDateTime.now(), "CONSUMER", "1001")
//        );
//    }
//
//    @Test
//    void testGetBalance_unauthorizedAccess_throwsException() {
//        // Given
//        String userId = "1002"; // incoming user
//        Account account = new Account();
//        account.setAccountNumber("ACC321");
//        account.setUserId(999L); // different user
//
//        when(accountRepo.findByAccountNumber("ACC321")).thenReturn(Optional.of(account));
//
//        // Then
//        assertThrows(UnauthorizedAccessException.class, () -> 
//            accountService.getBalance("ACC321", LocalDateTime.now(), "CONSUMER", userId)
//        );
//    }
//    
//    //---------------------deposit method test cases starts here---------------------------------------------
//    
//    @Test
//    void testDepositMoney_validConsumer_successfulDeposit() {
//        // Given
//        String accountNumber = "ACC101";
//        String userId = "1001";
//        String role = "CONSUMER";
//        LocalDateTime time = LocalDateTime.now();
//
//        Account account = new Account();
//        account.setAccountNumber(accountNumber);
//        account.setAvailableBalance(new BigDecimal("500"));
//        account.setAccountStatus(AccountStatus.ACTIVE);
//        account.setUserId(Long.parseLong(userId));
//
//        TransactionDto transactionDto = new TransactionDto();
//        transactionDto.setAccountNumber(accountNumber);
//        transactionDto.setAmount(new BigDecimal("200"));
//
//        when(accountRepo.findByAccountNumber(accountNumber)).thenReturn(Optional.of(account));
//        when(accountRepo.save(any())).thenReturn(account);
//
//        // Simulate modelMapper.map(TransactionDto, DepositResponseDto)
//        doAnswer(invocation -> {
//            TransactionDto source = invocation.getArgument(0);
//            DepositResponseDto target = invocation.getArgument(1);
//            target.setAccountNumber(source.getAccountNumber());
//            target.setAmount(source.getAmount());
//            target.setTransactionTime(source.getTransactionTime());
//            return null;
//        }).when(modelMapper).map(any(TransactionDto.class), any(DepositResponseDto.class));
//
//        // When
//        Response response = accountService.depositMoney(transactionDto, time, role, userId);
//
//        // Then
//        assertNotNull(response);
//        assertEquals("SUCCESS", response.getResponseCode());
//
//        // Check fields of the returned DepositResponseDto
//        DepositResponseDto actualResponse = (DepositResponseDto) response.getData();
//        assertEquals(accountNumber, actualResponse.getAccountNumber());
//        assertEquals(new BigDecimal("200"), actualResponse.getAmount());
//        assertEquals(time, actualResponse.getTransactionTime());
//
//        verify(transactionEventProducer, times(2)).sendTransactionEvent(any());
//        verify(accountStatusProducer, never()).sendAccountStatusEvent(any());
//    }
//    
//    @Test
//    void testDepositMoney_balanceCrossesThreshold_triggersStatusChangeAndNotification() {
//        // Given
//        String accountNumber = "ACC999";
//        String userId = "1001";
//        String role = "CONSUMER";
//        LocalDateTime time = LocalDateTime.now();
//
//        Account account = new Account();
//        account.setAccountNumber(accountNumber);
//        account.setAvailableBalance(new BigDecimal("950"));
//        account.setAccountStatus(AccountStatus.PENDING);
//        account.setUserId(Long.parseLong(userId));
//
//        TransactionDto transactionDto = new TransactionDto();
//        transactionDto.setAccountNumber(accountNumber);
//        transactionDto.setAmount(new BigDecimal("100"));
//
//        when(accountRepo.findByAccountNumber(accountNumber)).thenReturn(Optional.of(account));
//        when(accountRepo.save(any())).thenReturn(account);
//
//        // Simulate modelMapper.map(TransactionDto, DepositResponseDto)
//        doAnswer(invocation -> {
//            TransactionDto source = invocation.getArgument(0);
//            DepositResponseDto target = invocation.getArgument(1);
//            target.setAccountNumber(source.getAccountNumber());
//            target.setAmount(source.getAmount());
//            target.setTransactionTime(source.getTransactionTime());
//            return null;
//        }).when(modelMapper).map(any(TransactionDto.class), any(DepositResponseDto.class));
//
//        // When
//        Response response = accountService.depositMoney(transactionDto, time, role, userId);
//
//        // Then
//        assertNotNull(response);
//        assertEquals("SUCCESS", response.getResponseCode());
//
//        DepositResponseDto actualResponse = (DepositResponseDto) response.getData();
//        assertEquals(accountNumber, actualResponse.getAccountNumber());
//        assertEquals(new BigDecimal("100"), actualResponse.getAmount());
//        assertEquals(time, actualResponse.getTransactionTime());
//
//        verify(accountStatusProducer).sendAccountStatusEvent(any());
//        verify(transactionEventProducer, times(2)).sendTransactionEvent(any());
//    }
//    
//    @Test
//    void testDepositMoney_unauthorizedConsumer_throwsException() {
//        // Given
//        String accountNumber = "ACC123";
//        String userId = "unauthorized";
//        String role = "CONSUMER";
//
//        Account account = new Account();
//        account.setAccountNumber(accountNumber);
//        account.setUserId(999L); // different user
//
//        TransactionDto transactionDto = new TransactionDto();
//        transactionDto.setAccountNumber(accountNumber);
//        transactionDto.setAmount(new BigDecimal("100"));
//
//        when(accountRepo.findByAccountNumber(accountNumber)).thenReturn(Optional.of(account));
//
//        // Then
//        assertThrows(UnauthorizedAccessException.class, () -> 
//            accountService.depositMoney(transactionDto, LocalDateTime.now(), role, userId)
//        );
//    }
//    
//    @Test
//    void testDepositMoney_accountNotFound_throwsException() {
//        // Given
//        when(accountRepo.findByAccountNumber("UNKNOWN_ACC")).thenReturn(Optional.empty());
//
//        TransactionDto dto = new TransactionDto();
//        dto.setAccountNumber("UNKNOWN_ACC");
//        dto.setAmount(new BigDecimal("200"));
//
//        // Then
//        assertThrows(ResourceNotFound.class, () ->
//            accountService.depositMoney(dto, LocalDateTime.now(), "CONSUMER", "1001")
//        );
//    }
//    
//    @Test
//    void testDepositMoney_saveFails_triggersFailedEvent() {
//        // Given
//        String accountNumber = "ACC500";
//        String userId = "1001";
//        String role = "CONSUMER";
//
//        Account account = new Account();
//        account.setAccountNumber(accountNumber);
//        account.setAvailableBalance(new BigDecimal("500"));
//        account.setAccountStatus(AccountStatus.ACTIVE);
//        account.setUserId(Long.parseLong(userId));
//
//        TransactionDto dto = new TransactionDto();
//        dto.setAccountNumber(accountNumber);
//        dto.setAmount(new BigDecimal("100"));
//
//        when(accountRepo.findByAccountNumber(accountNumber)).thenReturn(Optional.of(account));
//        doThrow(new DatabaseSaveException(dto)).when(accountRepo).save(any());
//
//        // Then
//        assertThrows(DatabaseSaveException.class, () ->
//            accountService.depositMoney(dto, LocalDateTime.now(), role, userId)
//        );
//
//        // One PENDING and one FAILED event should be triggered
//        verify(transactionEventProducer, times(2)).sendTransactionEvent(any());
//    }
//   
//    @Test
//    void testDepositMoney_officerCanAccessAnyUserAccount() {
//        String role = "OFFICER";
//        String userId = "9999"; // doesn't match
//        Account account = new Account();
//        account.setUserId(1001L);
//        account.setAccountNumber("ACC124");
//        account.setAvailableBalance(new BigDecimal("600"));
//        account.setAccountStatus(AccountStatus.ACTIVE);
//
//        when(accountRepo.findByAccountNumber("ACC124")).thenReturn(Optional.of(account));
//        when(accountRepo.save(any())).thenReturn(account);
//        doAnswer(invocation -> null).when(modelMapper).map(any(TransactionDto.class), any(DepositResponseDto.class));
//
//        TransactionDto dto = new TransactionDto();
//        dto.setAccountNumber("ACC124");
//        dto.setAmount(new BigDecimal("150"));
//
//        Response response = accountService.depositMoney(dto, LocalDateTime.now(), role, userId);
//        assertEquals("SUCCESS", response.getResponseCode());
//    }
//
//    @Test
//    void testDepositMoney_balanceHighButStatusNotPending_doesNotTriggerStatusEvent() {
//        // Given
//        String accountNumber = "ACC888";
//        String userId = "1001";
//        String role = "CONSUMER";
//        LocalDateTime time = LocalDateTime.now();
//
//        Account account = new Account();
//        account.setAccountNumber(accountNumber);
//        account.setAvailableBalance(new BigDecimal("1200")); // ✅ >= 1000
//        account.setAccountStatus(AccountStatus.ACTIVE);      // ❌ Not PENDING
//        account.setUserId(Long.parseLong(userId));
//
//        TransactionDto dto = new TransactionDto();
//        dto.setAccountNumber(accountNumber);
//        dto.setAmount(new BigDecimal("100"));
//
//        when(accountRepo.findByAccountNumber(accountNumber)).thenReturn(Optional.of(account));
//        when(accountRepo.save(any())).thenReturn(account);
//        doAnswer(invocation -> null).when(modelMapper).map(any(TransactionDto.class), any(DepositResponseDto.class));
//
//        // When
//        Response response = accountService.depositMoney(dto, time, role, userId);
//
//        // Then
//        assertEquals("SUCCESS", response.getResponseCode());
//
//        // ✅ Make sure account status event is not triggered
//        verify(accountStatusProducer, never()).sendAccountStatusEvent(any());
//    }
//
//    //---------------------deposit method test cases ends here---------------------------------------------
//
//    //--------------------withdraw method test cases starts here---------------------------------------------
//    @Test
//    void testWithdrawMoney_validConsumer_successfulWithdrawal() {
//        String accountNumber = "ACC200";
//        String userId = "1001";
//        String role = "CONSUMER";
//        LocalDateTime time = LocalDateTime.now();
//
//        Account account = new Account();
//        account.setAccountNumber(accountNumber);
//        account.setAvailableBalance(new BigDecimal("600"));
//        account.setAccountStatus(AccountStatus.ACTIVE);
//        account.setUserId(Long.parseLong(userId));
//
//        TransactionDto dto = new TransactionDto();
//        dto.setAccountNumber(accountNumber);
//        dto.setAmount(new BigDecimal("200"));
//
//        when(accountRepo.findByAccountNumber(accountNumber)).thenReturn(Optional.of(account));
//        when(accountRepo.save(any())).thenReturn(account);
//        doAnswer(invocation -> null).when(modelMapper).map(any(TransactionDto.class), any(WithdrawResponseDto.class));
//
//        Response response = accountService.withdrawMoney(dto, time, role, userId);
//
//        assertEquals("SUCCESS", response.getResponseCode());
//        verify(transactionEventProducer, times(2)).sendTransactionEvent(any());
//    }
//
//    @Test
//    void testWithdrawMoney_unauthorizedConsumer_throwsException() {
//        String accountNumber = "ACC201";
//        String userId = "1001";
//        String role = "CONSUMER";
//
//        Account account = new Account();
//        account.setAccountNumber(accountNumber);
//        account.setUserId(2002L);
//
//        TransactionDto dto = new TransactionDto();
//        dto.setAccountNumber(accountNumber);
//        dto.setAmount(new BigDecimal("100"));
//
//        when(accountRepo.findByAccountNumber(accountNumber)).thenReturn(Optional.of(account));
//
//        assertThrows(UnauthorizedAccessException.class, () ->
//            accountService.withdrawMoney(dto, LocalDateTime.now(), role, userId)
//        );
//    }
//    
//    @Test
//    void testWithdrawMoney_accountNotFound_throwsException() {
//        when(accountRepo.findByAccountNumber("UNKNOWN")).thenReturn(Optional.empty());
//
//        TransactionDto dto = new TransactionDto();
//        dto.setAccountNumber("UNKNOWN");
//        dto.setAmount(new BigDecimal("200"));
//
//        assertThrows(ResourceNotFound.class, () ->
//            accountService.withdrawMoney(dto, LocalDateTime.now(), "CONSUMER", "1001")
//        );
//    }
//
//    @Test
//    void testWithdrawMoney_insufficientBalanceOrInactive_throwsException() {
//        String accountNumber = "ACC202";
//        String userId = "1001";
//        String role = "CONSUMER";
//
//        Account account = new Account();
//        account.setAccountNumber(accountNumber);
//        account.setAvailableBalance(new BigDecimal("150"));
//        account.setAccountStatus(AccountStatus.PENDING); // Not ACTIVE
//        account.setUserId(Long.parseLong(userId));
//
//        TransactionDto dto = new TransactionDto();
//        dto.setAccountNumber(accountNumber);
//        dto.setAmount(new BigDecimal("100"));
//
//        when(accountRepo.findByAccountNumber(accountNumber)).thenReturn(Optional.of(account));
//
//        assertThrows(InsufficientBalanceException.class, () ->
//            accountService.withdrawMoney(dto, LocalDateTime.now(), role, userId)
//        );
//    }
//
//    @Test
//    void testWithdrawMoney_saveFails_triggersFailedEvent() {
//        String accountNumber = "ACC203";
//        String userId = "1001";
//        String role = "CONSUMER";
//
//        Account account = new Account();
//        account.setAccountNumber(accountNumber);
//        account.setAvailableBalance(new BigDecimal("500"));
//        account.setAccountStatus(AccountStatus.ACTIVE);
//        account.setUserId(Long.parseLong(userId));
//
//        TransactionDto dto = new TransactionDto();
//        dto.setAccountNumber(accountNumber);
//        dto.setAmount(new BigDecimal("200"));
//
//        when(accountRepo.findByAccountNumber(accountNumber)).thenReturn(Optional.of(account));
//        doThrow(new DatabaseSaveException(dto)).when(accountRepo).save(any());
//
//        assertThrows(DatabaseSaveException.class, () ->
//            accountService.withdrawMoney(dto, LocalDateTime.now(), role, userId)
//        );
//
//        // One PENDING and one FAILED event should be sent
//        verify(transactionEventProducer, times(2)).sendTransactionEvent(any());
//    }
//
//    @Test
//    void testWithdrawMoney_officerCanWithdrawFromAnyAccount() {
//        String accountNumber = "ACC204";
//        String userId = "9999";
//        String role = "OFFICER";
//
//        Account account = new Account();
//        account.setAccountNumber(accountNumber);
//        account.setAvailableBalance(new BigDecimal("800"));
//        account.setAccountStatus(AccountStatus.ACTIVE);
//        account.setUserId(1234L);
//
//        TransactionDto dto = new TransactionDto();
//        dto.setAccountNumber(accountNumber);
//        dto.setAmount(new BigDecimal("300"));
//
//        when(accountRepo.findByAccountNumber(accountNumber)).thenReturn(Optional.of(account));
//        when(accountRepo.save(any())).thenReturn(account);
//        doAnswer(invocation -> null).when(modelMapper).map(any(TransactionDto.class), any(WithdrawResponseDto.class));
//
//        Response response = accountService.withdrawMoney(dto, LocalDateTime.now(), role, userId);
//
//        assertEquals("SUCCESS", response.getResponseCode());
//        verify(transactionEventProducer, times(2)).sendTransactionEvent(any());
//    }
//    
//    @Test
//    void testWithdrawMoney_inactiveAccount_throwsException() {
//        String accountNumber = "ACC202";
//        String userId = "1001";
//        String role = "CONSUMER";
//        LocalDateTime time = LocalDateTime.now();
//
//        Account account = new Account();
//        account.setAccountNumber(accountNumber);
//        account.setAvailableBalance(new BigDecimal("1000"));
//        account.setAccountStatus(AccountStatus.CLOSED); // inactive account
//        account.setUserId(Long.parseLong(userId));
//
//        TransactionDto dto = new TransactionDto();
//        dto.setAccountNumber(accountNumber);
//        dto.setAmount(new BigDecimal("200")); // 1000 - 200 = 800 ≥ 100, but account inactive
//
//        when(accountRepo.findByAccountNumber(accountNumber)).thenReturn(Optional.of(account));
//
//        assertThrows(InsufficientBalanceException.class, () ->
//            accountService.withdrawMoney(dto, time, role, userId)
//        );
//
//        verify(transactionEventProducer, never()).sendTransactionEvent(any());
//    }
//    
//    //--------------------withdraw method test cases ends here-------------------------------------------
//    
//    //------------------------get userId method starts----------------------------------------------------
//
//    @Test
//    void testGetUserId_validAccountNumber_returnsUserId() {
//        String accountNumber = "ACC300";
//        Long expectedUserId = 1001L;
//
//        Account account = new Account();
//        account.setAccountNumber(accountNumber);
//        account.setUserId(expectedUserId);
//
//        when(accountRepo.findByAccountNumber(accountNumber)).thenReturn(Optional.of(account));
//
//        Long actualUserId = accountService.getUserId(accountNumber);
//
//        assertEquals(expectedUserId, actualUserId);
//    }
//    
//    @Test
//    void testGetUserId_invalidAccountNumber_throwsException() {
//        String accountNumber = "INVALID_ACC";
//
//        when(accountRepo.findByAccountNumber(accountNumber)).thenReturn(Optional.empty());
//
//        assertThrows(ResourceNotFound.class, () ->
//            accountService.getUserId(accountNumber)
//        );
//    }
//    //------------------------get userId method ends----------------------------------------------------
//    
//    //-------------------------fetch account number starts----------------------------------------------
//    
//    @Test
//    void testFetchAccountNumberByUserId_validUserId_returnsAccountNumber() {
//        Long userId = 1001L;
//        String expectedAccountNumber = "ACC400";
//
//        Account account = new Account();
//        account.setUserId(userId);
//        account.setAccountNumber(expectedAccountNumber);
//
//        when(accountRepo.findAccountNumberByUserId(userId)).thenReturn(Optional.of(account));
//
//        String actualAccountNumber = accountService.fetchAccountNumberByUserId(userId);
//
//        assertEquals(expectedAccountNumber, actualAccountNumber);
//    }
//    
//    @Test
//    void testFetchAccountNumberByUserId_invalidUserId_throwsException() {
//        Long userId = 9999L;
//
//        when(accountRepo.findAccountNumberByUserId(userId)).thenReturn(Optional.empty());
//
//        assertThrows(ResourceNotFound.class, () ->
//            accountService.fetchAccountNumberByUserId(userId)
//        );
//    }
//    
//    //-------------------------fetch account number ends----------------------------------------------
//    
//    //-------------------------check account number exist starts----------------------------------------------
//    @Test
//    void testCheckAccountNumberExist_whenAccountExists_returnsTrue() {
//        String accountNumber = "ACC123";
//        when(accountRepo.existsByAccountNumber(accountNumber)).thenReturn(true);
//
//        Boolean result = accountService.checkAccountNumberExist(accountNumber);
//
//        assertTrue(result);
//    }
//    
//    
//    @Test
//    void testCheckAccountNumberExist_whenAccountDoesNotExist_returnsFalse() {
//        String accountNumber = "ACC999";
//        when(accountRepo.existsByAccountNumber(accountNumber)).thenReturn(false);
//
//        Boolean result = accountService.checkAccountNumberExist(accountNumber);
//
//        assertFalse(result);
//    }
//
//  //-------------------------check account number exist ends----------------------------------------------
//
//}
