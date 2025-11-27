package com.southernwavebank.notification_service.exception;
//package com.bankofindia.notification_service.exception;
//
//import static org.junit.jupiter.api.Assertions.assertEquals;
//
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//
//import com.bankofindia.notification_service.model.response.ErrorResponse;
//
//public class GlobalExceptionHandlerTest {
//
//    private GlobalExceptionHandler exceptionHandler;
//
//    @BeforeEach
//    void setUp() {
//        exceptionHandler = new GlobalExceptionHandler();
//    }
//
//    @Test
//    void testHandleNotificationFailedException_returnsInternalServerErrorWithMessage() {
//        // Given
//        String errorMessage = "Simulated failure";
//        NotificationFailedException exception = new NotificationFailedException(errorMessage);
//
//        // When
//        ResponseEntity<ErrorResponse> response = exceptionHandler.handleNotificationFailed(exception);
//
//        // Then
//        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
//        assertEquals(errorMessage, response.getBody());
//    }
//}
