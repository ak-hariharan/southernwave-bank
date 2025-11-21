package com.southernwavebank.user_service.config;


import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.southernwavebank.user_service.model.dto.UserDto;
import com.southernwavebank.user_service.model.entity.User;

@Configuration
public class UserConfig {

	@Bean
	public ModelMapper modelMapper() {
		ModelMapper modelMapper = new ModelMapper();
		// Global config
		modelMapper.getConfiguration()
				   .setSkipNullEnabled(true) // skip nulls
				   .setMatchingStrategy(MatchingStrategies.STRICT); // safer mapping

		// 	Define mappings once
		modelMapper.typeMap(UserDto.class, User.class)
		           .addMappings(mapper -> mapper.skip(User::setUserId));
		
		return modelMapper;
	}
}
