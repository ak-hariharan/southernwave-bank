package com.southernwavebank.auth_service.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.southernwavebank.auth_service.controller.AuthController;
import com.southernwavebank.auth_service.jwt.JWTService;
import com.southernwavebank.auth_service.model.Role;
import com.southernwavebank.auth_service.model.dto.AuthResponse;
import com.southernwavebank.auth_service.model.dto.ForgetPasswordRequest;
import com.southernwavebank.auth_service.model.dto.LoginRequest;
import com.southernwavebank.auth_service.model.dto.LogoutRequest;
import com.southernwavebank.auth_service.model.dto.ResetPasswordRequest;
import com.southernwavebank.auth_service.model.dto.TokenRefreshRequest;
import com.southernwavebank.auth_service.reponse.Response;
import com.southernwavebank.auth_service.service.AuthService;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockitoPostProcessor;


import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDateTime;

@WebMvcTest(AuthController.class)

public class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AuthService authService;

    @MockitoBean
    private JWTService jwtService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testLogin() throws Exception {
        LoginRequest loginRequest = new LoginRequest("testuser", "password");
        AuthResponse authResponse = new AuthResponse("token1", "token2");
        ResponseEntity<AuthResponse> responseEntity = ResponseEntity.ok(authResponse);
        when(authService.loginUser(loginRequest)).thenReturn(responseEntity);

        mockMvc.perform(post("/swb/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").value("token1"));

    }
    
    @Test
    void testRefershToken() throws Exception {
    	TokenRefreshRequest request = new TokenRefreshRequest("token1");
    	AuthResponse response = new AuthResponse("token2", "token1");
    	
    	ResponseEntity<AuthResponse> responseEntity = ResponseEntity.ok(response);
    	when(authService.refreshToken(request)).thenReturn(responseEntity);
    	
    	mockMvc.perform(post("/swb/auth/refresh")
    			.contentType(MediaType.APPLICATION_JSON)
    			.content(objectMapper.writeValueAsString(request)))
    			.andExpect(status().isOk())
    			.andExpect(jsonPath("$.accessToken").value("token2"));
    }

    @Test
    void testLogout() throws Exception {
        LogoutRequest logoutRequest = new LogoutRequest("testuser");
        Response response = new Response("Logged out", null);

        ResponseEntity<Response> responseEntity = ResponseEntity.ok(response);
        when(authService.logoutUser(logoutRequest)).thenReturn(responseEntity);

        mockMvc.perform(post("/swb/auth/logout")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(logoutRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Logged out"));
    }
    
    @Test
    void testForgetPassword() throws Exception {
    	ForgetPasswordRequest forgetPasswordRequest = new ForgetPasswordRequest("user","test@mail.com");
        Response response = new Response("Otp generated successffully", null);
        ResponseEntity<Response> responseEntity = ResponseEntity.ok(response);
        when(authService.forgetPassword(org.mockito.ArgumentMatchers.eq(forgetPasswordRequest), org.mockito.ArgumentMatchers.any(LocalDateTime.class))).thenReturn(responseEntity);

        mockMvc.perform(post("/swb/auth/forget-password")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(forgetPasswordRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Otp generated successffully"));
    }
    
    @Test
    void testResetPassword() throws Exception {
    	ResetPasswordRequest resetPasswordRequest = new ResetPasswordRequest(234651,"test@password");
    	String email = "test@mail.com";
        Response response = new Response("Password reset successffully", null);
        ResponseEntity<Response> responseEntity = ResponseEntity.ok(response);
        when(authService.resetPassword(email,resetPasswordRequest)).thenReturn(responseEntity);

        mockMvc.perform(post("/swb/auth/reset-password/{email}",email)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(resetPasswordRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Password reset successffully"));
    }

    @Test
    void testValidateToken() throws Exception {
        String token = "Bearer mytoken";
        Response response = new Response("Valid token", null);

        ResponseEntity<Response> validationResponse = ResponseEntity.ok(response);
        when(authService.validateToken(token)).thenReturn(validationResponse);

        mockMvc.perform(get("/swb/auth/validate")
                .header("Authorization", token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Valid token"));
    }
    
}

