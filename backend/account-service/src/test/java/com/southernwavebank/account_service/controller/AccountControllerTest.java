package com.southernwavebank.account_service.controller;
//package com.bankofindia.account_service.controller;
//
//import com.bankofindia.account_service.model.AccountStatus;
//import com.bankofindia.account_service.model.externaldto.TransactionDto;
//import com.bankofindia.account_service.model.externaldto.UserDto;
//import com.bankofindia.account_service.model.response.Response;
//import com.bankofindia.account_service.service.AccountService;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
//import org.springframework.test.context.TestPropertySource;
//import org.springframework.test.context.bean.override.mockito.MockitoBean;
//import org.springframework.test.web.servlet.MockMvc;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.MediaType;
//
//import java.math.BigDecimal;
//import java.time.LocalDateTime;
//
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.ArgumentMatchers.eq;
//import static org.mockito.Mockito.when;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
//
//@WebMvcTest(AccountController.class)
//@TestPropertySource(properties = {
//    "spring.application.ok=200",
//    "spring.application.created=201",
//    "spring.application.forbidden=403",
//    "spring.application.bad_request=400",
//    "spring.application.internal_server_error=500"
//})
//public class AccountControllerTest {
//
//    @Autowired
//    private MockMvc mockMvc;
//
//    @MockitoBean
//    private AccountService accountService;
//
//    @Autowired
//    private ObjectMapper objectMapper;
//    
//    @Value("${spring.application.ok:SUCCESS}")
//    private String success;
//    
//    @Value("${spring.application.created:CREATED}")
//    private String created;
//
//    @Value("${spring.application.forbidden:FORBIDDEN}")
//    private String forbidden;
//
//    @Value("${spring.application.bad_request:BAD_REQUEST}")
//    private String bad_request;
//
//    @Value("${spring.application.internal_server_error:INTERNAL_SERVER_ERROR}")
//    private String internal_server_error;
//
//    @Test
//    void testCreateAccount_WithOfficerRole_ReturnsCreated() throws Exception {
//      
//        UserDto userDto = new UserDto();
//        userDto.setUserId(1L);
//        userDto.setUsername("John");
//        userDto.setEmailId("john@example.com");
//        userDto.setContactNumber("1234567890");
//
//        Response mockResponse = Response.builder()
//                .responseCode(created)
//                .message("Account created successfully")
//                .data("Dummy account")
//                .build();
//
//        when(accountService.createAccount(userDto, LocalDateTime.now()))
//                .thenReturn(mockResponse);
//
//        mockMvc.perform(post("/boi/account/create")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .header("X-User-Role", "OFFICER")
//                        .content(objectMapper.writeValueAsString(userDto)))
//                .andExpect(status().isCreated());
////                .andExpect(jsonPath("$.responseCode").value("201"))
////                .andExpect(jsonPath("$.message").value("Account created successfully"))
//             
//    }
//    
//    @Test
//    void testCreateAccount_WithNonOfficerRole_ReturnsCreated() throws Exception {
//    	 UserDto userDto = new UserDto();
//         userDto.setUserId(1L);
//         userDto.setUsername("John");
//         userDto.setEmailId("john@example.com");
//         userDto.setContactNumber("1234567890");
//
//         mockMvc.perform(post("/boi/account/create")
//                 .contentType(MediaType.APPLICATION_JSON)
//                 .header("X-User-Role", "USER")  // Non-officer role
//                 .content(objectMapper.writeValueAsString(userDto)))
//         .andExpect(status().isForbidden())  // Expect status 403 (Forbidden)
//         .andExpect(jsonPath("$.responseCode").value(forbidden))  // Assert the responseCode matches 'FORBIDDEN'
//         .andExpect(jsonPath("$.message").value("Only OFFICER role can create users"))  // Assert the message
//         .andExpect(jsonPath("$.data").value("Your JWT has USER role"));  // Assert the data matches the role in the header
//
//    }
//    
//    
// // 1. Case: userDto is null
//    @Test
//    void testCreateAccount_WithNullUserDto_ReturnsBadRequest() throws Exception {
//        mockMvc.perform(post("/boi/account/create")
//                .contentType(MediaType.APPLICATION_JSON)
//                .header("X-User-Role", "OFFICER"))
//            .andExpect(status().isBadRequest())
//            .andExpect(jsonPath("$.message").value("Invalid user data"));
//    }
//
//    
//
//    // 2. Case: userDto.username is null
//    @Test
//    void testCreateAccount_WithNullUsername_ReturnsBadRequest() throws Exception {
//        UserDto userDto = new UserDto();
//        userDto.setUserId(1L);
//        userDto.setUsername(null); // Username is null
//        userDto.setEmailId("john@example.com");
//        userDto.setContactNumber("1234567890");
//
//        mockMvc.perform(post("/boi/account/create")
//                .contentType(MediaType.APPLICATION_JSON)
//                .header("X-User-Role", "OFFICER")
//                .content(objectMapper.writeValueAsString(userDto)))
//            .andExpect(status().isBadRequest())
//            .andExpect(jsonPath("$.message").value("Invalid user data"));
//    }
//    
//    @Test
//    void testCreateAccount_WithOnlyUsername_ReturnsBadRequest() throws Exception {
//        UserDto userDto = new UserDto();
//        userDto.setUserId(1L);
//        userDto.setUsername("john"); // Only Username is given 
//
//        mockMvc.perform(post("/boi/account/create")
//                .contentType(MediaType.APPLICATION_JSON)
//                .header("X-User-Role", "OFFICER")
//                .content(objectMapper.writeValueAsString(userDto)))
//            .andExpect(status().isBadRequest())
//            .andExpect(jsonPath("$.message").value("Invalid user data"));
//    }
//
//    // 3. Case: userDto.emailId is null
//    @Test
//    void testCreateAccount_WithNullEmail_ReturnsBadRequest() throws Exception {
//        UserDto userDto = new UserDto();
//        userDto.setUserId(1L);
//        userDto.setUsername("John");
//        userDto.setEmailId(null); // Email is null
//        userDto.setContactNumber("1234567890");
//
//        mockMvc.perform(post("/boi/account/create")
//                .contentType(MediaType.APPLICATION_JSON)
//                .header("X-User-Role", "OFFICER")
//                .content(objectMapper.writeValueAsString(userDto)))
//            .andExpect(status().isBadRequest())
//            .andExpect(jsonPath("$.message").value("Invalid user data"));
//    }
//    
//    @Test
//    void testCreateAccount_WithOnlyEmail_ReturnsBadRequest() throws Exception {
//        UserDto userDto = new UserDto();
//        userDto.setUserId(1L);
//        userDto.setUsername(null);
//        userDto.setEmailId("john@example.com"); // Only Email is present
//        userDto.setContactNumber(null);
//
//        mockMvc.perform(post("/boi/account/create")
//                .contentType(MediaType.APPLICATION_JSON)
//                .header("X-User-Role", "OFFICER")
//                .content(objectMapper.writeValueAsString(userDto)))
//            .andExpect(status().isBadRequest())
//            .andExpect(jsonPath("$.message").value("Invalid user data"));
//    }
//    
//    @Test
//    void testCreateAccount_WhenExceptionThrown_ReturnsInternalServerError() throws Exception {
//        UserDto userDto = new UserDto();
//        userDto.setUserId(1L);
//        userDto.setUsername("John");
//        userDto.setEmailId("john@example.com");
//        userDto.setContactNumber("1234567890");
//
//        // Simulate service throwing an exception
//        when(accountService.createAccount(any(UserDto.class), any(LocalDateTime.class)))
//                .thenThrow(new RuntimeException("Something went wrong"));
//
//        mockMvc.perform(post("/boi/account/create")
//                .contentType(MediaType.APPLICATION_JSON)
//                .header("X-User-Role", "OFFICER")
//                .content(objectMapper.writeValueAsString(userDto)))
//            .andExpect(status().isInternalServerError())
//            .andExpect(jsonPath("$.message").value("An unexpected error occurred"))
//            .andExpect(jsonPath("$.responseCode").value(internal_server_error)) // assuming internal_server_error = 500
//            .andExpect(jsonPath("$.data").isEmpty());
//    }
//
//    @Test
//    void testGetAccountDetails_Success() throws Exception {
//        // Arrange
//        String accountNumber = "123456";
//        String role = "OFFICER"; // example role
//        String userId = "1"; // optional userId
//
//        Response mockResponse = new Response();
//        mockResponse.setResponseCode("200");
//        mockResponse.setMessage("Account details fetched successfully");
//        mockResponse.setData("7876543123456"); // Add appropriate data
//
//        // Mocking the accountService method call
//       when(accountService.getAccountDetails(accountNumber, role, userId)).thenReturn(mockResponse);
//
//        // Act & Assert
//        mockMvc.perform(get("/boi/account/details/{accountNumber}", accountNumber)
//                .header("X-User-Role", role)
//                .header("X-User-Id", userId)) // Optional, test both with and without
//            .andExpect(status().isOk()) // Expecting HTTP 200 OK
//            .andExpect(jsonPath("$.responseCode").value(success))
//            .andExpect(jsonPath("$.message").value("Account details fetched successfully"));
//    }
//    
//    @Test
//    void testUpdateAccountStatus_UnauthorizedRole_ReturnsForbidden() throws Exception {
//        // Arrange
//        String accountNumber = "123456";
//        AccountStatus status = AccountStatus.ACTIVE; // Or any valid AccountStatus
//        String role = "USER"; // Unauthorized role
//
//        // Act & Assert
//        mockMvc.perform(put("/boi/account/update/{accountNumber}", accountNumber)
//                .param("status", status.toString()) // Send AccountStatus as a request parameter
//                .header("X-User-Role", role)) // Add the role header
//            .andExpect(status().isForbidden()) // Expecting HTTP 403 Forbidden
//            .andExpect(jsonPath("$.responseCode").value(forbidden))
//            .andExpect(jsonPath("$.message").value("Only OFFICER role can update the account status"))
//            .andExpect(jsonPath("$.data").value("Your JWT has USER role"));
//    }
//    
//    @Test
//    void testUpdateAccountStatus_Success_ReturnsOk() throws Exception {
//        // Arrange
//        String accountNumber = "123456";
//        AccountStatus status = AccountStatus.ACTIVE; // Or any valid AccountStatus
//        String role = "OFFICER"; // Valid role
//
//        // Mocking the service call
//        Response mockResponse = new Response();
//        mockResponse.setResponseCode("200");
//        mockResponse.setMessage("Account status updated successfully");
//        mockResponse.setData("Account number: 123456, Status: ACTIVE");
//        when(accountService.updateAccountStatus(accountNumber, status)).thenReturn(mockResponse);
//
//        // Act & Assert
//        mockMvc.perform(put("/boi/account/update/{accountNumber}", accountNumber)
//                .param("status", status.toString()) // Send AccountStatus as a request parameter
//                .header("X-User-Role", role)) // Add the role header
//            .andExpect(status().isOk()) // Expecting HTTP 200 OK
//            .andExpect(jsonPath("$.responseCode").value(success))
//            .andExpect(jsonPath("$.message").value("Account status updated successfully"))
//            .andExpect(jsonPath("$.data").value("Account number: 123456, Status: ACTIVE"));
//    }
//    
//    @Test
//    void testGetBalance_WithValidOfficerRole_ReturnsOk() throws Exception {
//        String accountNumber = "123456";
//        String role = "OFFICER";
//        String userId = null;
//
//        Response mockResponse = Response.builder()
//                .responseCode("200")
//                .message("Balance fetched")
//                .data(5000.0) // example balance
//                .build();
//
//        when(accountService.getBalance(eq(accountNumber), any(LocalDateTime.class), eq(role), eq(userId)))
//                .thenReturn(mockResponse);
//
//        mockMvc.perform(get("/boi/account/balance/{accountNumber}", accountNumber)
//                .header("X-User-Role", role))
//            .andExpect(status().isOk())
//            .andExpect(jsonPath("$.responseCode").value(success))
//            .andExpect(jsonPath("$.message").value("Balance fetched"))
//            .andExpect(jsonPath("$.data").value(5000.0));
//    }
//
//
//    @Test
//    void testGetBalance_WithConsumerRole_ReturnsOk() throws Exception {
//        String accountNumber = "123456";
//        String role = "CONSUMER";
//        String userId = "2";
//
//        Response mockResponse = Response.builder()
//                .responseCode("200")
//                .message("Balance fetched")
//                .data(1500.0)
//                .build();
//
//        when(accountService.getBalance(eq(accountNumber), any(LocalDateTime.class), eq(role), eq(userId)))
//                .thenReturn(mockResponse);
//
//        mockMvc.perform(get("/boi/account/balance/{accountNumber}", accountNumber)
//                .header("X-User-Role", role)
//                .header("X-User-Id", userId))
//            .andExpect(status().isOk())
//            .andExpect(jsonPath("$.responseCode").value(success))
//            .andExpect(jsonPath("$.message").value("Balance fetched"))
//            .andExpect(jsonPath("$.data").value(1500.0));
//    }
//    
//    @Test
//    void testDepositMoney_SuccessfulDeposit_ReturnsOk() throws Exception {
//        TransactionDto transaction = new TransactionDto();
//        transaction.setAccountNumber("123456");
//        transaction.setAmount(new BigDecimal("1000.00")); 
//
//        String role = "CONSUMER";
//        String userId = "1";
//
//        Response mockResponse = Response.builder()
//                .responseCode("200")
//                .message("Deposit successful")
//                .data(transaction)
//                .build();
//
//        when(accountService.depositMoney(eq(transaction), any(LocalDateTime.class), eq(role), eq(userId)))
//                .thenReturn(mockResponse);
//
//        mockMvc.perform(post("/boi/account/deposit")
//                .contentType(MediaType.APPLICATION_JSON)
//                .content(objectMapper.writeValueAsString(transaction))
//                .header("X-User-Role", role)
//                .header("X-User-Id", userId))
//            .andExpect(status().isOk())
//            .andExpect(jsonPath("$.responseCode").value(success))
//            .andExpect(jsonPath("$.message").value("Deposit successful"))
//            .andExpect(jsonPath("$.data.accountNumber").value("123456"))
//            .andExpect(jsonPath("$.data.amount").value(1000.0));
//    }
//
//    @Test
//    void testWithdrawMoney_ValidRequest_ReturnsOk() throws Exception {
//        TransactionDto transactionDto = new TransactionDto();
//        transactionDto.setAccountNumber("1234567890");
//        transactionDto.setAmount(new BigDecimal("500.00"));  // Ensure BigDecimal is used
//
//        Response expectedResponse = Response.builder()
//                .responseCode("200")
//                .message("Withdrawal successful")
//                .data(transactionDto)
//                .build();
//
//        when(accountService.withdrawMoney(
//                any(TransactionDto.class),
//                any(LocalDateTime.class),
//                eq("CONSUMER"),
//                eq("user123")
//        )).thenReturn(expectedResponse);
//
//        mockMvc.perform(post("/boi/account/withdraw")
//                .contentType(MediaType.APPLICATION_JSON)
//                .header("X-User-Role", "CONSUMER")
//                .header("X-User-Id", "user123")
//                .content(objectMapper.writeValueAsString(transactionDto)))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.responseCode").value(success))
//                .andExpect(jsonPath("$.message").value("Withdrawal successful"));
//    }
//
//    @Test
//    void testGetUserId_ReturnsUserId() throws Exception {
//        String accountNumber = "1234567890";
//        Long userId = 1001L;
//
//        when(accountService.getUserId(accountNumber)).thenReturn(userId);
//
//        mockMvc.perform(get("/boi/account/userId/{accountNumber}", accountNumber))
//               .andExpect(status().isOk())
//               .andExpect(content().string(userId.toString()));
//    }
//
//    @Test
//    void testFetchAccountNumberByUserId_ReturnsAccountNumber() throws Exception {
//        Long userId = 1001L;
//        String accountNumber = "1234567890";
//
//        when(accountService.fetchAccountNumberByUserId(userId)).thenReturn(accountNumber);
//
//        mockMvc.perform(get("/boi/account/accountnumber/{userId}", userId))
//               .andExpect(status().isOk())
//               .andExpect(content().string(accountNumber));
//    }
//
//    
//    @Test
//    void testCheckAccountNumberExist_ReturnsTrue() throws Exception {
//        String accountNumber = "1234567890";
//
//        when(accountService.checkAccountNumberExist(accountNumber)).thenReturn(true);
//
//        mockMvc.perform(get("/boi/account/{accountNumber}", accountNumber))
//               .andExpect(status().isOk())
//               .andExpect(content().string("true"));
//    }
//
//}
