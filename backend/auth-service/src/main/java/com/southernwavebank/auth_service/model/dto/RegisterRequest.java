package com.southernwavebank.auth_service.model.dto;

import com.southernwavebank.auth_service.model.Role;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RegisterRequest {

	@NotNull(message = "Name is required")
	private String name;
	
	@NotNull(message = "Email is required")
	private String emailId;

	@NotNull(message = "Password is required")
	private String password;

	@NotNull(message = "Role is required") 
	private Role role;
	
	private String contactNumber;
}
