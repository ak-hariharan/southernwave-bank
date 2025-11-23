package com.southernwavebank.auth_service.exception;

import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;

import com.southernwavebank.auth_service.reponse.ErrorResponse;

import lombok.extern.slf4j.Slf4j;


@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    private ResponseEntity<ErrorResponse> buildErrorResponse(HttpStatus status, String message, String data, String error) {
        return ResponseEntity.status(status)
                .body(ErrorResponse.builder()
                        .message(message)
                        .data(data)
                        .error(error)
                        .build());
    }

    @ExceptionHandler(UnAuthorizedUserException.class)
    public ResponseEntity<ErrorResponse> handleUnAuthorizedUserException(UnAuthorizedUserException ex) {
        log.warn("Unauthorized access attempt");
        return buildErrorResponse(HttpStatus.UNAUTHORIZED,
                ex.getMessage(),
                null,
                "Unauthorized");
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationErrors(MethodArgumentNotValidException ex) {
        log.warn("MethodArgumentNotValidException caught");

        String errorMessage = ex.getBindingResult().getFieldErrors().stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .findFirst()
                .orElse("Validation error");

        return buildErrorResponse(HttpStatus.BAD_REQUEST,
                "Value required",
                null,
                errorMessage);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleHttpMessageNotReadable(HttpMessageNotReadableException ex) {
        log.warn("HttpMessageNotReadableException caught");
        return buildErrorResponse(HttpStatus.BAD_REQUEST,
                "Request body is missing or malformed",
                "No data given",
                "Empty JSON");
    }
    
    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<ErrorResponse> handleNoHandlerFound(NoHandlerFoundException ex) {
        log.warn("NoHandlerFoundException caught: {}", ex.getRequestURL());
        return buildErrorResponse(HttpStatus.NOT_FOUND,
                "Endpoint not found",
                null,
                ex.getRequestURL());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception ex) {
        log.error("Unhandled exception caught", ex);
        return buildErrorResponse(
        	    HttpStatus.INTERNAL_SERVER_ERROR,
        	    (ex.getMessage() != null && !ex.getMessage().isEmpty()) 
        	        ? ex.getMessage() 
        	        : "Something went wrong on our side. Please try again later.",
        	    null,
        	    "Internal server error"
        	);

    }
}
