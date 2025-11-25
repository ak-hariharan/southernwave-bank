package com.southernwavebank.transaction_service.service;
//package com.bankofindia.transaction_service.service;
//
//import static org.junit.jupiter.api.Assertions.assertEquals;
//import static org.junit.jupiter.api.Assertions.assertNotNull;
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.ArgumentMatchers.anyString;
//import static org.mockito.ArgumentMatchers.eq;
//import static org.mockito.Mockito.doNothing;
//import static org.mockito.Mockito.never;
//import static org.mockito.Mockito.times;
//import static org.mockito.Mockito.verify;
//import static org.mockito.Mockito.when;
//
//import java.lang.reflect.Field;
//import java.math.BigDecimal;
//import java.time.LocalDateTime;
//import java.util.Collections;
//import java.util.List;
//
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//import org.modelmapper.ModelMapper;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.web.reactive.function.client.WebClient;
//
//import com.bankofindia.transaction_service.exception.ResourceNotFound;
//import com.bankofindia.transaction_service.exception.UnauthorizedAccessException;
//import com.bankofindia.transaction_service.external.ExternalAccountServiceClient;
//import com.bankofindia.transaction_service.kafka.NoTransactionHistoryEventProducer;
//import com.bankofindia.transaction_service.kafka.TransactionEventProducer;
//import com.bankofindia.transaction_service.kafka.TransactionHistoryEventProducer;
//import com.bankofindia.transaction_service.model.TransactionStatus;
//import com.bankofindia.transaction_service.model.dto.TransactionDto;
//import com.bankofindia.transaction_service.model.entity.Transaction;
//import com.bankofindia.transaction_service.model.externaldto.NotificationDto;
//import com.bankofindia.transaction_service.model.response.Response;
//import com.bankofindia.transaction_service.repository.TransactionRepository;
//import com.bankofindia.transaction_service.serviceimpl.TransactionServiceImpl;
//
//import reactor.core.publisher.Mono;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.Mockito.*;
//
//import java.lang.reflect.Field;
//
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.*;
//
//import org.modelmapper.ModelMapper;
//import org.modelmapper.TypeMap;
//import org.springframework.test.util.ReflectionTestUtils;
//import org.springframework.web.reactive.function.client.WebClient;
//
//@ExtendWith(MockitoExtension.class)
//class TransactionServiceImplTest {
//
//	@InjectMocks
//    private TransactionServiceImpl transactionService;
//
//    @Mock private TransactionRepository transactionRepository;
//    @Mock private ModelMapper modelMapper;
//    @Mock private TransactionEventProducer transactionEventProducer;
//    @Mock private TransactionHistoryEventProducer transHistoryEventProducer;
//    @Mock private NoTransactionHistoryEventProducer noTransactionHistoryEventProducer;
//    @Mock private ExternalAccountServiceClient externalAccountServiceClient;
//    @Mock private WebClient.Builder webClientBuilder;
//    
//    @Mock private WebClient webClient;
//    @Mock private WebClient.RequestHeadersUriSpec requestHeadersUriSpec;
//    @Mock private WebClient.RequestHeadersSpec<?> requestHeadersSpec;
//    @Mock private WebClient.ResponseSpec responseSpec;
//
//
//    @BeforeEach
//    void setUp() {
//        transactionService = new TransactionServiceImpl(webClientBuilder);
//        ReflectionTestUtils.setField(transactionService, "transactionRepository", transactionRepository);
//        ReflectionTestUtils.setField(transactionService, "modelMapper", modelMapper);
//        ReflectionTestUtils.setField(transactionService, "transactionEventProducer", transactionEventProducer);
//        ReflectionTestUtils.setField(transactionService, "success", "SUCCESS");
//        ReflectionTestUtils.setField(transactionService, "bad_request", "BAD_REQUEST");
//        ReflectionTestUtils.setField(transactionService, "internal_server_error", "INTERNAL_ERROR");
//        ReflectionTestUtils.setField(transactionService, "transHistoryEventProducer", transHistoryEventProducer);
//        ReflectionTestUtils.setField(transactionService, "noTransactionHistoryEventProducer", noTransactionHistoryEventProducer);
//        ReflectionTestUtils.setField(transactionService, "externalAccountServiceClient", externalAccountServiceClient);
//          
//     }
//    
//    //-----------------------depoistMoney()------------------------------
//
//    @Test
//    void depositMoney_successfulTransaction_shouldSaveAndSendNotification() {
//        // Given
//        TransactionDto dto = new TransactionDto();
//        dto.setStatus(TransactionStatus.SUCCESS);
//        dto.setAccountNumber("ACC001");
//        dto.setAmount(new BigDecimal("200"));
//
//        // Simulate modelMapper.map(TransactionDto -> Transaction)
//        doAnswer(invocation -> {
//            TransactionDto source = invocation.getArgument(0);
//            Transaction target = invocation.getArgument(1);
//            target.setAccountNumber(source.getAccountNumber());
//            target.setAmount(source.getAmount());
//            target.setStatus(source.getStatus());
//            return null;
//        }).when(modelMapper).map(eq(dto), any(Transaction.class));
//
//        // Simulate modelMapper.map(Transaction -> NotificationDto)
//        doAnswer(invocation -> {
//            Transaction source = invocation.getArgument(0);
//            NotificationDto target = invocation.getArgument(1);
//            target.setAccountNumber(source.getAccountNumber());
//            target.setAmount(source.getAmount());
//            return null;
//        }).when(modelMapper).map(any(Transaction.class), any(NotificationDto.class));
//
//        // Capture the saved Transaction
//        ArgumentCaptor<Transaction> transactionCaptor = ArgumentCaptor.forClass(Transaction.class);
//        when(transactionRepository.save(transactionCaptor.capture()))
//                .thenAnswer(invocation -> invocation.getArgument(0)); // return the same object
//
//        // When
//        Response response = transactionService.depositMoney(dto);
//
//        // Then
//        assertNotNull(response);
//        assertEquals("SUCCESS", response.getResponseCode());
//        assertEquals("Transaction completed successfully.", response.getMessage());
//
//        Transaction captured = transactionCaptor.getValue();
//        assertEquals("ACC001", captured.getAccountNumber());
//        assertEquals(new BigDecimal("200"), captured.getAmount());
//        assertEquals(TransactionStatus.SUCCESS, captured.getStatus());
//
//        Transaction returned = (Transaction) response.getData();
//        assertEquals("ACC001", returned.getAccountNumber());
//        assertEquals(new BigDecimal("200"), returned.getAmount());
//        assertEquals(TransactionStatus.SUCCESS, returned.getStatus());
//
//        verify(transactionEventProducer, times(1)).sendTransactionHappenedEvent(any(NotificationDto.class));
//    }
//    
//    @Test
//    void depositMoney_failedTransaction_shouldSaveOnly() {
//        TransactionDto dto = new TransactionDto();
//        dto.setStatus(TransactionStatus.FAILED);
//        dto.setAccountNumber("ACC001");
//        dto.setAmount(new BigDecimal("200"));
//
//        doAnswer(invocation -> {
//            TransactionDto source = invocation.getArgument(0);
//            Transaction target = invocation.getArgument(1);
//            target.setAccountNumber(source.getAccountNumber());
//            target.setAmount(source.getAmount());
//            target.setStatus(source.getStatus());
//            return null;
//        }).when(modelMapper).map(eq(dto), any(Transaction.class));
//
//        ArgumentCaptor<Transaction> transactionCaptor = ArgumentCaptor.forClass(Transaction.class);
//        when(transactionRepository.save(transactionCaptor.capture()))
//                .thenAnswer(invocation -> invocation.getArgument(0));
//
//        Response response = transactionService.depositMoney(dto);
//
//        assertNotNull(response);
//        assertEquals("SUCCESS", response.getResponseCode());
//        assertEquals("Transaction saved but not successful.", response.getMessage());
//
//        Transaction captured = transactionCaptor.getValue();
//        assertEquals("ACC001", captured.getAccountNumber());
//        assertEquals(new BigDecimal("200"), captured.getAmount());
//        assertEquals(TransactionStatus.FAILED, captured.getStatus());
//
//        verify(transactionEventProducer, never()).sendTransactionHappenedEvent(any(NotificationDto.class));
//    }
//    
//    @Test
//    void depositMoney_nullTransactionDto_shouldReturnBadRequest() {
//        TransactionDto dto = null;
//
//        Response response = transactionService.depositMoney(dto);
//
//        assertNotNull(response);
//        assertEquals("BAD_REQUEST", response.getResponseCode());
//        assertEquals("Invalid transaction data", response.getMessage());
//        verify(transactionRepository, never()).save(any(Transaction.class));
//        verify(transactionEventProducer, never()).sendTransactionHappenedEvent(any(NotificationDto.class));
//    }
//
//    @Test
//    void depositMoney_shouldReturnInternalError_onException() {
//        TransactionDto dto = new TransactionDto();
//        dto.setStatus(TransactionStatus.SUCCESS);
//
//        doThrow(new RuntimeException("DB error")).when(modelMapper).map(eq(dto), any(Transaction.class));
//
//        Response response = transactionService.depositMoney(dto);
//
//        assertEquals("INTERNAL_ERROR", response.getResponseCode());
//        assertEquals("Transaction failed due to internal error", response.getMessage());
//        assertNull(response.getData());
//    }
//    
//    //-----------------------withdrawMoney()------------------------------
//    
//    @Test
//    void withdrawMoney_successfulTransaction_shouldSaveAndSendNotification() {
//        // Given
//        TransactionDto dto = new TransactionDto();
//        dto.setStatus(TransactionStatus.SUCCESS);
//        dto.setAccountNumber("ACC001");
//        dto.setAmount(new BigDecimal("200"));
//
//        Transaction transaction = new Transaction();
//        transaction.setAccountNumber("ACC001");
//        transaction.setAmount(new BigDecimal("200"));
//        transaction.setStatus(TransactionStatus.SUCCESS);
//
//        // Simulate modelMapper.map(TransactionDto -> Transaction)
//        doAnswer(invocation -> {
//            TransactionDto source = invocation.getArgument(0);
//            Transaction target = invocation.getArgument(1);
//            target.setAccountNumber(source.getAccountNumber());
//            target.setAmount(source.getAmount());
//            target.setStatus(source.getStatus());
//            return null;
//        }).when(modelMapper).map(eq(dto), any(Transaction.class));
//
//        // Simulate modelMapper.map(Transaction -> NotificationDto)
//        doAnswer(invocation -> {
//            Transaction source = invocation.getArgument(0);
//            NotificationDto target = invocation.getArgument(1);
//            target.setAccountNumber(source.getAccountNumber());
//            target.setAmount(source.getAmount());
//            return null;
//        }).when(modelMapper).map(any(Transaction.class), any(NotificationDto.class));
//
//        // Capture the saved Transaction
//        ArgumentCaptor<Transaction> transactionCaptor = ArgumentCaptor.forClass(Transaction.class);
//        when(transactionRepository.save(transactionCaptor.capture()))
//                .thenAnswer(invocation -> invocation.getArgument(0)); // return the same object
//
//        // When
//        Response response = transactionService.withdrawMoney(dto);
//
//        // Then
//        assertNotNull(response);
//        assertEquals("SUCCESS", response.getResponseCode());
//        assertEquals("Transaction completed successfully.", response.getMessage());
//
//        Transaction captured = transactionCaptor.getValue();
//        assertEquals("ACC001", captured.getAccountNumber());
//        assertEquals(new BigDecimal("200"), captured.getAmount());
//        assertEquals(TransactionStatus.SUCCESS, captured.getStatus());
//
//        Transaction returned = (Transaction) response.getData();
//        assertEquals("ACC001", returned.getAccountNumber());
//        assertEquals(new BigDecimal("200"), returned.getAmount());
//        assertEquals(TransactionStatus.SUCCESS, returned.getStatus());
//
//        verify(transactionEventProducer, times(1)).sendTransactionHappenedEvent(any(NotificationDto.class));
//    }
//
//    @Test
//    void withdrawMoney_failedTransaction_shouldSaveOnly() {
//        // Given
//        TransactionDto dto = new TransactionDto();
//        dto.setStatus(TransactionStatus.FAILED);
//        dto.setAccountNumber("ACC001");
//        dto.setAmount(new BigDecimal("200"));
//
//        Transaction transaction = new Transaction();
//        transaction.setAccountNumber("ACC001");
//        transaction.setAmount(new BigDecimal("200"));
//        transaction.setStatus(TransactionStatus.FAILED);
//
//        // Simulate modelMapper.map(TransactionDto -> Transaction)
//        doAnswer(invocation -> {
//            TransactionDto source = invocation.getArgument(0);
//            Transaction target = invocation.getArgument(1);
//            target.setAccountNumber(source.getAccountNumber());
//            target.setAmount(source.getAmount());
//            target.setStatus(source.getStatus());
//            return null;
//        }).when(modelMapper).map(eq(dto), any(Transaction.class));
//
//        ArgumentCaptor<Transaction> transactionCaptor = ArgumentCaptor.forClass(Transaction.class);
//        when(transactionRepository.save(transactionCaptor.capture()))
//                .thenAnswer(invocation -> invocation.getArgument(0));
//
//        // When
//        Response response = transactionService.withdrawMoney(dto);
//
//        // Then
//        assertNotNull(response);
//        assertEquals("SUCCESS", response.getResponseCode());
//        assertEquals("Transaction saved but not successful.", response.getMessage());
//
//        Transaction captured = transactionCaptor.getValue();
//        assertEquals("ACC001", captured.getAccountNumber());
//        assertEquals(new BigDecimal("200"), captured.getAmount());
//        assertEquals(TransactionStatus.FAILED, captured.getStatus());
//
//        verify(transactionEventProducer, never()).sendTransactionHappenedEvent(any(NotificationDto.class));
//    }
//
//    @Test
//    void withdrawMoney_nullTransactionDto_shouldReturnBadRequest() {
//        // Given
//        TransactionDto dto = null;
//
//        // When
//        Response response = transactionService.withdrawMoney(dto);
//
//        // Then
//        assertNotNull(response);
//        assertEquals("BAD_REQUEST", response.getResponseCode());
//        assertEquals("Invalid transaction data", response.getMessage());
//        verify(transactionRepository, never()).save(any(Transaction.class));
//        verify(transactionEventProducer, never()).sendTransactionHappenedEvent(any(NotificationDto.class));
//    }
//
//    @Test
//    void withdrawMoney_shouldReturnInternalError_onException() {
//        // Given
//        TransactionDto dto = new TransactionDto();
//        dto.setStatus(TransactionStatus.SUCCESS);
//
//        // Simulate exception during the modelMapper mapping process
//        doThrow(new RuntimeException("DB error")).when(modelMapper).map(eq(dto), any(Transaction.class));
//
//        // When
//        Response response = transactionService.withdrawMoney(dto);
//
//        // Then
//        assertEquals("INTERNAL_ERROR", response.getResponseCode());
//        assertEquals("Transaction failed due to internal error", response.getMessage());
//        assertNull(response.getData());
//    }
//    
//    //--------------------------------fetchTransactionsOfAccount()-------------------------
//    
//    @Test
//    void fetchTransactionsOfAccount_asOfficer_shouldReturnTransactionHistory() {
//        String accountNumber = "ACC123";
//        String role = "OFFICER";
//        String userId = "user001";
//
//        Transaction firstTransaction = new Transaction();
//        firstTransaction.setAccountNumber(accountNumber);
//        firstTransaction.setAmount(BigDecimal.valueOf(1000));
//        firstTransaction.setStatus(TransactionStatus.SUCCESS);
//        
//        Transaction secondTransaction = new Transaction();
//        secondTransaction.setAccountNumber(accountNumber);
//        secondTransaction.setAmount(BigDecimal.valueOf(500));
//        secondTransaction.setStatus(TransactionStatus.FAILED);        
//
//        when(transactionRepository.existsByAccountNumber(accountNumber)).thenReturn(true);
//        when(transactionRepository.findByAccountNumber(accountNumber)).thenReturn(List.of(firstTransaction, secondTransaction));
//        when(modelMapper.map(any(Transaction.class), eq(TransactionDto.class))).thenAnswer(invocation -> {
//            Transaction txn = invocation.getArgument(0);
//            TransactionDto dto = new TransactionDto();
//            dto.setAccountNumber(txn.getAccountNumber());
//            dto.setAmount(txn.getAmount());
//            dto.setStatus(txn.getStatus());
//            return dto;
//        });
//
//        Response response = transactionService.fetchTransactionsOfAccount(accountNumber, role, userId);
//
//        assertEquals("SUCCESS", response.getResponseCode());
//        assertEquals("Transaction history retrieved successfully", response.getMessage());
//        List<TransactionDto> data = (List<TransactionDto>) response.getData();
//        assertEquals(2, data.size());
//        verify(transHistoryEventProducer, never()).sendTransactionHistoryEvent(any());
//    }
//
//    @Test
//    void fetchTransactionsOfAccount_asConsumer_validAccount_shouldReturnTransactionsAndSendEvent() {
//        String accountNumber = "ACC123";
//        String userId = "user001";
//        String role = "CONSUMER";
//
//        when(webClientBuilder.build()).thenReturn(webClient);
//        when(webClient.get()).thenReturn(requestHeadersUriSpec);
//        when(requestHeadersUriSpec.uri(anyString(), eq(userId))).thenReturn(requestHeadersSpec);
//        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
//        when(responseSpec.bodyToMono(String.class)).thenReturn(Mono.just(accountNumber));
//
//        when(transactionRepository.existsByAccountNumber(accountNumber)).thenReturn(true);
//        Transaction firstTransaction = new Transaction();
//        firstTransaction.setAccountNumber(accountNumber);
//        firstTransaction.setAmount(BigDecimal.valueOf(1000));
//        firstTransaction.setStatus(TransactionStatus.SUCCESS);
//        when(transactionRepository.findByAccountNumber(accountNumber)).thenReturn(List.of(firstTransaction));
//
//        when(modelMapper.map(any(Transaction.class), eq(TransactionDto.class))).thenAnswer(invocation -> {
//            Transaction txnSrc = invocation.getArgument(0);
//            TransactionDto dto = new TransactionDto();
//            dto.setAccountNumber(txnSrc.getAccountNumber());
//            dto.setAmount(txnSrc.getAmount());
//            dto.setStatus(txnSrc.getStatus());
//            return dto;
//        });
//
//        Response response = transactionService.fetchTransactionsOfAccount(accountNumber, role, userId);
//
//        assertEquals("SUCCESS", response.getResponseCode());
//        assertEquals("Transaction history retrieved successfully", response.getMessage());
//        verify(transHistoryEventProducer, times(1)).sendTransactionHistoryEvent(any());
//    }
//    
//    @Test
//    void fetchTransactionsOfAccount_noTransactionsButValidAccount_shouldSendEmptyResponse() {
//        String accountNumber = "ACC999";
//        String role = "CONSUMER";
//        String userId = "user002";
//
//        when(webClientBuilder.build()).thenReturn(webClient);
//        when(webClient.get()).thenReturn(requestHeadersUriSpec);
//        when(requestHeadersUriSpec.uri(anyString(), eq(userId))).thenReturn(requestHeadersSpec);
//        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
//        when(responseSpec.bodyToMono(String.class)).thenReturn(Mono.just(accountNumber));
//
//        when(transactionRepository.existsByAccountNumber(accountNumber)).thenReturn(false);
//        when(externalAccountServiceClient.getAccountNumber(accountNumber)).thenReturn(true);
//
//        Response response = transactionService.fetchTransactionsOfAccount(accountNumber, role, userId);
//
//        assertEquals("SUCCESS", response.getResponseCode());
//        assertEquals("No transactions found for this account", response.getMessage());
//        verify(noTransactionHistoryEventProducer, times(1)).sendNoTransactionHappenedEvent(any());
//    }
//
//    @Test
//    void fetchTransactionsOfAccount_consumerAccessingAnotherAccount_shouldThrowUnauthorized() {
//        String userId = "user123";
//        String accountNumber = "ACC456";
//        String actualAccountNumber = "ACC458";
//        String role = "CONSUMER";
//
//        when(webClientBuilder.build()).thenReturn(webClient);
//        when(webClient.get()).thenReturn(requestHeadersUriSpec);
//        when(requestHeadersUriSpec.uri(anyString(), eq(userId))).thenReturn(requestHeadersSpec);
//        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
//        when(responseSpec.bodyToMono(String.class)).thenReturn(Mono.just(actualAccountNumber));
//
//        assertThrows(UnauthorizedAccessException.class, () ->
//                transactionService.fetchTransactionsOfAccount(accountNumber, role, userId));
//
//        verify(transactionRepository, never()).findByAccountNumber(any());
//        verify(transHistoryEventProducer, never()).sendTransactionHistoryEvent(any());
//    }
//    
//    @Test
//    void fetchTransactionsOfAccount_accountNotFoundAnywhere_shouldThrowResourceNotFound() {
//        String accountNumber = "UNKNOWN_ACC";
//        String role = "OFFICER";
//        String userId = "user007";
//
//        when(transactionRepository.existsByAccountNumber(accountNumber)).thenReturn(false);
//        when(externalAccountServiceClient.getAccountNumber(accountNumber)).thenReturn(false);
//
//        assertThrows(ResourceNotFound.class, () ->
//                transactionService.fetchTransactionsOfAccount(accountNumber, role, userId));
//
//        verify(noTransactionHistoryEventProducer, never()).sendNoTransactionHappenedEvent(any());
//    }
//    
//    @Test
//    void fetchTransactionsOfAccount_noTransactionsFound_shouldSendNoTransactionEvent() {
//        // Given
//        String accountNumber = "ACC001";
//        String role = "CONSUMER";
//        String userId = "user1";
//
//        // Simulate that the transaction repository returns an empty list
//        when(transactionRepository.existsByAccountNumber(accountNumber)).thenReturn(true);
//        when(transactionRepository.findByAccountNumber(accountNumber)).thenReturn(Collections.emptyList());
//
//        // Mock external service response
////        when(externalAccountServiceClient.getAccountNumber(accountNumber)).thenReturn(true);
//
//        // Mocking the NotificationDto creation and sending the event
//        doNothing().when(noTransactionHistoryEventProducer).sendNoTransactionHappenedEvent(any(NotificationDto.class));
//
//        when(webClientBuilder.build()).thenReturn(webClient);
//        when(webClient.get()).thenReturn(requestHeadersUriSpec);
//        when(requestHeadersUriSpec.uri(anyString(), eq(userId))).thenReturn(requestHeadersSpec);
//        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
//        when(responseSpec.bodyToMono(String.class)).thenReturn(Mono.just(accountNumber));
//        // When
//        Response response = transactionService.fetchTransactionsOfAccount(accountNumber, role, userId);
//
//        // Then
//        assertEquals("SUCCESS", response.getResponseCode());
//        assertEquals("No transactions found for the given date range", response.getMessage());
//        assertTrue(((List<?>) response.getData()).isEmpty());
//
//        // Verify the event was sent
//        verify(noTransactionHistoryEventProducer, times(1)).sendNoTransactionHappenedEvent(any(NotificationDto.class));
//    }
//
//    //-----------------------fetchTransactionsOfAccount() between dates--------------------------
//    
//    @Test
//    void fetchTransactionsOfAccount_asOfficer_shouldReturnTransactionHistoryWithDate() {
//        String accountNumber = "ACC123";
//        String role = "OFFICER";
//        String userId = "user001";
//        
//        LocalDateTime now = LocalDateTime.now();
//        LocalDateTime earlier = now.minusDays(7);
//
//        Transaction firstTransaction = new Transaction();
//        firstTransaction.setAccountNumber(accountNumber);
//        firstTransaction.setAmount(BigDecimal.valueOf(1000));
//        firstTransaction.setStatus(TransactionStatus.SUCCESS);
//
//        Transaction secondTransaction = new Transaction();
//        secondTransaction.setAccountNumber(accountNumber);
//        secondTransaction.setAmount(BigDecimal.valueOf(500));
//        secondTransaction.setStatus(TransactionStatus.FAILED);
//
//        when(transactionRepository.existsByAccountNumber(accountNumber)).thenReturn(true);
//        when(transactionRepository.findByAccountNumberAndTransactionTimeBetween(accountNumber, earlier, now))
//            .thenReturn(List.of(firstTransaction, secondTransaction));
//
//        when(modelMapper.map(any(Transaction.class), eq(TransactionDto.class))).thenAnswer(invocation -> {
//            Transaction txn = invocation.getArgument(0);
//            TransactionDto dto = new TransactionDto();
//            dto.setAccountNumber(txn.getAccountNumber());
//            dto.setAmount(txn.getAmount());
//            dto.setStatus(txn.getStatus());
//            return dto;
//        });
//
//        Response response = transactionService.fetchTransactionsOfAccount(accountNumber, earlier, now, role, userId);
//
//        assertEquals("SUCCESS", response.getResponseCode());
//        assertEquals("Transactions history retrieved successfully", response.getMessage());
//        assertEquals(2, ((List<?>) response.getData()).size());
//        verify(transHistoryEventProducer, never()).sendTransactionHistoryEvent(any());
//    }
//    
//    @Test
//    void fetchTransactionsOfAccount_asConsumer_validAccount_shouldReturnTransactionsAndSendEventWithDate() {
//        String accountNumber = "ACC123";
//        String userId = "user001";
//        String role = "CONSUMER";
//        
//        LocalDateTime now = LocalDateTime.now();
//        LocalDateTime earlier = now.minusDays(7);
//
//        Transaction transaction = new Transaction();
//        transaction.setAccountNumber(accountNumber);
//        transaction.setAmount(BigDecimal.valueOf(1000));
//        transaction.setStatus(TransactionStatus.SUCCESS);
//
//        when(webClientBuilder.build()).thenReturn(webClient);
//        when(webClient.get()).thenReturn(requestHeadersUriSpec);
//        when(requestHeadersUriSpec.uri(anyString(), eq(userId))).thenReturn(requestHeadersSpec);
//        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
//        when(responseSpec.bodyToMono(String.class)).thenReturn(Mono.just(accountNumber));
//
//        when(transactionRepository.existsByAccountNumber(accountNumber)).thenReturn(true);
//        when(transactionRepository.findByAccountNumberAndTransactionTimeBetween(accountNumber, earlier, now))
//            .thenReturn(List.of(transaction));
//
//        when(modelMapper.map(any(Transaction.class), eq(TransactionDto.class))).thenAnswer(invocation -> {
//            Transaction txn = invocation.getArgument(0);
//            TransactionDto dto = new TransactionDto();
//            dto.setAccountNumber(txn.getAccountNumber());
//            dto.setAmount(txn.getAmount());
//            dto.setStatus(txn.getStatus());
//            return dto;
//        });
//
//        Response response = transactionService.fetchTransactionsOfAccount(accountNumber, earlier, now, role, userId);
//
//        assertEquals("SUCCESS", response.getResponseCode());
//        assertEquals("Transactions history retrieved successfully", response.getMessage());
//        verify(transHistoryEventProducer, times(1)).sendTransactionHistoryEvent(any());
//    }
//    
//    @Test
//    void fetchTransactionsOfAccount_consumerAccessingAnotherAccount_shouldThrowUnauthorizedWithDate() {
//        String userId = "user123";
//        String accountNumber = "ACC456";
//        String actualAccountNumber = "ACC789";
//        String role = "CONSUMER";
//        
//        LocalDateTime now = LocalDateTime.now();
//        LocalDateTime earlier = now.minusDays(7);
//
//        when(webClientBuilder.build()).thenReturn(webClient);
//        when(webClient.get()).thenReturn(requestHeadersUriSpec);
//        when(requestHeadersUriSpec.uri(anyString(), eq(userId))).thenReturn(requestHeadersSpec);
//        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
//        when(responseSpec.bodyToMono(String.class)).thenReturn(Mono.just(actualAccountNumber));
//
//        assertThrows(UnauthorizedAccessException.class, () ->
//                transactionService.fetchTransactionsOfAccount(accountNumber, earlier, now, role, userId));
//
//        verify(transactionRepository, never()).findByAccountNumber(any());
//        verify(transHistoryEventProducer, never()).sendTransactionHistoryEvent(any());
//    }
//    
//    @Test
//    void fetchTransactionsOfAccount_noTransactionsButValidAccount_shouldSendEmptyResponseWithDate() {
//        String accountNumber = "ACC999";
//        String role = "CONSUMER";
//        String userId = "user002";
//        
//        LocalDateTime now = LocalDateTime.now();
//        LocalDateTime earlier = now.minusDays(7);
//
//        when(webClientBuilder.build()).thenReturn(webClient);
//        when(webClient.get()).thenReturn(requestHeadersUriSpec);
//        when(requestHeadersUriSpec.uri(anyString(), eq(userId))).thenReturn(requestHeadersSpec);
//        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
//        when(responseSpec.bodyToMono(String.class)).thenReturn(Mono.just(accountNumber));
//
//        when(transactionRepository.existsByAccountNumber(accountNumber)).thenReturn(false);
//        when(externalAccountServiceClient.getAccountNumber(accountNumber)).thenReturn(true);
//
//        Response response = transactionService.fetchTransactionsOfAccount(accountNumber, earlier, now, role, userId);
//
//        assertEquals("SUCCESS", response.getResponseCode());
//        assertEquals("No transactions found for this account", response.getMessage());
//        assertTrue(((List<?>) response.getData()).isEmpty());
//        verify(noTransactionHistoryEventProducer, times(1)).sendNoTransactionHappenedEvent(any());
//    }
//    
//    @Test
//    void fetchTransactionsOfAccount_accountNotFoundAnywhere_shouldThrowResourceNotFoundWithDate() {
//        String accountNumber = "UNKNOWN_ACC";
//        String role = "OFFICER";
//        String userId = "user007";
//        
//        LocalDateTime now = LocalDateTime.now();
//        LocalDateTime earlier = now.minusDays(7);
//
//        when(transactionRepository.existsByAccountNumber(accountNumber)).thenReturn(false);
//        when(externalAccountServiceClient.getAccountNumber(accountNumber)).thenReturn(false);
//
//        assertThrows(ResourceNotFound.class, () ->
//                transactionService.fetchTransactionsOfAccount(accountNumber, earlier, now, role, userId));
//
//        verify(noTransactionHistoryEventProducer, never()).sendNoTransactionHappenedEvent(any());
//    }
//
//    @Test
//    void fetchTransactionsOfAccount_noTransactionsFound_shouldSendNoTransactionEventWithDate() {
//        String accountNumber = "ACC001";
//        String role = "CONSUMER";
//        String userId = "user1";
//        
//        LocalDateTime now = LocalDateTime.now();
//        LocalDateTime earlier = now.minusDays(7);
//
//        when(transactionRepository.existsByAccountNumber(accountNumber)).thenReturn(true);
//        when(transactionRepository.findByAccountNumberAndTransactionTimeBetween(accountNumber, earlier, now))
//            .thenReturn(Collections.emptyList());
//
//        when(webClientBuilder.build()).thenReturn(webClient);
//        when(webClient.get()).thenReturn(requestHeadersUriSpec);
//        when(requestHeadersUriSpec.uri(anyString(), eq(userId))).thenReturn(requestHeadersSpec);
//        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
//        when(responseSpec.bodyToMono(String.class)).thenReturn(Mono.just(accountNumber));
//
//        doNothing().when(noTransactionHistoryEventProducer).sendNoTransactionHappenedEvent(any(NotificationDto.class));
//
//        Response response = transactionService.fetchTransactionsOfAccount(accountNumber, earlier, now, role, userId);
//
//        assertEquals("SUCCESS", response.getResponseCode());
//        assertEquals("No transactions found for the given date range", response.getMessage());
//        assertTrue(((List<?>) response.getData()).isEmpty());
//        verify(noTransactionHistoryEventProducer, times(1)).sendNoTransactionHappenedEvent(any(NotificationDto.class));
//    }
//}
