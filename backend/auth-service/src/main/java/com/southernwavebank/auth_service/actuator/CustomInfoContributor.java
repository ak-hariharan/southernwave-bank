package com.southernwavebank.auth_service.actuator;

import java.util.HashMap;
import java.util.Map;

import org.springframework.boot.actuate.info.Info.Builder;
import org.springframework.boot.actuate.info.InfoContributor;
import org.springframework.stereotype.Component;

@Component
public class CustomInfoContributor implements InfoContributor {
 
	@Override
	public void contribute(Builder builder) {
		  Map<String, Object> details = new HashMap<>();
	        details.put("App Name", "Auth Service");
	        details.put("Description", "This service handles user authentication, authorization, and JWT token generation.");
	        builder.withDetail("service-info", details);
	}
}

