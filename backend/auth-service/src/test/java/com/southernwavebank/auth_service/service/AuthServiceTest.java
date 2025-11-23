package com.southernwavebank.auth_service.service;
//package com.bankofindia.auth_service.service;
//
//import static org.junit.jupiter.api.Assertions.assertEquals;
//import static org.junit.jupiter.api.Assertions.assertFalse;
//import static org.junit.jupiter.api.Assertions.assertNotNull;
//import static org.junit.jupiter.api.Assertions.assertNull;
//import static org.junit.jupiter.api.Assertions.assertThrows;
//import static org.junit.jupiter.api.Assertions.assertTrue;
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.ArgumentMatchers.anyInt;
//import static org.mockito.ArgumentMatchers.anyString;
//import static org.mockito.ArgumentMatchers.eq;
//import static org.mockito.Mockito.never;
//import static org.mockito.Mockito.times;
//import static org.mockito.Mockito.verify;
//import static org.mockito.Mockito.when;
//
//import java.util.HashMap;
//import java.util.Map;
//import java.util.Optional;
//import java.util.concurrent.ConcurrentHashMap;
//
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//import org.springframework.test.util.ReflectionTestUtils;
//import org.springframework.web.reactive.function.client.WebClient;
//import org.springframework.web.reactive.function.client.WebClient.RequestHeadersSpec;
//
//import com.bankofindia.auth_service.exception.UnAuthorizedUserException;
//import com.bankofindia.auth_service.kafka.OtpNotificationEventProducer;
//import com.bankofindia.auth_service.model.AuthResponse;
//import com.bankofindia.auth_service.model.ForgetPasswordRequest;
//import com.bankofindia.auth_service.model.LoginRequest;
//import com.bankofindia.auth_service.model.LogoutRequest;
//import com.bankofindia.auth_service.model.RegisterRequest;
//import com.bankofindia.auth_service.model.ResetPasswordRequest;
//import com.bankofindia.auth_service.model.Role;
//import com.bankofindia.auth_service.model.TokenRefreshRequest;
//import com.bankofindia.auth_service.model.entity.UserTokenInfo;
//import com.bankofindia.auth_service.model.externaldto.NotificationDto;
//import com.bankofindia.auth_service.model.externaldto.UpdatePasswordRequest;
//import com.bankofindia.auth_service.reponse.Response;
//import com.bankofindia.auth_service.repository.UserTokenInfoRepository;
//import com.bankofindia.auth_service.util.OtpData;
//import com.fasterxml.jackson.databind.ObjectMapper;
//
//import reactor.core.publisher.Mono;
//
//@ExtendWith(MockitoExtension.class)
//public class AuthServiceTest {
//
//    @Mock
//    private WebClient.Builder webClientBuilder;
//
//    @Mock
//    private WebClient webClient;
//
//    @Mock
//    private WebClient.RequestBodyUriSpec requestBodyUriSpec;
//
//    @Mock
//    private WebClient.RequestBodySpec requestBodySpec;
//
//    @Mock
//    private WebClient.RequestHeadersSpec<?> requestHeadersSpec;
//  
//    @Mock 
//    private WebClient.RequestHeadersUriSpec requestHeadersUriSpec;
//    
//    @Mock
//    private WebClient.ResponseSpec responseSpec;
//
//    @Mock
//    private UserTokenInfoRepository userTokenInfoRepo;
//
//    @Mock
//    private JwtService jwtService;
//
//    @Mock
//    private ObjectMapper objectMapper;
//    
//    @Mock
//    private OtpNotificationEventProducer otpNotificationEventProducer;
//
//    @InjectMocks
//    private AuthService authService;
//    
//    
//    private Map<String, OtpData> otpStore;
//    
//    @BeforeEach
//    void setUp() {
//        ReflectionTestUtils.setField(authService, "created", "201");
//        ReflectionTestUtils.setField(authService, "conflict", "409");
//        ReflectionTestUtils.setField(authService, "unauthorized", "401");
//        ReflectionTestUtils.setField(authService, "success", "200");
//        ReflectionTestUtils.setField(authService, "not_found", "404");
//        ReflectionTestUtils.setField(authService, "bad_request", "400");
//        
//        otpStore = new ConcurrentHashMap<>();
//        ReflectionTestUtils.setField(authService, "otpStore", otpStore);
//    }
//
//    
//    //--Login Starts--
//    
//    @Test
//    void testLoginUser_successConsumer() {
//        // Given
//        LoginRequest loginRequest = new LoginRequest("user@example.com", "password");
//
//        Map<String, Object> userData = new HashMap<>();
//        userData.put("emailId", "user@example.com");
//        userData.put("role", "CONSUMER");
//
//        Response mockResponse = new Response();
//        mockResponse.setData(userData);
//
//        // Setup WebClient chain
//        when(webClientBuilder.build()).thenReturn(webClient);
//        when(webClient.post()).thenReturn(requestBodyUriSpec);
//        when(requestBodyUriSpec.uri("http://user-service/boi/users/login")).thenReturn(requestBodySpec);
//        when(requestBodySpec.bodyValue(loginRequest))
//        .thenReturn((WebClient.RequestHeadersSpec) requestHeadersSpec);
//        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
//        when(responseSpec.bodyToMono(Response.class)).thenReturn(Mono.just(mockResponse));
//
//        // Setup token repo
//        when(userTokenInfoRepo.findById("user@example.com")).thenReturn(Optional.empty());
//        when(jwtService.generateToken("user@example.com", "CONSUMER", 0)).thenReturn("mock-accessToken");
//        when(jwtService.generateRefreshToken("user@example.com",0)).thenReturn("mock-refreshToken");
//
//        // When
//        AuthResponse authResponse = authService.loginUser(loginRequest);
//
//        // Then 
//        assertNotNull(authResponse);
//        assertEquals("mock-accessToken", authResponse.getAccessToken());
//        assertEquals("mock-refreshToken", authResponse.getRefreshToken());
//
//        verify(userTokenInfoRepo).save(any(UserTokenInfo.class));
//        verify(jwtService).generateToken("user@example.com", "CONSUMER", 0);
//    }
//    
//    @Test
//    void testLoginUser_successOfficer() {
//        // Given
//        LoginRequest loginRequest = new LoginRequest("user@example.com", "password");
//
//        Map<String, Object> userData = new HashMap<>();
//        userData.put("emailId", "officer@example.com");
//        userData.put("role", "OFFICER");
//
//        Response mockResponse = new Response();
//        mockResponse.setData(userData);
//
//        // Setup WebClient chain
//        when(webClientBuilder.build()).thenReturn(webClient);
//        when(webClient.post()).thenReturn(requestBodyUriSpec);
//        when(requestBodyUriSpec.uri("http://user-service/boi/users/login")).thenReturn(requestBodySpec);
//        when(requestBodySpec.bodyValue(loginRequest))
//        .thenReturn((WebClient.RequestHeadersSpec) requestHeadersSpec);
//        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
//        when(responseSpec.bodyToMono(Response.class)).thenReturn(Mono.just(mockResponse));
//
//        // Setup token repo
//        when(userTokenInfoRepo.findById("officer@example.com")).thenReturn(Optional.of(new UserTokenInfo("officer@example.com", 1)));
//        when(jwtService.generateToken(anyString(), anyString(), anyInt())).thenReturn("generatedToken");
//
//        // When
//        AuthResponse authResponse = authService.loginUser(loginRequest);
//
//        // Then 
//        assertNotNull(authResponse);
//        assertEquals("generatedToken", authResponse.getAccessToken());
//        verify(userTokenInfoRepo, times(0)).save(any(UserTokenInfo.class)); // No save for Officer
//        verify(jwtService, times(1)).generateToken(anyString(), eq("OFFICER"), eq(1));
//    }
//    
//    @Test
//    void testLoginUser_responseNull() {
//        // Given
//        LoginRequest loginRequest = new LoginRequest("user@example.com", "password");
//
//        // Mocked response is null
//        when(webClientBuilder.build()).thenReturn(webClient);
//        when(webClient.post()).thenReturn(requestBodyUriSpec);
//        when(requestBodyUriSpec.uri("http://user-service/boi/users/login")).thenReturn(requestBodySpec);
//        when(requestBodySpec.bodyValue(loginRequest))
//                .thenReturn((WebClient.RequestHeadersSpec) requestHeadersSpec);
//        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
//        when(responseSpec.bodyToMono(Response.class)).thenReturn(Mono.empty());
//
//        // When & Then
//        UnAuthorizedUserException exception = assertThrows(UnAuthorizedUserException.class, () -> {
//            authService.loginUser(loginRequest);
//        });
//        assertEquals("Unauthorized user", exception.getMessage());
//    }
//    
//    @Test
//    void testLoginUser_dataNull() {
//        // Given
//        LoginRequest loginRequest = new LoginRequest("user@example.com", "password");
//
//        // Mocked response with null data
//        Response mockResponse = new Response();
//        mockResponse.setData(null);
//
//        // Setup WebClient chain
//        when(webClientBuilder.build()).thenReturn(webClient);
//        when(webClient.post()).thenReturn(requestBodyUriSpec);
//        when(requestBodyUriSpec.uri("http://user-service/boi/users/login")).thenReturn(requestBodySpec);
//        when(requestBodySpec.bodyValue(loginRequest))
//                .thenReturn((WebClient.RequestHeadersSpec) requestHeadersSpec);
//        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
//        when(responseSpec.bodyToMono(Response.class)).thenReturn(Mono.just(mockResponse));
//
//        // When & Then
//        UnAuthorizedUserException exception = assertThrows(UnAuthorizedUserException.class, () -> {
//            authService.loginUser(loginRequest);
//        });
//        assertEquals("Unauthorized user", exception.getMessage());
//    }
//    
//    @Test
//    void testLoginUser_tokenInfoNotFoundForOfficer() {
//        // Given
//        LoginRequest loginRequest = new LoginRequest("officer@example.com", "password");
//
//        // Mocked response with officer role
//        Map<String, Object> userData = new HashMap<>();
//        userData.put("emailId", "officer@example.com");
//        userData.put("role", "OFFICER");
//
//        Response mockResponse = new Response();
//        mockResponse.setData(userData);
//
//        // Setup WebClient chain
//        when(webClientBuilder.build()).thenReturn(webClient);
//        when(webClient.post()).thenReturn(requestBodyUriSpec);
//        when(requestBodyUriSpec.uri("http://user-service/boi/users/login")).thenReturn(requestBodySpec);
//        when(requestBodySpec.bodyValue(loginRequest))
//                .thenReturn((WebClient.RequestHeadersSpec) requestHeadersSpec);
//        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
//        when(responseSpec.bodyToMono(Response.class)).thenReturn(Mono.just(mockResponse));
//
//        // Setup token repo to return empty Optional for the officer
//        when(userTokenInfoRepo.findById("officer@example.com")).thenReturn(Optional.empty());
//
//        // When & Then
//        UnAuthorizedUserException exception = assertThrows(UnAuthorizedUserException.class, () -> {
//            authService.loginUser(loginRequest);
//        });
//        assertEquals("Unauthorized user", exception.getMessage());
//    }
//
//    
//    @Test
//    void testLoginUser_roleNotSupported() {
//        // Given
//        LoginRequest loginRequest = new LoginRequest("user@example.com", "password");
//
//        Map<String, Object> userData = new HashMap<>();
//        userData.put("emailId", "user@example.com");
//        userData.put("role", "UNKNOWN");
//
//        Response mockResponse = new Response();
//        mockResponse.setData(userData);
//
//        // Setup WebClient chain
//        when(webClientBuilder.build()).thenReturn(webClient);
//        when(webClient.post()).thenReturn(requestBodyUriSpec);
//        when(requestBodyUriSpec.uri("http://user-service/boi/users/login")).thenReturn(requestBodySpec);
//        when(requestBodySpec.bodyValue(loginRequest))
//        .thenReturn((WebClient.RequestHeadersSpec) requestHeadersSpec);
//        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
//        when(responseSpec.bodyToMono(Response.class)).thenReturn(Mono.just(mockResponse));
//
//        // When & Then
//        UnAuthorizedUserException exception = assertThrows(UnAuthorizedUserException.class, () -> {
//            authService.loginUser(loginRequest);
//        });
//        assertEquals("Unauthorized user", exception.getMessage());
//    }
//    
//    //--Login Ends--
//    
//    //--Refresh token starts---
//    @Test
//    void testRefreshToken_success() {
//        // Arrange
//        String refreshToken = "valid-refresh-token";
//        String username = "user@example.com";
//        int tokenVersion = 1;
//        String role = "CONSUMER";
//        String newAccessToken = "new-access-token";
//
//        TokenRefreshRequest request = new TokenRefreshRequest();
//        request.setRefreshToken(refreshToken);
//
//        UserTokenInfo tokenInfo = new UserTokenInfo();
//        tokenInfo.setTokenVersion(tokenVersion);
//
//        when(jwtService.extractUsername(refreshToken)).thenReturn(username);
//        when(userTokenInfoRepo.findById(username)).thenReturn(Optional.of(tokenInfo));
//        when(jwtService.validateRefreshToken(refreshToken, tokenVersion)).thenReturn(true);
//
//        // Mock WebClient flow
//        when(webClientBuilder.build()).thenReturn(webClient);
//        when(webClient.get()).thenReturn(requestHeadersUriSpec);
//        when(requestHeadersUriSpec.uri("http://user-service/boi/users/role?email=" + username)).thenReturn(requestHeadersSpec);
//        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
//        when(responseSpec.bodyToMono(String.class)).thenReturn(Mono.just(role));
//
//        when(jwtService.generateToken(username, role, tokenVersion)).thenReturn(newAccessToken);
//
//        // Act
//        AuthResponse response = authService.refreshToken(request);
//
//        // Assert
//        assertNotNull(response);
//        assertEquals(newAccessToken, response.getAccessToken());
//        assertEquals(refreshToken, response.getRefreshToken());
//    }
//    
//    @Test
//    void testRefreshToken_missingToken_shouldThrowException() {
//        // Arrange
//        TokenRefreshRequest request = new TokenRefreshRequest();
//        request.setRefreshToken(""); // or null to test both scenarios
//
//        // Act & Assert
//        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
//            authService.refreshToken(request);
//        });
//
//        assertEquals("Invalid refresh request", exception.getMessage());
//    }
//    
//    @Test
//    void testRefreshToken_nullToken_shouldThrowException() {
//        // Arrange
//        TokenRefreshRequest request = new TokenRefreshRequest();
//        request.setRefreshToken(null);
//
//        // Act & Assert
//        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
//            authService.refreshToken(request);
//        });
//
//        assertEquals("Invalid refresh request", exception.getMessage());
//    }
//
//    @Test
//    void testRefreshToken_isInvalid() {
//        // Arrange
//        String refreshToken = "valid-refresh-token";
//        String username = "user@example.com";
//        int tokenVersion = 1;
//        String role = "CONSUMER";
//        String newAccessToken = "new-access-token";
//
//        TokenRefreshRequest request = new TokenRefreshRequest();
//        request.setRefreshToken(refreshToken);
//
//        UserTokenInfo tokenInfo = new UserTokenInfo();
//        tokenInfo.setTokenVersion(tokenVersion);
//
//        when(jwtService.extractUsername(refreshToken)).thenReturn(username);
//        when(userTokenInfoRepo.findById(username)).thenReturn(Optional.of(tokenInfo));
//        when(jwtService.validateRefreshToken(refreshToken, tokenVersion)).thenReturn(false);
//        
//        UnAuthorizedUserException exception = assertThrows(UnAuthorizedUserException.class, () -> {
//            authService.refreshToken(request);
//        });
//        
//        assertEquals("Invalid refresh token", exception.getMessage());
//    }
//
//
//    
//    //--Refresh token ends---
//    
//    //--Register Starts--
//    @Test
//    void testRegisterUser_success() {
//        // Given
//        RegisterRequest registerRequest = RegisterRequest.builder()
//                .name("Test Officer")
//                .emailId("officer@example.com")
//                .password("secure")
//                .role(Role.OFFICER)
//                .contactNumber("9999999999")
//                .build();
//
//        Map<String, Object> userData = new HashMap<>();
//        userData.put("emailId", registerRequest.getEmailId());
//        userData.put("role", registerRequest.getRole().toString());
//
//        Response mockResponse = Response.builder()
//                .responseCode("200")
//                .message("Success")
//                .data(userData)
//                .build();
//
//        when(webClientBuilder.build()).thenReturn(webClient);
//        when(webClient.post()).thenReturn(requestBodyUriSpec);
//        when(requestBodyUriSpec.uri("http://user-service/boi/users/register")).thenReturn(requestBodySpec);
//        when(requestBodySpec.bodyValue(registerRequest)).thenReturn((WebClient.RequestHeadersSpec) requestHeadersSpec);
//        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
//        when(responseSpec.bodyToMono(Response.class)).thenReturn(Mono.just(mockResponse));
//
//        when(objectMapper.convertValue(userData, RegisterRequest.class)).thenReturn(registerRequest);
//        when(jwtService.generateToken("officer@example.com", "OFFICER", 0)).thenReturn("mock-accessToken");
//        when(jwtService.generateRefreshToken("officer@example.com", 0)).thenReturn("mock-refreshToken");
//
//        // When
//        Response response = authService.registerUser(registerRequest);
//        
//
//        // Then
//        assertNotNull(response);
//        assertEquals("201", response.getResponseCode());
//        assertEquals("JWT token created", response.getMessage());
//        
//		
//		AuthResponse data = (AuthResponse) response.getData(); 
//		assertEquals("mock-accessToken", data.getAccessToken());
//		assertEquals("mock-refreshToken", data.getRefreshToken());
//
//        verify(userTokenInfoRepo).save(any(UserTokenInfo.class));
//        verify(jwtService).generateToken("officer@example.com", "OFFICER", 0);
//    }
//    
//    @Test
//    void testRegisterUser_responseIsNull() {
//        RegisterRequest registerRequest = RegisterRequest.builder()
//                .emailId("test@example.com")
//                .role(Role.OFFICER)
//                .build();
//
//        when(webClientBuilder.build()).thenReturn(webClient);
//        when(webClient.post()).thenReturn(requestBodyUriSpec);
//        when(requestBodyUriSpec.uri(anyString())).thenReturn(requestBodySpec);
//        when(requestBodySpec.bodyValue(registerRequest)).thenReturn((WebClient.RequestHeadersSpec) requestHeadersSpec);
//        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
//        when(responseSpec.bodyToMono(Response.class)).thenReturn(Mono.empty());
//
//        Response response = authService.registerUser(registerRequest);
//
//        assertEquals("409", response.getResponseCode());
//        assertEquals("Registration failed, check your registration data", response.getMessage());
//    }
//    
//    @Test
//    void testRegisterUser_responseCodeNot200() {
//        RegisterRequest registerRequest = RegisterRequest.builder()
//                .emailId("test@example.com")
//                .role(Role.OFFICER)
//                .build();
//
//        Response mockResponse = Response.builder()
//                .responseCode("400")
//                .message("Bad Request")
//                .data(new HashMap<>())  // data present, but code is not 200
//                .build();
//
//        when(webClientBuilder.build()).thenReturn(webClient);
//        when(webClient.post()).thenReturn(requestBodyUriSpec);
//        when(requestBodyUriSpec.uri(anyString())).thenReturn(requestBodySpec);
//        when(requestBodySpec.bodyValue(registerRequest)).thenReturn((WebClient.RequestHeadersSpec) requestHeadersSpec);
//        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
//        when(responseSpec.bodyToMono(Response.class)).thenReturn(Mono.just(mockResponse));
//
//        Response response = authService.registerUser(registerRequest);
//
//        assertEquals("409", response.getResponseCode());
//        assertEquals("There is a problem while registering", response.getMessage());
//        assertEquals("Bad Request", response.getData());
//    }
//
//    @Test
//    void testRegisterUser_responseDataIsNull() {
//        RegisterRequest registerRequest = RegisterRequest.builder()
//                .emailId("test@example.com")
//                .role(Role.OFFICER)
//                .build();
//
//        Response mockResponse = Response.builder()
//                .responseCode("200")
//                .message("Success but no data")
//                .data(null)  // data is null
//                .build();
//
//        when(webClientBuilder.build()).thenReturn(webClient);
//        when(webClient.post()).thenReturn(requestBodyUriSpec);
//        when(requestBodyUriSpec.uri(anyString())).thenReturn(requestBodySpec);
//        when(requestBodySpec.bodyValue(registerRequest)).thenReturn((WebClient.RequestHeadersSpec) requestHeadersSpec);
//        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
//        when(responseSpec.bodyToMono(Response.class)).thenReturn(Mono.just(mockResponse));
//
//        Response response = authService.registerUser(registerRequest);
//
//        assertEquals("409", response.getResponseCode());
//        assertEquals("There is a problem while registering", response.getMessage());
//        assertEquals("Success but no data", response.getData());
//    }
//    
//    @Test
//    void testRegisterUser_exception() {
//        RegisterRequest registerRequest = RegisterRequest.builder()
//                .emailId("test@example.com")
//                .role(Role.OFFICER)
//                .build();
//
//        when(webClientBuilder.build()).thenThrow(new RuntimeException("Connection error"));
//
//        Response response = authService.registerUser(registerRequest);
//
//        assertEquals("409", response.getResponseCode());
//        assertEquals("Registration failed, check your registration data", response.getMessage());
//        assertNull(response.getData());
//    }
//
//    //--Register Ends--
//    
//    //--Logout starts--
//    @Test
//    void logoutUser_successfulLogout_returnsSuccessResponse() {
//        LogoutRequest request = new LogoutRequest();
//        request.setEmailId("user@example.com");
//
//        UserTokenInfo tokenInfo = new UserTokenInfo();
//        tokenInfo.setEmailId("user@example.com");
//        tokenInfo.setTokenVersion(1);
//
//        when(userTokenInfoRepo.findById("user@example.com")).thenReturn(Optional.of(tokenInfo));
//
//        Response response = authService.logoutUser(request);
//
//        assertEquals("200", response.getResponseCode());
//        assertEquals("Logged out successfully", response.getMessage());
//        verify(userTokenInfoRepo).save(any(UserTokenInfo.class));
//    }
//    
//    @Test
//    void logoutUser_userNotFound_returnsNotFoundResponse() {
//        LogoutRequest request = new LogoutRequest();
//        request.setEmailId("user@example.com");
//
//        when(userTokenInfoRepo.findById("user@example.com")).thenReturn(Optional.empty());
//
//        Response response = authService.logoutUser(request);
//
//        assertEquals("404", response.getResponseCode());
//        assertEquals("User not found for logout", response.getMessage());
//        verify(userTokenInfoRepo, never()).save(any());
//    }
//
//    @Test
//    void logoutUser_exceptionThrown_returnsConflictResponse() {
//        LogoutRequest request = new LogoutRequest();
//        request.setEmailId("user@example.com");
//
//        when(userTokenInfoRepo.findById("user@example.com")).thenThrow(new RuntimeException("DB error"));
//
//        Response response = authService.logoutUser(request);
//
//        assertEquals("409", response.getResponseCode());
//        assertEquals("Logout failed due to exception", response.getMessage());
//    }
//
//    //--Logout ends--
//    
//    //--forgetPassword starts----
//    @Test
//    void testForgetPassword_Success() {
//    	ForgetPasswordRequest request = new ForgetPasswordRequest();
//    	request.setEmailId("test@mail.com");
//        Response mockResponse = new Response("200", "User found", new Object());
//        
//        when(webClientBuilder.build()).thenReturn(webClient);
//        when(webClient.post()).thenReturn(requestBodyUriSpec);
//        when(requestBodyUriSpec.uri("http://user-service/boi/users/forgot-password")).thenReturn(requestBodySpec);
//        when(requestBodySpec.bodyValue(request))
//        .thenReturn((WebClient.RequestHeadersSpec) requestHeadersSpec);
//        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
//        
//        when(responseSpec.bodyToMono(Response.class)).thenReturn(Mono.just(mockResponse));
//
//        Response result = authService.forgetPassword(request);
//
//        assertEquals("200", result.getResponseCode());
//        assertEquals("OTP generated successfully", result.getMessage());
//        assertEquals("OTP sent to email", result.getData());
//
//        verify(otpNotificationEventProducer, times(1)).sendOtpNotification(any(NotificationDto.class));
//    }
//    
//	  @Test
//	  void testForgetPassword_NullResponse() {
//		  
//		ForgetPasswordRequest request = new ForgetPasswordRequest();
//	    
//	    when(webClientBuilder.build()).thenReturn(webClient);
//	    when(webClient.post()).thenReturn(requestBodyUriSpec);
//	    when(requestBodyUriSpec.uri("http://user-service/boi/users/forgot-password")).thenReturn(requestBodySpec);
//	    when(requestBodySpec.bodyValue(request))
//	    .thenReturn((WebClient.RequestHeadersSpec) requestHeadersSpec);
//	    when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
//    
//        when(responseSpec.bodyToMono(Response.class)).thenReturn(Mono.empty());
//
//        Response result = authService.forgetPassword(request);
//
//        assertEquals("409", result.getResponseCode());
//        assertEquals("There is a problem while registering", result.getMessage());
//        assertEquals("Response is null", result.getData());
//
//        verify(otpNotificationEventProducer, never()).sendOtpNotification(any());
//    }
//	  
//	  @Test
//	  void testForgetPassword_InvalidResponseCode() {
//		  ForgetPasswordRequest request = new ForgetPasswordRequest();
//	      Response invalidResponse = new Response("400", "Bad request", new Object());
//
//		  when(webClientBuilder.build()).thenReturn(webClient);
//		  when(webClient.post()).thenReturn(requestBodyUriSpec);
//		  when(requestBodyUriSpec.uri("http://user-service/boi/users/forgot-password")).thenReturn(requestBodySpec);
//		  when(requestBodySpec.bodyValue(request))
//		    .thenReturn((WebClient.RequestHeadersSpec) requestHeadersSpec);
//		  when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
//	      when(responseSpec.bodyToMono(Response.class)).thenReturn(Mono.just(invalidResponse));
//
//	      Response result = authService.forgetPassword(request);
//
//	      assertEquals("409", result.getResponseCode());
//	      assertTrue(result.getData().toString().contains("Bad request"));
//
//	      verify(otpNotificationEventProducer, never()).sendOtpNotification(any());
//	  }
//
//	  @Test
//	  void testForgetPassword_NullDataInResponse() {
//		  ForgetPasswordRequest request = new ForgetPasswordRequest();
//	      Response responseWithNullData = new Response("200", "Success", null);
//
//		  when(webClientBuilder.build()).thenReturn(webClient);
//		  when(webClient.post()).thenReturn(requestBodyUriSpec);
//		  when(requestBodyUriSpec.uri("http://user-service/boi/users/forgot-password")).thenReturn(requestBodySpec);
//		  when(requestBodySpec.bodyValue(request))
//		    .thenReturn((WebClient.RequestHeadersSpec) requestHeadersSpec);
//		  when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
//	      when(responseSpec.bodyToMono(Response.class)).thenReturn(Mono.just(responseWithNullData));
//
//	      Response result = authService.forgetPassword(request);
//
//	      assertEquals("409", result.getResponseCode());
//	      assertEquals("There is a problem while registering", result.getMessage());
//	  }
//
//	  @Test
//	  void testForgetPassword_ExceptionThrown() {
//		  ForgetPasswordRequest request = new ForgetPasswordRequest();
//		  Response response = new Response("500", "Internal Server Error", null);
//		  
//		  when(webClientBuilder.build()).thenReturn(webClient);
//		  when(webClient.post()).thenReturn(requestBodyUriSpec);
//		  when(requestBodyUriSpec.uri("http://user-service/boi/users/forgot-password")).thenReturn(requestBodySpec);
//		  when(requestBodySpec.bodyValue(request))
//		    .thenReturn((WebClient.RequestHeadersSpec) requestHeadersSpec);
//		  when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
//		  
//	      when(responseSpec.bodyToMono(Response.class)).thenThrow(new RuntimeException("Service down"));
//
//	      Response result = authService.forgetPassword(request);
//
//	      assertEquals("500", response.getResponseCode());
//	      assertEquals("Internal Server Error", response.getMessage());
//	      assertNull(response.getData());
//
//	      verify(otpNotificationEventProducer, never()).sendOtpNotification(any());
//	  }
//
//    //--forgetPassword ends----
//	  
//	//--resetPassword starts---
//	@Test
//	void testResetPassword_OtpNotFound() {
//		String email = "user@example.com";
//		ResetPasswordRequest request = new ResetPasswordRequest(123456, "newPass");
//
//		Response result = authService.resetPassword(email, request);
//
//		assertEquals("404", result.getResponseCode());
//		assertEquals("OTP not found", result.getMessage());
//	}
//
//	@Test
//	void testResetPassword_ExpiredOtp() {
//		String email = "user@example.com";
//		ResetPasswordRequest request = new ResetPasswordRequest(123456, "newPass");
//
//		long oldTimestamp = System.currentTimeMillis() - (6 * 60 * 1000); // 6 minutes ago
//		otpStore.put(email, new OtpData(123456, oldTimestamp));
//
//		Response result = authService.resetPassword(email, request);
//
//		assertEquals("400", result.getResponseCode());
//		assertEquals("OTP expired. Please request a new one.", result.getMessage());
//	}
//
//	@Test
//	void testResetPassword_InvalidOtp() {
//		String email = "user@example.com";
//		ResetPasswordRequest request = new ResetPasswordRequest(999999, "newPass");
//
//		long now = System.currentTimeMillis();
//		otpStore.put(email, new OtpData(123456, now));
//
//		Response result = authService.resetPassword(email, request);
//
//		assertEquals("401", result.getResponseCode());
//		assertEquals("Invalid OTP. Please try again.", result.getMessage());
//	}
//
//	@Test
//    void testResetPassword_UserServiceFails() {
//		String email = "user@example.com";
//        ResetPasswordRequest request = new ResetPasswordRequest(123456, "newPass");
//
//        otpStore.put(email, new OtpData(123456, System.currentTimeMillis()));
//
//        Response userServiceError = new Response("204", "Internal Error", null);
//
//        when(webClientBuilder.build()).thenReturn(webClient);
//        when(webClient.put()).thenReturn(requestBodyUriSpec);
//        when(requestBodyUriSpec.uri(anyString())).thenReturn(requestBodySpec);
//        when(requestBodySpec.bodyValue(any(UpdatePasswordRequest.class))).thenReturn((WebClient.RequestHeadersSpec) requestHeadersSpec);
//        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
//        when(responseSpec.bodyToMono(Response.class)).thenReturn(Mono.just(userServiceError));
//
//        Response result = authService.resetPassword(email, request);
//
//        assertEquals("500", result.getResponseCode());
//        assertEquals("Failed to update password. Try again later.", result.getMessage());
//    }
//	
//	@Test
//    void testResetPassword_Success() {
//		String email = "user@example.com";
//        ResetPasswordRequest request = new ResetPasswordRequest(123456, "newPass");
//
//        otpStore.put(email, new OtpData(123456, System.currentTimeMillis()));
//
//        Response userServiceSuccess = new Response("200", "Password updated successfully for user", null);
//
//        when(webClientBuilder.build()).thenReturn(webClient);
//        when(webClient.put()).thenReturn(requestBodyUriSpec);
//        when(requestBodyUriSpec.uri("http://user-service/boi/users/update-password")).thenReturn(requestBodySpec);
//		when(requestBodySpec.bodyValue(any(UpdatePasswordRequest.class))).thenReturn((WebClient.RequestHeadersSpec) requestHeadersSpec);
//        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
//        
//        when(responseSpec.bodyToMono(Response.class)).thenReturn(Mono.just(userServiceSuccess));
//
//        Response result = authService.resetPassword(email, request);
//
//        assertEquals("200", result.getResponseCode());
//        assertEquals("Password updated successfully.", result.getMessage());
//        assertNull(result.getData());
//        assertFalse(otpStore.containsKey(email));
//    }
//
//	@Test
//	void testResetPassword_ExceptionThrown() {
//		String email = "user@example.com";
//		ResetPasswordRequest request = new ResetPasswordRequest(123456, "newPass");
//
//		otpStore.put(email, new OtpData(123456, System.currentTimeMillis()));
//
//		when(webClientBuilder.build()).thenThrow(new RuntimeException("Boom"));
//
//		Response result = authService.resetPassword(email, request);
//
//		assertEquals("500", result.getResponseCode());
//		assertEquals("Internal server error", result.getMessage());
//	}
//	//--resetPassword ends-----
//    
//    //--Validate token starts--
//    @Test
//    void validateToken_nullOrInvalidHeader_returnsUnauthorized() {
//        Response response = authService.validateToken(null);
//        assertEquals("401", response.getResponseCode());
//        assertEquals("Invalid token", response.getMessage());
//
//        response = authService.validateToken("InvalidHeader");
//        assertEquals("401", response.getResponseCode());
//        assertEquals("Invalid token", response.getMessage());
//    }
//
//    @Test
//    void validateToken_userNotFound_returnsUnauthorized() {
//        when(jwtService.extractUsername("valid.jwt.token")).thenReturn("test@example.com");
//        when(userTokenInfoRepo.findById("test@example.com")).thenReturn(Optional.empty());
//
//        Response response = authService.validateToken("Bearer valid.jwt.token");
//
//        assertEquals("401", response.getResponseCode());
//        assertEquals("Token validation failed", response.getMessage());
//    }
//
//    @Test
//    void validateToken_invalidTokenVersion_returnsUnauthorized() {
//        when(jwtService.extractUsername("valid.jwt.token")).thenReturn("test@example.com");
//
//        UserTokenInfo tokenInfo = new UserTokenInfo();
//        tokenInfo.setTokenVersion(2);
//        when(userTokenInfoRepo.findById("test@example.com")).thenReturn(Optional.of(tokenInfo));
//
//        when(jwtService.validateToken("valid.jwt.token", 2)).thenReturn(false);
//
//        Response response = authService.validateToken("Bearer valid.jwt.token");
//
//        assertEquals("401", response.getResponseCode());
//        assertEquals("Token validation failed", response.getMessage());
//    }
//    
//    @Test
//    void validateToken_validToken_nonConsumerRole_returnsSuccess() {
//        when(jwtService.extractUsername("valid.jwt.token")).thenReturn("test@example.com");
//
//        UserTokenInfo tokenInfo = new UserTokenInfo();
//        tokenInfo.setTokenVersion(1);
//        when(userTokenInfoRepo.findById("test@example.com")).thenReturn(Optional.of(tokenInfo));
//
//        when(jwtService.validateToken("valid.jwt.token", 1)).thenReturn(true);
//        when(jwtService.extractRole("valid.jwt.token")).thenReturn("OFFICER");
//
//        Response response = authService.validateToken("Bearer valid.jwt.token");
//
//        assertEquals("200", response.getResponseCode());
//        assertEquals("Token is valid", response.getMessage());
//
//        Map<?, ?> data = (Map<?, ?>) response.getData();
//        assertEquals("test@example.com", data.get("emailId"));
//        assertEquals("OFFICER", data.get("role"));
//    }
//
//    @Test
//    void validateToken_validToken_consumerRole_returnsSuccessWithUserId() {
//        // Mock JWT extraction and role validation
//        when(jwtService.extractUsername("valid.jwt.token")).thenReturn("test@example.com");
//
//        // Mock UserTokenInfo repository
//        UserTokenInfo tokenInfo = new UserTokenInfo();
//        tokenInfo.setTokenVersion(1);
//        when(userTokenInfoRepo.findById("test@example.com")).thenReturn(Optional.of(tokenInfo));
//
//        // Mock JWT token validation and role extraction
//        when(jwtService.validateToken("valid.jwt.token", 1)).thenReturn(true);
//        when(jwtService.extractRole("valid.jwt.token")).thenReturn("CONSUMER");
//
//        // Mock WebClient chain
//        when(webClientBuilder.build()).thenReturn(webClient);
//        when(webClient.get()).thenReturn(requestHeadersUriSpec);  // Return RequestHeadersUriSpec for GET
//        when(requestHeadersUriSpec.uri(anyString())).thenReturn(requestHeadersSpec);  // Mock URI
//        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
//
//        // Prepare mock response from user service
//        Response userServiceResponse = Response.builder().data("user123").build();
//        when(responseSpec.bodyToMono(Response.class)).thenReturn(Mono.just(userServiceResponse));
//
//        // Call the method under test
//        Response response = authService.validateToken("Bearer valid.jwt.token");
//
//        // Assertions
//        assertEquals("200", response.getResponseCode());
//        assertEquals("Token is valid", response.getMessage());
//
//        Map<?, ?> data = (Map<?, ?>) response.getData();
//        assertEquals("test@example.com", data.get("emailId"));
//        assertEquals("CONSUMER", data.get("role"));
//        assertEquals("user123", data.get("userId"));
//    }
//
//
//    @Test
//    void validateToken_exceptionThrown_returnsUnauthorized() {
//        when(jwtService.extractUsername(anyString())).thenThrow(new RuntimeException("error"));
//
//        Response response = authService.validateToken("Bearer valid.jwt.token");
//
//        assertEquals("401", response.getResponseCode());
//        assertEquals("Token validation failed due to exception", response.getMessage());
//    }
//
//
//    //--Validate token ends--
//}
//
