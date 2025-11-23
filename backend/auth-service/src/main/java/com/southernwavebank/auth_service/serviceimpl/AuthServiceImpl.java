package com.southernwavebank.auth_service.serviceimpl;

import java.time.LocalDateTime;
import java.util.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClient.Builder;

import com.southernwavebank.auth_service.exception.UnAuthorizedUserException;
import com.southernwavebank.auth_service.jwt.JWTService;
import com.southernwavebank.auth_service.kafka.OtpNotificationEventProducer;
import com.southernwavebank.auth_service.model.dto.*;
import com.southernwavebank.auth_service.model.entity.UserTokenInfo;
import com.southernwavebank.auth_service.model.externaldto.*;
import com.southernwavebank.auth_service.reponse.Response;
import com.southernwavebank.auth_service.repository.UserTokenInfoRepository;
import com.southernwavebank.auth_service.service.AuthService;
import com.southernwavebank.auth_service.util.OtpRedisService;

import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Core authentication service implementation that manages registration, login, tokens and OTP flows.
 * Bridges to user-service, Redis and Kafka to implement authentication features.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService{

    private JWTService jwtService;
	private WebClient.Builder webClientBuilder;
	private UserTokenInfoRepository userTokenInfoRepo;
    private OtpNotificationEventProducer otpNotificationEventProducer;
    private OtpRedisService otpRedisService; 
    
    @Autowired
    public AuthServiceImpl(JWTService jwtService, Builder webClientBuilder, UserTokenInfoRepository userTokenInfoRepo,
			OtpNotificationEventProducer otpNotificationEventProducer, OtpRedisService otpRedisService) {
		this.jwtService = jwtService;
		this.webClientBuilder = webClientBuilder;
		this.userTokenInfoRepo = userTokenInfoRepo;
		this.otpNotificationEventProducer = otpNotificationEventProducer;
		this.otpRedisService = otpRedisService;
	}


	/**
	 * Register a new user via user-service and create initial JWT tokens.
	 * @returns ResponseEntity<Response> with created tokens on success.
	 * @throws RuntimeException when user-service response is invalid or indicates failure.
	 */
	// completed
	@Override
	public ResponseEntity<Response> registerUser(RegisterRequest registerRequest) {
		log.info("Register request received");
		log.info("Calling user service to register the user");
		ResponseEntity<Response> entity = webClientBuilder.build().post().uri("http://user-service/boi/users/register")
				.bodyValue(registerRequest).exchangeToMono(clientResponse -> clientResponse.toEntity(Response.class))
				.block(); // Blocking call to wait // for response
		log.info("Received user service response");

		if (entity == null || !entity.getStatusCode().is2xxSuccessful() || entity.getBody() == null
				|| entity.getBody().getData() == null) {
			log.warn("User registration failed: User service returned {}",
					entity != null ? entity.getStatusCode() : "null");

			String errorMessage = (entity != null && entity.getBody() != null) ? entity.getBody().getMessage()
					: "Response is null";
			throw new RuntimeException(errorMessage);
		}

		Response response = entity.getBody();
		Map<String, Object> userData = (Map<String, Object>) response.getData();
		String emailId = (String) userData.get("emailId");
		String role = (String) userData.get("role");
		String id = String.valueOf(userData.get("officerId"));
		userTokenInfoRepo.save(new UserTokenInfo(emailId, 0));

		String accessToken = jwtService.generateToken(emailId, role, 0, id, "auth-service");
		String refreshToken = jwtService.generateRefreshToken(emailId, 0);

		AuthResponse tokens = new AuthResponse();
		tokens.setAccessToken(accessToken);
		tokens.setRefreshToken(refreshToken);

		log.info("User registered and token created");
		return ResponseEntity.status(HttpStatus.CREATED)
				.body(Response.builder().message("JWT token created").data(tokens).build());

	}

	/**
	 * Authenticate user credentials and return access and refresh tokens.
	 * @returns ResponseEntity<AuthResponse> containing access and refresh tokens upon success.
	 * @throws UnAuthorizedUserException when authentication fails or user-service returns error.
	 */
	// completed
	@Override
	@Transactional
	public ResponseEntity<AuthResponse> loginUser(LoginRequest principal) {
		log.info("Login attempt received");
		// Authenticate with User Service
		log.info("Calling user service to check user existence against DB");
		ResponseEntity<Response> entity = webClientBuilder.build().post().uri("http://user-service/boi/users/login")
				.bodyValue(principal).exchangeToMono(clientResponse -> clientResponse.toEntity(Response.class)).block();
		log.info("Received user service response");

		if (entity == null || !entity.getStatusCode().is2xxSuccessful() || entity.getBody() == null
				|| entity.getBody().getData() == null) {
			log.warn("Login failed: User service returned {}",
					(entity != null && entity.getBody() != null) ? entity.getStatusCode() : "null");

			String errorMessage = (entity != null && entity.getBody() != null) ? entity.getBody().getMessage()
					: "Response is null";
			throw new UnAuthorizedUserException(errorMessage);
		}

		Response response = entity.getBody();

		Map<String, Object> userData = (Map<String, Object>) response.getData();
		String emailId = (String) userData.get("emailId");
		String role = (String) userData.get("role");
		String userId;

		if ("CONSUMER".equals(role)) {
			userId = userData.get("userId").toString();
		} else {
			userId = userData.get("officerId").toString();
		}

		log.debug("User authenticated: {} with role {}", emailId, role);

		int tokenVersion;
		if ("CONSUMER".equalsIgnoreCase(role)) {
			tokenVersion = userTokenInfoRepo.findById(emailId).map(UserTokenInfo::getTokenVersion).orElseGet(() -> {
				log.info("First-time login for CONSUMER: saving token version 0");
				userTokenInfoRepo.save(new UserTokenInfo(emailId, 0));
				return 0;
			});

		} else {
			tokenVersion = userTokenInfoRepo.findById(emailId).map(UserTokenInfo::getTokenVersion).get();
		}

		// Generate both tokens
		String accessToken = jwtService.generateToken(emailId, role, tokenVersion, userId, "auth-service");
		String refreshToken = jwtService.generateRefreshToken(emailId, tokenVersion);

		log.info("Access and Refresh tokens generated successfully");

		return ResponseEntity.status(HttpStatus.OK)
				.body(AuthResponse.builder().accessToken(accessToken).refreshToken(refreshToken).build());
	}
	
	/**
	 * Invalidate current tokens for a user by incrementing token version (logout).
	 * @returns ResponseEntity<Response> indicating logout success or user-not-found.
	 * @throws RuntimeException when repository operations fail unexpectedly.
	 */
	// completed
	@Override
	@Transactional
	public ResponseEntity<Response> logoutUser(LogoutRequest logoutRequest) {
		log.info("Logout request received");
		Optional<UserTokenInfo> optional = userTokenInfoRepo.findById(logoutRequest.getEmailId());

		if (optional.isPresent()) {
			UserTokenInfo tokenInfo = optional.get();
			tokenInfo.setTokenVersion(tokenInfo.getTokenVersion() + 1);
			userTokenInfoRepo.save(tokenInfo);

			return ResponseEntity.status(HttpStatus.OK)
					.body(Response.builder().message("Logged out successfully").build());
		} else {
			log.warn("Logout failed: User not found");
			return ResponseEntity.status(HttpStatus.NOT_FOUND)
					.body(Response.builder().message("User not found").build());
		}

	}

	/**
	 * Refresh the access token using a validated refresh token.
	 * @returns ResponseEntity<AuthResponse> with a new access token and the same refresh token.
	 * @throws UnAuthorizedUserException when refresh token is invalid or expired.
	 */
	// completed
	@Override
	public ResponseEntity<AuthResponse> refreshToken(TokenRefreshRequest request) {
		log.info("Refresh token request received");
		String refreshToken = request.getRefreshToken();
		if (refreshToken == null || refreshToken.isEmpty()) {
			throw new RuntimeException("Refresh token is missing");
		}

		// Validate refresh token
		String username = jwtService.extractUsername(refreshToken);

		Optional<UserTokenInfo> tokenInfo = userTokenInfoRepo.findById(username);
		Integer tokenVersionInToken = tokenInfo.get().getTokenVersion();

		boolean isValid = jwtService.validateRefreshToken(refreshToken, tokenVersionInToken);

		if (!isValid) {
			throw new UnAuthorizedUserException("Invalid refresh token");
		}

		log.info("Calling user service for necessary user details");
		ResponseEntity<Response> entity = webClientBuilder.build().get()
				.uri("http://user-service/boi/users/userdata?email=" + username).retrieve().toEntity(Response.class)
				.block();

		log.info("Received user service response");
		if (entity == null || !entity.getStatusCode().is2xxSuccessful() || entity.getBody() == null
				|| entity.getBody().getData() == null) {
			log.warn("Refersh token request failed: User service returned {}",
					(entity != null && entity.getBody() != null) ? entity.getStatusCode() : "null");
			throw new RuntimeException("Failed to retrieve user data");
		}

		Response userDataResponse = entity.getBody();
		Map<String, Object> userData = (Map<String, Object>) userDataResponse.getData();

		String role = (String) userData.get("role");
		String userId;

		if ("CONSUMER".equals(role) || "OFFICER".equals(role)) {
			userId = userData.get("userId").toString();
		} else {
			throw new IllegalArgumentException("Unsupported role: " + role);
		}

		// Issue new access token
		String newAccessToken = jwtService.generateToken(username, role, tokenVersionInToken, userId, "auth-service");

		log.info("Access token refreshed successfully");
		return ResponseEntity.status(HttpStatus.CREATED)
				.body(AuthResponse.builder().accessToken(newAccessToken).refreshToken(refreshToken).build());
	}

	
	/**
	 * Start forget-password flow: verify user via user-service, generate OTP and dispatch notification.
	 * @returns ResponseEntity<Response> confirming OTP generation and dispatch.
	 * @throws RuntimeException when user-service verification or OTP saving fails.
	 */
	// completed
	@Override
	public ResponseEntity<Response> forgetPassword(ForgetPasswordRequest forgetPasswordRequest, LocalDateTime time) {
		log.info("Forget password progess started");
		log.info("Calling user service for forget password process");
		ResponseEntity<Response> entity = webClientBuilder.build().post()
				.uri("http://user-service/boi/users/forgot-password").bodyValue(forgetPasswordRequest)
				.exchangeToMono(clientResponse -> clientResponse.toEntity(Response.class)).block();

		log.info("Received user service response");
		if (entity == null || !entity.getStatusCode().is2xxSuccessful() || entity.getBody() == null
				|| entity.getBody().getData() == null) {
			log.warn("Forget password process failed : User service returned {}",
					entity != null ? entity.getStatusCode() : "null");

			String errorMessage = (entity != null && entity.getBody() != null) ? entity.getBody().getMessage()
					: "Response is null";

			throw new RuntimeException(errorMessage);
		}

		Response response = entity.getBody();

		Map<String, Object> userData = (Map<String, Object>) response.getData();
		String role = (String) userData.get("role");
		String userId;

		if ("CONSUMER".equals(role)) {
			userId = userData.get("userId").toString();
		} else {
			userId = userData.get("officerId").toString();
		}

		int otpValue = 100000 + (int) (Math.random() * 900000);
		String emailId = forgetPasswordRequest.getEmailId();

		// Save OTP in Redis cloud
		otpRedisService.saveOtp(emailId, otpValue);

		NotificationDto notificationDto = new NotificationDto();
		notificationDto.setOtp(otpValue);
		notificationDto.setEmailId(emailId);
		notificationDto.setUserId(Long.valueOf(userId));
		notificationDto.setTime(time);
		otpNotificationEventProducer.sendOtpNotification(notificationDto);
		log.info("OTP generated successfully");

		return ResponseEntity.status(HttpStatus.OK)
				.body(Response.builder().message("OTP generated successfully").data("OTP sent to email").build());
	}
	
	/**
	 * Reset a user's password after validating the OTP stored in Redis.
	 * @returns ResponseEntity<Response> confirming password update on success.
	 * @throws UnAuthorizedUserException when OTP is missing or does not match.
	 */
	// completed
	@Override
	public ResponseEntity<Response> resetPassword(String email, ResetPasswordRequest resetPasswordRequest) {
		log.info("Reset password request received");

		Integer storedOtp = otpRedisService.getOtp(email);
		if (storedOtp == null) {
			throw new UnAuthorizedUserException("OTP not found");
		}

		if (!storedOtp.equals(resetPasswordRequest.getOtp())) {
			throw new UnAuthorizedUserException("Invalid OTP. Please try again");
		}

		UpdatePasswordRequest updateRequest = new UpdatePasswordRequest();
		updateRequest.setEmailId(email);
		updateRequest.setNewPassword(resetPasswordRequest.getPassword());

		log.info("Calling user service to reset password");
		ResponseEntity<Response> entity = webClientBuilder.build().put()
				.uri("http://user-service/boi/users/update-password").bodyValue(updateRequest)
				.exchangeToMono(clientResponse -> clientResponse.toEntity(Response.class)).block();
		log.info("Received user service response");
		if (entity == null || !entity.getStatusCode().is2xxSuccessful() || entity.getBody() == null) {
			log.warn("Forget password process failed : User service returned {}",
					entity != null ? entity.getStatusCode() : "null");

			String errorMessage = (entity != null && entity.getBody() != null) ? entity.getBody().getMessage()
					: "Response is null";

			throw new RuntimeException(errorMessage);
		}
		otpRedisService.deleteOtp(email); // Invalidate OTP after success

		return ResponseEntity.status(HttpStatus.OK)
				.body(Response.builder().message("Password updated successfully.").data(null).build());

	}

	
	/**
	 * Validate the provided bearer token and ensure token-version remains current.
	 * @returns ResponseEntity<Response> indicating token validity when checks pass.
	 * @throws UnAuthorizedUserException when token is missing, malformed or invalid.
	 */
	// completed
	@Override
	public ResponseEntity<Response> validateToken(String authHeader) {
		log.info("Token validation request received");
		if (authHeader == null || !authHeader.startsWith("Bearer ")) {
			log.warn("Token missing or malformed");
			throw new UnAuthorizedUserException("Invalid Token");
		}

		String token = authHeader.substring(7);
		String emailId = jwtService.extractUsername(token);

		Optional<UserTokenInfo> optionalInfo = userTokenInfoRepo.findById(emailId);

		if (optionalInfo.isEmpty()) {
			log.warn("Token validation failed: User info not found");
			throw new UnAuthorizedUserException("Token validation failed");
		}

		int currentVersion = optionalInfo.get().getTokenVersion();

		if (!jwtService.validateToken(token, currentVersion)) {
			log.warn("Token validation failed: Invalid token version");
			throw new UnAuthorizedUserException("Token validation failed");
		}

		log.info("Token validated successfully");
		return ResponseEntity.status(HttpStatus.OK)
				.body(Response.builder().message("Token is valid").data(null).build());

	}
	
	/**
	 * Delegate an existing token to a target service by re-issuing a token with the target audience.
	 * @returns ResponseEntity<?> with a delegated token mapped under "token" on success.
	 * @throws RuntimeException when original token is missing or claims extraction fails.
	 */
	// completed
	@Override
	public ResponseEntity<?> delegateToken(String authHeader, String targetService) {
			if (authHeader == null || !authHeader.startsWith("Bearer ")) {
				return ResponseEntity
						.status(HttpStatus.BAD_REQUEST)
						.body("Missing or invalid Authorization header");
			}

			String originalToken = authHeader.substring(7);
			Claims claims = jwtService.extractClaimsFromToken(originalToken);

			String username = claims.getSubject();
			String role = claims.get("role", String.class);
			String userId = claims.get("userId", String.class);
			Integer tokenVersion = claims.get("tokenVersion", Integer.class);

			String delegatedToken = jwtService.generateToken(username, role, tokenVersion, userId, targetService);
			return ResponseEntity.ok(Map.of("token", delegatedToken));
	}

}
