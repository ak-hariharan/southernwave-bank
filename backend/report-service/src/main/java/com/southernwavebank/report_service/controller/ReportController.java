package com.southernwavebank.report_service.controller;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.southernwavebank.report_service.model.externalDto.TransactionDto;
import com.southernwavebank.report_service.service.ReportService;

import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/swb/report")
@Slf4j
public class ReportController {
	
	@Autowired
	private ReportService reportService;
	
	@PostMapping("/generate")
    public String generateReport(@RequestBody List<TransactionDto> transactions){
		log.info("Received report generation operation");
		LocalDateTime time = LocalDateTime.now();
        try {
        	log.info("Processing the report in report service method");
            String filePath = reportService.generateTransactionReport(transactions, time);
            log.info("Returning the path for email attachment");
            return filePath;
        } catch (Exception e) {
        	 log.info("Failed to send path for email attachment");
            return "Error generating report: " + e.getMessage();
        }
    }
}
