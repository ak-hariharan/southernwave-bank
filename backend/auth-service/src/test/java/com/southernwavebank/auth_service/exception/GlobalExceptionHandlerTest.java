package com.southernwavebank.auth_service.exception;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.southernwavebank.auth_service.reponse.Response;

import static org.junit.jupiter.api.Assertions.*;
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
//    void testHandleUnAuthorizedUserException_returnsConflictWithMessage() {
//        String errorMessage = "User is not authorized";
//        UnAuthorizedUserException exception = new UnAuthorizedUserException(errorMessage);
//
//        ResponseEntity<Response> response = exceptionHandler.handleUnAuthorizedUserException(exception);
//
//        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
//        assertEquals(errorMessage, response.getBody());
//    }
//}
