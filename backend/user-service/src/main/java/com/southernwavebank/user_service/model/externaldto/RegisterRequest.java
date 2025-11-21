package com.southernwavebank.user_service.model.externaldto;


import com.southernwavebank.user_service.model.Role;

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
	
	@NotNull(message = "Id is required")
	private Long officerId;
	
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
