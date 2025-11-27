package com.southernwavebank.notification_service.utils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class FetchTimeAndDate {
	
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
  
    public static String getFormattedTime(LocalDateTime time) {
        return time.format(FORMATTER); 
    }
}
