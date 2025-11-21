package com.southernwavebank.user_service.actuator;

import java.util.HashMap;
import java.util.Map;

import org.springframework.boot.actuate.info.InfoContributor;
import org.springframework.boot.actuate.info.Info.Builder;
import org.springframework.stereotype.Component;

@Component
public class CustomInfoContributor implements InfoContributor {
 
	@Override
	public void contribute(Builder builder) {
		  Map<String, Object> details = new HashMap<>();
	        details.put("App Name", "User Service");
	        details.put("Description", "This service manages user registration, profile updates, and user details.");
	        builder.withDetail("service-info", details);
	}
}

