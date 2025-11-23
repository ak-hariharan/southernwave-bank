package com.southernwavebank.auth_service.model.externaldto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NotificationDto {
	
	private int otp;
	private String emailId;
	private Long userId;
	private LocalDateTime time;
}
