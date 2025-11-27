package com.southernwavebank.notification_service.exception;
//package com.bankofindia.notification_service.exception;
//
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.ExceptionHandler;
//import org.springframework.web.bind.annotation.RestControllerAdvice;
//
//import com.bankofindia.notification_service.model.response.ErrorResponse;
//
//import lombok.extern.slf4j.Slf4j;
//
//@RestControllerAdvice
//@Slf4j
//public class GlobalExceptionHandler {
//	
//	private ResponseEntity<ErrorResponse> buildErrorResponse(
//            HttpStatus status, String message, Object data, String error) {
//        return ResponseEntity.status(status).body(
//                ErrorResponse.builder()
//                        .message(message)
//                        .data(data)
//                        .error(error)
//                        .build()
//        );
//    }
//	
//	
//	@ExceptionHandler(NotificationFailedException.class)
//    public ResponseEntity<ErrorResponse> handleNotificationFailed(NotificationFailedException ex) {
//        log.warn("DuplicateFieldException caught: {}", ex.getCause());
//        return buildErrorResponse(HttpStatus.CONFLICT,
//                "Duplicate field error",
//                null,
//                ex.getMessage());
//    }
//
//}
