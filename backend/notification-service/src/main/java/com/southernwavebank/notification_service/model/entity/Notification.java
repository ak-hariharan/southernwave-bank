package com.southernwavebank.notification_service.model.entity;

import java.time.LocalDateTime;

import com.southernwavebank.notification_service.model.NotificationStatus;
import com.southernwavebank.notification_service.model.NotificationType;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Notification {
	
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String email;
    
    private String phoneNumber;
    
    @Lob
    private String message;
    
    @Enumerated(EnumType.STRING)
    private NotificationType notificationType; 
    
    @Enumerated(EnumType.STRING)
    private NotificationStatus notificationStatus;
    
    private LocalDateTime time;
}
