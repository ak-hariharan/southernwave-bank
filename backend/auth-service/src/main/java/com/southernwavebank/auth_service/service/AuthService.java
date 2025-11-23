package com.southernwavebank.auth_service.service;

import java.time.LocalDateTime;

import org.springframework.http.ResponseEntity;

import com.southernwavebank.auth_service.model.*;
import com.southernwavebank.auth_service.model.dto.AuthResponse;
import com.southernwavebank.auth_service.model.dto.ForgetPasswordRequest;
import com.southernwavebank.auth_service.model.dto.LoginRequest;
import com.southernwavebank.auth_service.model.dto.LogoutRequest;
import com.southernwavebank.auth_service.model.dto.RegisterRequest;
import com.southernwavebank.auth_service.model.dto.ResetPasswordRequest;
import com.southernwavebank.auth_service.model.dto.TokenRefreshRequest;
import com.southernwavebank.auth_service.reponse.Response;

/**
 * Authentication service contract defining operations for user registration, login, token handling and OTP flows.
 * Implementations are expected to call user-service, manage tokens and interact with Redis/Kafka as needed.
 */
public interface AuthService {

	/**
	 * Register a new user and generate initial JWT tokens.
	 * @returns ResponseEntity<Response> containing tokens on success.
	 * @throws RuntimeException when user-service registration fails or returns invalid data.
	 */
	 public ResponseEntity<Response> registerUser(RegisterRequest registerRequest);

	/**
	 * Authenticate user credentials and return access & refresh tokens.
	 * @returns ResponseEntity<AuthResponse> with access and refresh tokens when authentication succeeds.
	 * @throws UnAuthorizedUserException when credentials are invalid or user-service authentication fails.
	 */
	 public ResponseEntity<AuthResponse> loginUser(LoginRequest principal);

	/**
	 * Logout the user by invalidating existing tokens via token-version increment.
	 * @returns ResponseEntity<Response> indicating logout success or user-not-found.
	 * @throws RuntimeException on persistence errors.
	 */
	 public ResponseEntity<Response> logoutUser(LogoutRequest logoutRequest);

	/**
	 * Refresh an access token using a provided refresh token.
	 * @returns ResponseEntity<AuthResponse> with a renewed access token.
	 * @throws UnAuthorizedUserException when refresh token validation fails.
	 */
	 public ResponseEntity<AuthResponse> refreshToken(TokenRefreshRequest request);

	/**
	 * Initiate forget-password flow by verifying user and generating/sending an OTP.
	 * @returns ResponseEntity<Response> confirming OTP generation and dispatch.
	 * @throws RuntimeException when user-service verification or OTP dispatch fails.
	 */
	 public ResponseEntity<Response> forgetPassword(ForgetPasswordRequest forgetPasswordRequest, LocalDateTime time);

	/**
	 * Reset the password after OTP verification.
	 * @returns ResponseEntity<Response> confirming the password update.
	 * @throws UnAuthorizedUserException when OTP is missing or invalid.
	 */
	 public ResponseEntity<Response> resetPassword(String email, ResetPasswordRequest resetPasswordRequest);

	/**
	 * Validate a bearer token provided in the Authorization header.
	 * @returns ResponseEntity<Response> indicating the token is valid.
	 * @throws UnAuthorizedUserException when token is missing, malformed or invalid.
	 */
	 public ResponseEntity<Response> validateToken(String authHeader);

	/**
	 * Delegate an existing token to a target service by reissuing it with the service audience.
	 * @returns ResponseEntity<?> containing the delegated token under key "token".
	 * @throws RuntimeException when the original token is invalid or missing.
	 */
	 public ResponseEntity<?> delegateToken(String authHeader, String targetService);
	 
}
