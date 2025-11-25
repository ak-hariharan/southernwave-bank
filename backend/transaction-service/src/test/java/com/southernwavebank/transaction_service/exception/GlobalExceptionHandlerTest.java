package com.southernwavebank.transaction_service.exception;
//package com.bankofindia.transaction_service.exception;
//
//import static org.junit.jupiter.api.Assertions.assertEquals;
//import static org.junit.jupiter.api.Assertions.assertNotNull;
//import static org.junit.jupiter.api.Assertions.assertNull;
//
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.test.util.ReflectionTestUtils;
//
//import com.bankofindia.transaction_service.model.response.Response;
//
//public class GlobalExceptionHandlerTest {
//
//    private GlobalExceptionHandler exceptionHandler;
//
//    @BeforeEach
//    void setup() {
//        exceptionHandler = new GlobalExceptionHandler();
//        ReflectionTestUtils.setField(exceptionHandler, "conflict", "409");
//    }
//
//    @Test
//    void handleResourceNotFound_shouldReturnConflictResponse() {
//        // Given
//        String identifier = "ACC12345";
//        ResourceNotFound exception = new ResourceNotFound(identifier);
//
//        // When
//        ResponseEntity<Response> responseEntity = exceptionHandler.handleResourceNotFound(exception);
//
//        // Then
//        assertEquals(HttpStatus.CONFLICT, responseEntity.getStatusCode());
//        Response response = responseEntity.getBody();
//        assertNotNull(response);
//        assertEquals("409", response.getResponseCode());
//        assertEquals("Account not found for account number", response.getMessage());
//        assertEquals(identifier, response.getData()); 
//    }
//
//    @Test
//    void handleUnauthorizedAccess_shouldReturnConflictResponse() {
//        // Given
//        String identifier = "user002@bank.com";
//        UnauthorizedAccessException exception = new UnauthorizedAccessException(identifier);
//
//        // When
//        ResponseEntity<Response> responseEntity = exceptionHandler.handleUnauthorizedAccess(exception);
//
//        // Then
//        assertEquals(HttpStatus.CONFLICT, responseEntity.getStatusCode());
//        Response response = responseEntity.getBody();
//        assertNotNull(response);
//        assertEquals("409", response.getResponseCode());
//        assertEquals("Access denied: You can only access your own account.", response.getMessage());
//        assertEquals(identifier, response.getData()); // identifier is set correctly
//    }
//}
