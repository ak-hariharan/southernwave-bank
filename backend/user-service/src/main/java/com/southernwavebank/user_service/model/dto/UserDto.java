package com.southernwavebank.user_service.model.dto;

import com.southernwavebank.user_service.model.requestgroups.OnCreate;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserDto {
	
	private Long userId;
	
	@NotNull(message = "Name is required", groups = OnCreate.class)
	private String username;
	
	@NotNull(message = "Email is required", groups = OnCreate.class)
	private String emailId;
	
	@NotNull(message = "Contact Number is required", groups = OnCreate.class)
	private String contactNumber;
	
}

