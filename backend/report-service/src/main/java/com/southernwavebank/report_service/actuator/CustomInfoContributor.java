package com.southernwavebank.report_service.actuator;

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
	        details.put("App Name", "Report Service");
	        details.put("Description", "This service generates and manages transaction reports for users.");
	        builder.withDetail("service-info", details);
	}
}

