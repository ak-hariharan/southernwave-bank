package com.southernwavebank.user_service.model.externaldto;

import java.time.LocalDateTime;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NotificationDto {
	
	private String username;
	
	private String emailId;
	
	private String contactNumber;
	
	private LocalDateTime time;
}
