package com.southernwavebank.report_service.service;

import java.security.GeneralSecurityException;
import java.time.LocalDateTime;
import java.util.List;

import com.southernwavebank.report_service.model.externalDto.TransactionDto;

public interface ReportService {
	
	String generateTransactionReport(List<TransactionDto> transactions, LocalDateTime time) throws GeneralSecurityException ; 
}
