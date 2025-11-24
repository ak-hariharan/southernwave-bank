package com.southernwavebank.account_service.config;

import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.southernwavebank.account_service.model.dto.AccountDto;
import com.southernwavebank.account_service.model.entity.Account;


@Configuration
public class AccountConfig {

	@Bean
	public ModelMapper modelMapper() {
		ModelMapper modelMapper = new ModelMapper();
		// Global config
		modelMapper.getConfiguration()
				   .setSkipNullEnabled(true) // skip nulls
				   .setMatchingStrategy(MatchingStrategies.STRICT); // safer mapping

		// 	Define mappings once
		modelMapper.typeMap(AccountDto.class, Account.class)
		           .addMappings(mapper -> mapper.skip(Account::setUserId));
		
		return modelMapper;
	}

}
