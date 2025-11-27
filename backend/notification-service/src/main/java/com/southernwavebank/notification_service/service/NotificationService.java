package com.southernwavebank.notification_service.service;

import java.time.LocalDateTime;
import java.util.List;

import com.southernwavebank.notification_service.model.dto.NotificationDto;
import com.southernwavebank.notification_service.model.externaldto.TransactionDto;

public interface NotificationService {
	void sendEmail(NotificationDto notificationDto, String eventType);
	
	void sendEmail(List<TransactionDto> transactions, LocalDateTime time); 
}
