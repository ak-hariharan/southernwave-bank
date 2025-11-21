package com.southernwavebank.user_service.exception;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import com.southernwavebank.user_service.exception.GlobalExceptionHandler;
import com.southernwavebank.user_service.exception.ResourceNotFound;
import com.southernwavebank.user_service.model.response.Response;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

//@ExtendWith(SpringExtension.class)  // Ensure Spring Extension is used
//@TestPropertySource(properties = {"spring.application.conflict=CONFLICT"})
//class GlobalExceptionHandlerTest {
//
//    @InjectMocks
//    private GlobalExceptionHandler globalExceptionHandler;  // Injecting the exception handler
//
//    @Mock
//    private Response mockResponse;  // Mocking Response class if needed
//
//    @Value("${spring.application.conflict:CONFLICT}")
//    private String conflict;
//
//    @BeforeEach
//    void setUp() {
//        // Initialize any necessary objects or mocks before each test
//    }
//
//    @Test
//    void testHandleResourceNotFound() {
//        String emailId = "notfound@bank.com";
//        String errorMessage = "User not found for: " + emailId;
//        ResourceNotFound exception = new ResourceNotFound(emailId);
//
//        // Call the exception handler method
//        ResponseEntity<Response> responseEntity = globalExceptionHandler.handleResourceNotFound(exception);
//
//        // Verify the response
//        assertEquals(HttpStatus.CONFLICT, responseEntity.getStatusCode());
//        assertEquals(conflict, responseEntity.getBody().getResponseCode());
//        assertEquals(errorMessage, responseEntity.getBody().getMessage());
//        assertEquals(emailId, responseEntity.getBody().getData());
//    }
//
//    @Test
//    void testHandleDuplicateEmailException() {
//        String emailId = "duplicate@bank.com";
//        String errorMessage = "Email already in use: " + emailId;
//        DuplicateEmailException exception = new DuplicateEmailException(emailId);
//
//        // Call the exception handler method
//        ResponseEntity<Response> responseEntity = globalExceptionHandler.handleDuplicateEmailException(exception);
//
//        // Verify the response
//        assertEquals(HttpStatus.CONFLICT, responseEntity.getStatusCode());
//        assertEquals(conflict, responseEntity.getBody().getResponseCode());
//        assertEquals(errorMessage, responseEntity.getBody().getMessage());
//        assertEquals(emailId, responseEntity.getBody().getData());
//    }

//    @Test
//    void testHandleResourceNotFound_ConflictCode() {
//        String emailId = "notfound@bank.com";
//        ResourceNotFound exception = new ResourceNotFound(emailId);
//
//        // Call the exception handler method
//        ResponseEntity<Response> responseEntity = globalExceptionHandler.handleResourceNotFound(exception);
//
//        // Verify the response
//        assertEquals(HttpStatus.CONFLICT, responseEntity.getStatusCode());
//        assertEquals(conflict, responseEntity.getBody().getResponseCode());
//        assertTrue(responseEntity.getBody().getMessage().contains("User not found for"));
//    }
//}

