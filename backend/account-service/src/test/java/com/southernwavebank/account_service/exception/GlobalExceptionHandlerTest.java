package com.southernwavebank.account_service.exception;
//package com.bankofindia.account_service.exception;
//
//import static org.junit.jupiter.api.Assertions.assertEquals;
//
//import java.math.BigDecimal;
//
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.test.util.ReflectionTestUtils;
//
//import com.bankofindia.account_service.model.externaldto.TransactionDto;
//import com.bankofindia.account_service.model.externaldto.UserDto;
//import com.bankofindia.account_service.model.response.Response;
//
//public class GlobalExceptionHandlerTest {
//
//	private GlobalExceptionHandler exceptionHandler;
//
//    @BeforeEach
//    void setup() {
//        exceptionHandler = new GlobalExceptionHandler();
//        ReflectionTestUtils.setField(exceptionHandler, "not_found", "NOT_FOUND");
//        ReflectionTestUtils.setField(exceptionHandler, "unauthorized", "UNAUTHORIZED");
//        ReflectionTestUtils.setField(exceptionHandler, "conflict", "CONFLICT");
//        ReflectionTestUtils.setField(exceptionHandler, "internal_server_error", "INTERNAL_SERVER_ERROR");
//    }
//
//    @Test
//    void testHandleResourceConflict() {
//        UserDto userDto = new UserDto(); // Add fields if needed
//        ResourceConflict ex = new ResourceConflict(userDto);
//        ResponseEntity<Response> response = exceptionHandler.handleResourceConflict(ex);
//
//        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
//        assertEquals("CONFLICT", response.getBody().getResponseCode());
//        assertEquals("Account already exists for user: ", response.getBody().getMessage());
//        assertEquals(userDto, response.getBody().getData());
//    }
//
//
//    @Test
//    void testHandleResourceNotFound() {
//        String identifier = "123";
//        ResourceNotFound ex = new ResourceNotFound(identifier);
//        ResponseEntity<Response> response = exceptionHandler.handleResourceNotFound(ex);
//
//        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
//        assertEquals("NOT_FOUND", response.getBody().getResponseCode());
//        assertEquals("Resource not found for: 123", response.getBody().getMessage());
//        assertEquals("123", response.getBody().getData());
//    }
//
//    @Test
//    void testHandleDatabaseSaveException() {
//        TransactionDto transactionDto = new TransactionDto(); // You can set test data if needed
//        DatabaseSaveException ex = new DatabaseSaveException(transactionDto);
//        ResponseEntity<Response> response = exceptionHandler.handleDatabaseSaveException(ex);
//
//        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
//        assertEquals("INTERNAL_SERVER_ERROR", response.getBody().getResponseCode());
//        assertEquals("Failed to proceed the deposit", response.getBody().getMessage());
//        assertEquals(transactionDto, response.getBody().getData());
//    }
//
//    @Test
//    void testHandleInsufficientBalanceException() {
//        BigDecimal amount = new BigDecimal("1500.00");
//        InsufficientBalanceException ex = new InsufficientBalanceException(amount);
//        ResponseEntity<Response> response = exceptionHandler.handleInsufficientBalanceException(ex);
//
//        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
//        assertEquals("CONFLICT", response.getBody().getResponseCode());
//        assertEquals("Failed to process the withdraw amount, your available balance is low", response.getBody().getMessage());
//        assertEquals(amount, response.getBody().getData());
//    }
//
//    @Test
//    void testHandleUnauthorizedAccessException() {
//        String accountNumber = "ACC12345";
//        String role = "USER";
//        UnauthorizedAccessException ex = new UnauthorizedAccessException(accountNumber, role);
//        ResponseEntity<Response> response = exceptionHandler.handleUnauthorizedAccessException(ex);
//
//        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
//        assertEquals("UNAUTHORIZED", response.getBody().getResponseCode());
//        assertEquals("Access denied: You can only access your own account", response.getBody().getMessage());
//        assertEquals(accountNumber + " " + role, response.getBody().getData());
//    }
//
//
//    @Test
//    void testHandleGenericException() {
//        Exception ex = new Exception("Something went wrong");
//        ResponseEntity<Response> response = exceptionHandler.handleGenericException(ex);
//
//        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
//        assertEquals("INTERNAL_SERVER_ERROR", response.getBody().getResponseCode());
//        assertEquals("An unexpected error occurred", response.getBody().getMessage());
//        assertEquals(null, response.getBody().getData());
//    }
//
//}
