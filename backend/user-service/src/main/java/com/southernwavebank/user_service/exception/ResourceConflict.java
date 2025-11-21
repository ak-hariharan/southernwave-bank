package com.southernwavebank.user_service.exception;

import com.southernwavebank.user_service.model.dto.UserDto;

public class ResourceConflict extends RuntimeException {
    private final UserDto userDto;

    public ResourceConflict(UserDto userDto) {
        super("Account already exists for user: ");
        this.userDto = userDto;
    }
    
    public UserDto getUserDto() {
        return userDto;
    }
}
