package com.southernwavebank.transaction_service.exception;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

	private ObjectMapper objectMapper;

    public CustomAuthenticationEntryPoint(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                         AuthenticationException authException) throws IOException {

    	  response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // 401
          response.setContentType("application/json");

          Map<String, Object> responseBody = new LinkedHashMap<>();
          responseBody.put("timestamp", 
        		    LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
          responseBody.put("errorCode", "UNAUTHORIZED_ACCESS");
          responseBody.put("message", "There is a problem in your JWT. Either JWT is invalid or not provided");

          response.getWriter().write(objectMapper.writeValueAsString(responseBody));
          
    }

	
}


