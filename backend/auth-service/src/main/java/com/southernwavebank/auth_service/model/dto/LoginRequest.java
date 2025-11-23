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
public class LoginRequest {

	@NotNull(message = "Email is required")
	private String emailId;

	@NotNull(message = "Password is required")
	private String password;

}
