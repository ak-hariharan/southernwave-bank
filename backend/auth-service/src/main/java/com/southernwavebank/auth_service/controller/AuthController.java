package com.southernwavebank.auth_service.controller;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import com.southernwavebank.auth_service.model.dto.LoginRequest;
import com.southernwavebank.auth_service.model.dto.LogoutRequest;
import com.southernwavebank.auth_service.model.dto.AuthResponse;
import com.southernwavebank.auth_service.model.dto.TokenRefreshRequest;
import com.southernwavebank.auth_service.model.dto.ForgetPasswordRequest;
import com.southernwavebank.auth_service.model.dto.ResetPasswordRequest;
import com.southernwavebank.auth_service.reponse.Response;
import com.southernwavebank.auth_service.service.AuthService;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

// REST controller exposing authentication endpoints for registration, login, token and password flows.
// Delegates processing to AuthService and handles incoming HTTP requests and responses.
@RestController
@RequestMapping("/swb/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {

	private AuthService authService;
	
	@Autowired
	public AuthController(AuthService authService) {
		this.authService = authService;
	}

	// Login endpoint: accepts credentials and returns AuthResponse with tokens.
	// Delegates authentication to AuthService.loginUser.
	@PostMapping("/login")
	public ResponseEntity<AuthResponse> login(@Validated @RequestBody LoginRequest loginRequest) {
		log.info("Login request received");
		ResponseEntity<AuthResponse> response = authService.loginUser(loginRequest);
		log.info("Login response sent");
		return response;
	}

	// Logout endpoint: invalidates user tokens by delegating to AuthService.logoutUser.
	// Accepts LogoutRequest and returns operation result.
	@PostMapping("/logout")
	public ResponseEntity<Response> logout(@Validated @RequestBody LogoutRequest logoutRequest) {
		log.info("Logout request received");
		ResponseEntity<Response> response = authService.logoutUser(logoutRequest);
		log.info("Logout request  end");
		return response;
	}

	// Refresh endpoint: accepts refresh token request and returns new access token.
	// Delegates token refresh logic to AuthService.refreshToken.
	@PostMapping("/refresh")
	public ResponseEntity<AuthResponse> refreshAccessToken(@Validated @RequestBody TokenRefreshRequest request) {
		log.info("Refresh token endpoint called");
		ResponseEntity<AuthResponse> response = authService.refreshToken(request);
		log.info("Refresh token endpoint finished");
		return response;
	}

	// Forget-password endpoint: triggers OTP generation and notification.
	// Forwards request to AuthService.forgetPassword with current timestamp.
	@PostMapping("/forget-password")
	public ResponseEntity<Response> forgetPassword(@Validated @RequestBody ForgetPasswordRequest forgetPasswordRequest) {
		log.info("Forget Password request received");
		ResponseEntity<Response> response = authService.forgetPassword(forgetPasswordRequest, LocalDateTime.now());
		log.info("Forget Password request end");
		return response;
	}

	// Reset-password endpoint: accepts OTP and new password, then delegates to AuthService.resetPassword.
	// Path variable email identifies the user whose password will be updated.
	@PostMapping("/reset-password/{email}")
	public ResponseEntity<Response> resetPassword(@PathVariable("email") String email,
			@Validated @RequestBody ResetPasswordRequest resetPasswordRequest) {
		log.info("Reset Password request received");
		ResponseEntity<Response> response = authService.resetPassword(email, resetPasswordRequest);
		log.info("Reset Password request end");
		return response;
	}

	// Validate endpoint: hidden from docs, validates the provided Authorization header token.
	// Delegates the validation check to AuthService.validateToken.
	@GetMapping("/validate")
	@Operation(hidden = true)
	public ResponseEntity<Response> validateToken(@RequestHeader("Authorization") String authHeader) {
		log.info("Token validation request received");
		ResponseEntity<Response> response = authService.validateToken(authHeader);
		log.info("Token validation completed");
		return response;

	}

	// Delegate-token endpoint: hidden from docs, issues a delegated token for provided audience.
	// Delegates to AuthService.delegateToken to create a token for another service.
	@GetMapping("/delegate-token")
	@Operation(hidden = true)
	public ResponseEntity<?> delegateToken(@RequestHeader("Authorization") String authHeader,
										   @RequestParam("aud") String targetService) {
		log.info("Delegate token generation started");
		ResponseEntity<?> response =  authService.delegateToken(authHeader, targetService);
		return response;

	}

}