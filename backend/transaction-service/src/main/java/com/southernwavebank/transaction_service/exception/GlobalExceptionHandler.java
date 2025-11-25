package com.southernwavebank.transaction_service.exception;

import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;

import com.southernwavebank.transaction_service.model.response.ErrorResponse;

import lombok.extern.slf4j.Slf4j;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    // Handle ResourceNotFound exceptions
    @ExceptionHandler(ResourceNotFound.class)
	public ResponseEntity<ErrorResponse> handleResourceNotFound(ResourceNotFound ex) {
	    log.warn("ResourceNotFound exception handled");
	    return buildErrorResponse(HttpStatus.NOT_FOUND, "Provide valid data", ex.getIdentifier(), ex.getMessage());
	}

    // Handle UnauthorizedAccessException
    @ExceptionHandler(UnauthorizedAccessException.class)
	public ResponseEntity<ErrorResponse> handleUnauthorizedAccessException(UnauthorizedAccessException ex) {
	    log.warn("UnauthorizedAccessException handled");
	    return buildErrorResponse(HttpStatus.UNAUTHORIZED, "You cannot access other account", ex.getIdentifier(), ex.getMessage());
	}
    
    @ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ErrorResponse> handleValidationErrors(MethodArgumentNotValidException ex) {
		log.warn("MethodArgumentNotValidException caught");

		String errorMessage = ex.getBindingResult().getFieldErrors().stream()
				.map(DefaultMessageSourceResolvable::getDefaultMessage).findFirst().orElse("Validation error");

		return buildErrorResponse(HttpStatus.BAD_REQUEST, "Value required", null, errorMessage);
	}

	@ExceptionHandler(HttpMessageNotReadableException.class)
	public ResponseEntity<ErrorResponse> handleHttpMessageNotReadable(HttpMessageNotReadableException ex) {
		log.warn("HttpMessageNotReadableException caught");
		return buildErrorResponse(HttpStatus.BAD_REQUEST, "Request body is missing or malformed", "No data given",
				"Empty JSON");
	}

	@ExceptionHandler(NoHandlerFoundException.class)
	public ResponseEntity<ErrorResponse> handleNoHandlerFound(NoHandlerFoundException ex) {
		log.warn("NoHandlerFoundException caught: {}", ex.getRequestURL());
		return buildErrorResponse(HttpStatus.NOT_FOUND, "Endpoint not found", null, ex.getRequestURL());
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<ErrorResponse> handleGenericException(Exception ex) {
	    log.error("Unhandled exception occurred", ex);
	    return buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR,"Try again", null,"An unexpected error occurred");
	}

    
    private ResponseEntity<ErrorResponse> buildErrorResponse(HttpStatus status, String message, Object data, String error) {
        return ResponseEntity.status(status)
                .body(ErrorResponse.builder()
                        .message(message)
                        .data(data)
                        .error(error)
                        .build());
    }

}
