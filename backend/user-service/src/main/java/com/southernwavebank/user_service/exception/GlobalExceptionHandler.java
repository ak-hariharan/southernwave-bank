package com.southernwavebank.user_service.exception;

import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;

import com.southernwavebank.user_service.model.response.ErrorResponse;

import lombok.extern.slf4j.Slf4j;


@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    private ResponseEntity<ErrorResponse> buildErrorResponse(
            HttpStatus status, String message, Object data, String error) {
        return ResponseEntity.status(status).body(
                ErrorResponse.builder()
                        .message(message)
                        .data(data)
                        .error(error)
                        .build()
        );
    }
    
    @ExceptionHandler(ResourceConflict.class)
    public ResponseEntity<ErrorResponse> handleResourceConflict(ResourceConflict ex) {
        log.warn("ResourceConflict caught: {}", ex.getUserDto());
        return buildErrorResponse(HttpStatus.CONFLICT,
                "Provide valid data",
                ex.getUserDto(),
                ex.getMessage());
    }

    @ExceptionHandler(ResourceNotFound.class)
    public ResponseEntity<ErrorResponse> handleResourceNotFound(ResourceNotFound ex) {
        log.warn("ResourceNotFound caught: {}", ex.getIdentifier());
        return buildErrorResponse(HttpStatus.NOT_FOUND,
                "Provide valid data",
                ex.getIdentifier(),
                ex.getMessage());
    }

    @ExceptionHandler(DuplicateFieldException.class)
    public ResponseEntity<ErrorResponse> handleDuplicateField(DuplicateFieldException ex) {
        log.warn("DuplicateFieldException caught: {}", ex.getIdentifier());
        return buildErrorResponse(HttpStatus.CONFLICT,
                "Duplicate field error",
                ex.getIdentifier(),
                ex.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationErrors(MethodArgumentNotValidException ex) {
        log.warn("Validation error: {}", ex.getMessage());

        String errorMessage = ex.getBindingResult().getFieldErrors().stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .findFirst()
                .orElse("Validation error");

        return buildErrorResponse(HttpStatus.BAD_REQUEST,
                "Value required",
                null,
                errorMessage);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgument(IllegalArgumentException ex) {
        log.warn("IllegalArgumentException caught: {}", ex.getMessage());

        String invalidValue = null;
        String errorMessage = ex.getMessage();
        if (ex.getMessage() != null && ex.getMessage().contains("|")) {
            String[] parts = ex.getMessage().split("\\|", 2);
            invalidValue = parts[0];
            errorMessage = parts[1];
        }

        return buildErrorResponse(HttpStatus.BAD_REQUEST,
                "Give proper valid values",
                invalidValue,
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

        return buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR,
        				"Something went wrong on our side. Please try again later.",
                         null,
                         "Internal server error"); // careful: you may want a generic message here instead of ex.getMessage()
    }

}
