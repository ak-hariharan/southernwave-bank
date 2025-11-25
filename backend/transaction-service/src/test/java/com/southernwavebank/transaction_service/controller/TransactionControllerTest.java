package com.southernwavebank.transaction_service.controller;
//package com.bankofindia.transaction_service.controller;
//
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.ArgumentMatchers.eq;
//import static org.mockito.Mockito.when;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
//
//import java.time.LocalDateTime;
//import java.util.List;
//
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
//import org.springframework.http.MediaType;
//import org.springframework.test.context.bean.override.mockito.MockitoBean;
//import org.springframework.test.web.servlet.MockMvc;
//
//import com.bankofindia.transaction_service.model.dto.TransactionDto;
//import com.bankofindia.transaction_service.model.response.Response;
//import com.bankofindia.transaction_service.service.TransactionService;
//import com.fasterxml.jackson.databind.ObjectMapper;
//
//@WebMvcTest(TransactionController.class)
//public class TransactionControllerTest {
//
//    @Autowired
//    private MockMvc mockMvc;
//
//    @MockitoBean
//    private TransactionService transactionService;
//
//    @Autowired
//    private ObjectMapper objectMapper;
//
//    @Test
//    void testDepositMoney() throws Exception {
//        TransactionDto transactionDto = new TransactionDto(); // Set fields as needed
//        Response mockResponse = new Response("200", "Deposit successful", null);
//
//        when(transactionService.depositMoney(any(TransactionDto.class))).thenReturn(mockResponse);
//
//        mockMvc.perform(post("/boi/transaction/deposit")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(transactionDto)))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.responseCode").value("200"))
//                .andExpect(jsonPath("$.message").value("Deposit successful"));
//    }
//
//    @Test
//    void testWithdrawMoney() throws Exception {
//        TransactionDto transactionDto = new TransactionDto(); // Set fields as needed
//        Response mockResponse = new Response("200", "Withdrawal successful", null);
//
//        when(transactionService.withdrawMoney(any(TransactionDto.class))).thenReturn(mockResponse);
//
//        mockMvc.perform(post("/boi/transaction/withdraw")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(transactionDto)))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.responseCode").value("200"))
//                .andExpect(jsonPath("$.message").value("Withdrawal successful"));
//    }
//
//    @Test
//    void testGetTransactions_withoutDateRange() throws Exception {
//        String accountNumber = "ACC123";
//        Response mockResponse = new Response("200", "History fetched", List.of());
//
//        when(transactionService.fetchTransactionsOfAccount(eq(accountNumber), eq("CONSUMER"), eq("123")))
//                .thenReturn(mockResponse);
//
//        mockMvc.perform(get("/boi/transaction/history/{accountNumber}", accountNumber)
//                        .header("X-User-Role", "CONSUMER")
//                        .header("X-User-Id", "123"))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.responseCode").value("200"))
//                .andExpect(jsonPath("$.message").value("History fetched"));
//    }
//
//    @Test
//    void testGetTransactions_withDateRange() throws Exception {
//        String accountNumber = "ACC123";
//        String start = "2024-01-01T00:00:00";
//        String end = "2024-12-31T23:59:59";
//        Response mockResponse = new Response("200", "History with filter", List.of());
//
//        when(transactionService.fetchTransactionsOfAccount(
//                eq(accountNumber),
//                any(LocalDateTime.class),
//                any(LocalDateTime.class),
//                eq("OFFICER"),
//                eq("1001")))
//                .thenReturn(mockResponse);
//
//        mockMvc.perform(get("/boi/transaction/history/{accountNumber}", accountNumber)
//                        .param("startDate", start)
//                        .param("endDate", end)
//                        .header("X-User-Role", "OFFICER")
//                        .header("X-User-Id", "1001"))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.responseCode").value("200"))
//                .andExpect(jsonPath("$.message").value("History with filter"));
//    }
//}
