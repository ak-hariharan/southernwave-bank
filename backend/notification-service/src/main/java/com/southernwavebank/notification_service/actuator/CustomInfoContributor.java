package com.southernwavebank.notification_service.actuator;

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
	        details.put("App Name", "Notification Service");
	        details.put("Description", "This service sends email and SMS notifications related to account activities and reports.");
	        builder.withDetail("service-info", details);
	}
}

