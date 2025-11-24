package com.southernwavebank.account_service.model.externaldto;

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
	
	private String username;
	
	private String emailId;
	
	private String contactNumber;
}