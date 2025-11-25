package com.southernwavebank.transaction_service.config;

import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.southernwavebank.transaction_service.model.dto.TransactionDto;
import com.southernwavebank.transaction_service.model.entity.Transaction;

@Configuration
public class TransactionConfig {
	
	@Bean
	public ModelMapper modelMapper() {
		ModelMapper modelMapper = new ModelMapper();
		// Global config
		modelMapper.getConfiguration()
				   .setSkipNullEnabled(true) // skip nulls
				   .setMatchingStrategy(MatchingStrategies.STRICT); // safer mapping

		// 	Define mappings once
		modelMapper.typeMap(TransactionDto.class, Transaction.class)
		           .addMappings(mapper -> mapper.skip(Transaction::setTransactionId));
		
		return modelMapper;
	}
}