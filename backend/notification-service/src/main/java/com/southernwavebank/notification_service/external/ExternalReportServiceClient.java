package com.southernwavebank.notification_service.external;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.southernwavebank.notification_service.model.externaldto.TransactionDto;


@FeignClient(name = "report-service") 
public interface ExternalReportServiceClient {

	@PostMapping("/swb/report/generate")
    public String generateReport(@RequestBody List<TransactionDto> transactions);
	
}
