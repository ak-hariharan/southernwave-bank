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
public class ResetPasswordRequest {
	
	@NotNull(message = "OTP is required")
	private Integer otp;

	@NotNull(message = "Password is required")
	private String password;
}