// working---------------------------------------------------------------
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import static org.junit.jupiter.api.Assertions.assertEquals;
//
//public class GlobalExceptionHandlerTest {
//
//  private GlobalExceptionHandler exceptionHandler;
//
//  @BeforeEach
//  void setup() {
//      exceptionHandler = new GlobalExceptionHandler();
//      ReflectionTestUtils.setField(exceptionHandler, "conflict", "409");
//      ReflectionTestUtils.setField(exceptionHandler, "bad_request", "400");
//      
//   }
//
//  @Test
//  void testHandleResourceNotFound() {
//    String emailId = "notfound@bank.com";
//    String errorMessage = "User not found for: " + emailId;
//    String responseCode = "409";
//    ResourceNotFound exception = new ResourceNotFound(emailId);
//
//    // Call the exception handler method
//    ResponseEntity<Response> responseEntity = exceptionHandler.handleResourceNotFound(exception);
//
//    // Verify the response
//    assertEquals(HttpStatus.CONFLICT, responseEntity.getStatusCode());
//    assertEquals("409", responseEntity.getBody().getResponseCode());  // Check the correct response code
//    assertEquals(errorMessage, responseEntity.getBody().getMessage());
//    assertEquals(emailId, responseEntity.getBody().getData());
//  }
  
  
//  @Test
//  void testHandleDuplicateEmailException() {
//      String emailId = "duplicate@bank.com";
//      String errorMessage = "Email is already in use by another user: " + emailId;
//      DuplicateEmailException exception = new DuplicateEmailException(emailId);
//
//      // Mocking the behavior of the exception handler
//      ResponseEntity<Response> responseEntity = exceptionHandler.handleDuplicateEmailException(exception);
//
//      assertEquals(HttpStatus.CONFLICT, responseEntity.getStatusCode());
//      assertEquals("409", responseEntity.getBody().getResponseCode());
//      assertEquals(errorMessage, responseEntity.getBody().getMessage());
//      assertEquals(emailId, responseEntity.getBody().getData());
//  }
//  
//  @Test
//  void testHandleValidationErrors() {
//      // Arrange
//      BindingResult bindingResult = mock(BindingResult.class);
//      FieldError fieldError1 = new FieldError("objectName", "emailId", "Email is required");
//      FieldError fieldError2 = new FieldError("objectName", "password", "Password must be at least 6 characters");
//
//      List<FieldError> fieldErrors = Arrays.asList(fieldError1, fieldError2);
//      when(bindingResult.getFieldErrors()).thenReturn(fieldErrors);
//
//      MethodArgumentNotValidException ex = new MethodArgumentNotValidException(null, bindingResult);
//
//      // Act
//      ResponseEntity<Response> responseEntity = exceptionHandler.handleValidationErrors(ex);
//
//      // Assert
//      assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
//      Response responseBody = responseEntity.getBody();
//      assertNotNull(responseBody);
//      assertEquals("400", responseEntity.getBody().getResponseCode());
//      assertEquals("Value required", responseBody.getMessage());
//
//      Map<String, String> errors = (Map<String, String>) responseBody.getData();
//      assertEquals(2, errors.size());
//      assertEquals("Email is required", errors.get("emailId"));
//      assertEquals("Password must be at least 6 characters", errors.get("password"));
//  }

//  @Test
//  void testHandleIllegalArgumentException() {
//      // Arrange
//      String message = "Invalid input parameter";
//      IllegalArgumentException exception = new IllegalArgumentException(message);
//
//      // Act
//      ResponseEntity<Response> responseEntity = exceptionHandler.handleIllegalArgumentException(exception);
//
//      // Assert
//      assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
//
//      Response response = responseEntity.getBody();
//      assertNotNull(response);
//      assertEquals("400", responseEntity.getBody().getResponseCode()); // Assuming bad_request = "400"
//      assertEquals("Give proper valid values", response.getMessage());
//
//      Map<String, String> data = (Map<String, String>) response.getData();
//      assertNotNull(data);
//      assertEquals(1, data.size());
//      assertEquals(message, data.get("error"));
//  }

//  @Test
//  void testHandleHttpMessageNotReadable() {
//      // Arrange
//      String errorMessage = "Malformed JSON request";
//      Throwable cause = new RuntimeException("Invalid format");
//
//      HttpInputMessage inputMessage = mock(HttpInputMessage.class); // Required for the new constructor
//      HttpMessageNotReadableException exception =
//              new HttpMessageNotReadableException(errorMessage, cause, inputMessage);
//
//      // Act
//      ResponseEntity<Response> responseEntity = exceptionHandler.handleHttpMessageNotReadable(exception);
//
//      // Assert
//      assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
//
//      Response response = responseEntity.getBody();
//      assertNotNull(response);
//      assertEquals("409", response.getResponseCode());  // Assuming 'conflict' = "409"
//      assertEquals("Request body is missing or malformed", response.getMessage());
//      assertEquals("No data given", response.getData());
//  }
//
//
//  
//}

