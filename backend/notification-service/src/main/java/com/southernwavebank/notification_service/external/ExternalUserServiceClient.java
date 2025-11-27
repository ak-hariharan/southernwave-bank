package com.southernwavebank.notification_service.external;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;


@FeignClient(name = "user-service")
public interface ExternalUserServiceClient {
	
	@GetMapping("/swb/users/{userId}")
	public ResponseEntity<?> getUserData(@PathVariable("userId") Long userId);

}
