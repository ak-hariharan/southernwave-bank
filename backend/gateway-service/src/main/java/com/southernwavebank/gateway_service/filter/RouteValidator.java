package com.southernwavebank.gateway_service.filter;

import java.util.List;

import org.springframework.stereotype.Component;

@Component
public class RouteValidator {
	
	private static final List<String> openApiEndpoints = List.of(
	        "/swb/auth/register",
	        "/swb/auth/login",
	        "/swb/auth/validate",
	        "/swb/auth/logout",
	        "/swb/auth/forget-password",
	        "/swb/auth/reset-password",
	        "/swb/auth/refresh",
	        "/actuator",
	        "/swagger-ui",
	        "/v3/api-docs"
	    );
	
	public boolean isPublic(String path) {
        return openApiEndpoints.stream().anyMatch(path::startsWith)
               || path.contains("swagger-ui") || path.contains("/v3/api-docs");
    }
}
