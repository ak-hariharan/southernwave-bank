package com.southernwavebank.report_service.controller;

import static org.hamcrest.CoreMatchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.southernwavebank.report_service.controller.ReportController;
import com.southernwavebank.report_service.model.externalDto.TransactionDto;
import com.southernwavebank.report_service.service.ReportService;

@WebMvcTest(ReportController.class)
public class ReportControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ReportService reportService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testGenerateReport_success() throws Exception {
        List<TransactionDto> transactions = List.of(new TransactionDto());
        String mockPath = "/files/txn_report.pdf";

        when(reportService.generateTransactionReport(anyList(), any(LocalDateTime.class)))
                .thenReturn(mockPath);

        mockMvc.perform(post("/boi/report/generate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(transactions)))
                .andExpect(status().isOk())
                .andExpect(content().string(mockPath));
    }

    @Test
    void testGenerateReport_failure() throws Exception {
        List<TransactionDto> transactions = List.of(new TransactionDto());

        when(reportService.generateTransactionReport(anyList(), any(LocalDateTime.class)))
                .thenThrow(new RuntimeException("PDF generation failed"));

        mockMvc.perform(post("/boi/report/generate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(transactions)))
                .andExpect(status().isOk()) // Controller still returns 200 with error message in body
                .andExpect(content().string(containsString("Error generating report: PDF generation failed")));
    }
}

