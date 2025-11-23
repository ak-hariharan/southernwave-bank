package com.southernwavebank.gateway_service.actuator;

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
	        details.put("App Name", "API Gateway");
	        details.put("Description", "This service acts as the unified entry point to route requests to microservices securely.");
	        builder.withDetail("service-info", details);
	}
}

