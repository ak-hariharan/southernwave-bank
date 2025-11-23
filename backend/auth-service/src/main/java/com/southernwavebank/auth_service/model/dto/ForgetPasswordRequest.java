package com.southernwavebank.auth_service.model.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ForgetPasswordRequest {

	@NotNull(message = "Name is required")
	private String name;
	
	@NotNull(message = "Email is required")
	private String emailId;
}
