package com.southernwavebank.notification_service.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.southernwavebank.notification_service.model.NotificationStatus;
import com.southernwavebank.notification_service.model.entity.Notification;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long>{

	boolean existsByEmailAndMessageAndNotificationStatus(String email, String message, NotificationStatus status);
	
    boolean existsByEmailAndMessageAndNotificationStatusIn(
            String email,
            String message,
            List<NotificationStatus> statuses);


    Optional<Notification> findByEmailAndMessageAndTime(String email, String message, LocalDateTime time);

}
