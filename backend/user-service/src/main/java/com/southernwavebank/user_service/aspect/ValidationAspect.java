package com.southernwavebank.user_service.aspect;

import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

import com.southernwavebank.user_service.model.dto.UserDto;

import lombok.extern.slf4j.Slf4j;

@Aspect
@Component
@Slf4j
public class ValidationAspect {
	
	@Before("@annotation(com.bankofindia.user_service.annotations.ValidateUserDto) && args(userDto)")
	public void validateUserDto(UserDto userDto) {

		log.info("Checking the data validation");
    	
        // Username validation
		if (!userDto.getUsername().matches("^[A-Z][a-z]{2,9}$")) {
			throw new IllegalArgumentException( userDto.getUsername() +
					"|Username must start with a capital letter and have 3–10 alphabetic characters only");
		}

        // Email validation using regex
		if (!userDto.getEmailId().matches("^[A-Za-z0-9._%+-]{5,64}@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$")) {
			throw new IllegalArgumentException( userDto.getEmailId() + "|Email ID is not valid");
		}

        // Contact number: exactly 10 digits
		if (userDto.getContactNumber() == null || !userDto.getContactNumber().matches("\\d{10}")) {
			throw new IllegalArgumentException( userDto.getContactNumber() + "|Contact number must be exactly 10 digits");
		}

	}
	
}



