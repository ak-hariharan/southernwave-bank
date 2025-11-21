package com.southernwavebank.user_service.controller;
//package com.bankofindia.user_service.controller;
//
//import com.bankofindia.user_service.model.dto.LoginRequest;
//import com.bankofindia.user_service.model.dto.UpdatePasswordRequest;
//import com.bankofindia.user_service.model.dto.UserDto;
//import com.bankofindia.user_service.model.externaldto.ForgetPasswordRequest;
//import com.bankofindia.user_service.model.externaldto.RegisterRequest;
//import com.bankofindia.user_service.model.response.Response;
//import com.bankofindia.user_service.service.UserService;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
//import org.springframework.boot.test.mock.mockito.MockitoTestExecutionListener;
//import org.springframework.boot.test.mock.mockito.MockitoTestExecutionListener.*;
//
//import org.springframework.boot.test.context.TestConfiguration;
//
//import org.springframework.context.annotation.Bean;
//import org.springframework.http.MediaType;
//import org.springframework.test.context.bean.override.mockito.MockitoBean;
//import org.springframework.test.web.servlet.MockMvc;
//
//import static org.mockito.Mockito.*;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//
//@WebMvcTest(UserController.class)
//public class UserControllerTest {
//
//    @Autowired
//    private MockMvc mockMvc;
//
//    @MockitoBean
//    private UserService userService;
//
//    @Autowired
//    private ObjectMapper objectMapper;
//
//    @Test
//    void testRegisterOfficer() throws Exception {
//        RegisterRequest registerRequest = new RegisterRequest();
//        Response mockResponse = new Response("201", "User registered successfully", null);
//
//        when(userService.registerOfficer(any(RegisterRequest.class))).thenReturn(mockResponse);
//
//        mockMvc.perform(post("/boi/users/register")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(registerRequest)))
//                .andExpect(status().isCreated())
//                .andExpect(jsonPath("$.responseCode").value("201"));
//    }
//
//    @Test
//    void testValidateUser() throws Exception {
////        UserDto userDto = new UserDto();
////        userDto.setEmailId("test@example.com");
////        userDto.setPassword("password123");
//    	
//    	LoginRequest dto = new LoginRequest();
//      dto.setEmailId("test@example.com");
//      dto.setPassword("password123");
//
//        Response response = Response.builder()
//                .responseCode("200")
//                .message("Login successful")
//                .data(null)
//                .build();
//
//        when(userService.validateUser(dto.getEmailId(), dto.getPassword()))
//                .thenReturn(response); // <--- Make sure this is returning the mocked response
//
//        mockMvc.perform(post("/boi/users/login")
//                .contentType(MediaType.APPLICATION_JSON)
//                .content(objectMapper.writeValueAsString(dto)))
//                .andExpect(status().isOk());
////                .andExpect(jsonPath("$.responseCode").value("200"))  // <--- Ensure this field exists in Response
////                .andExpect(jsonPath("$.message").value("Login successful"));
//    }
//
//
//	@Test
//	void testCheckUserExist_Success() throws Exception {
//		// Arrange
//		ForgetPasswordRequest request = new ForgetPasswordRequest();
//		request.setEmailId("user@example.com");
//
//		Response mockResponse = new Response("200", "User exists", "someData");
//
//		when(userService.checkUserExist(any(ForgetPasswordRequest.class))).thenReturn(mockResponse);
//
//		// Act & Assert
//		mockMvc.perform(post("/boi/users//forgot-password").contentType(MediaType.APPLICATION_JSON)
//				.content(objectMapper.writeValueAsString(request))).andExpect(status().isOk())
//				.andExpect(jsonPath("$.responseCode").value("200"))
//				.andExpect(jsonPath("$.message").value("User exists")).andExpect(jsonPath("$.data").value("someData"));
//
//		verify(userService, times(1)).checkUserExist(any(ForgetPasswordRequest.class));
//	}
//
//	@Test
//	void testGenerateNewPassword_Success() throws Exception {
//	    UpdatePasswordRequest request = new UpdatePasswordRequest();
//	    request.setEmailId("user@example.com");
//	    request.setNewPassword("newSecurePassword");
//
//	    Response mockResponse = new Response("200", "Password updated successfully", null);
//
//	    when(userService.generateNewPassword(any(UpdatePasswordRequest.class)))
//	            .thenReturn(mockResponse);
//
//	    mockMvc.perform(put("/boi/users/update-password")
//	                    .contentType(MediaType.APPLICATION_JSON)
//	                    .content(objectMapper.writeValueAsString(request)))
//	            .andExpect(status().isOk())
//	            .andExpect(jsonPath("$.responseCode").value("200"))
//	            .andExpect(jsonPath("$.message").value("Password updated successfully"))
//	            .andExpect(jsonPath("$.data").doesNotExist());
//
//	    verify(userService, times(1)).generateNewPassword(any(UpdatePasswordRequest.class));
//	}
//	
//    @Test
//    void testCreateUser_ForbiddenForNonOfficer() throws Exception {
//        UserDto userDto = new UserDto();
//        userDto.setEmailId("user@gmail.com");
//        userDto.setUserId(1L);
//        userDto.setUsername("Abcv");
//        userDto.setContactNumber("9875345321");
//
//
//        mockMvc.perform(post("/boi/users/create")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .header("X-User-Role", "CONSUMER")
//                        .content(objectMapper.writeValueAsString(userDto)))
//                .andExpect(status().isForbidden())
//                .andExpect(jsonPath("$.responseCode").value("403"));
//    }
//
//    @Test
//    void testCreateUser_SuccessForOfficer() throws Exception {
//        UserDto userDto = new UserDto();
//        userDto.setEmailId("user@gmail.com");
//        userDto.setUserId(1L);
//        userDto.setUsername("Abcv");
//        userDto.setContactNumber("9875345321");
//        Response mockResponse = new Response("201", "User created", userDto);
//
//        when(userService.createUser(any(UserDto.class))).thenReturn(mockResponse);
//
//        mockMvc.perform(post("/boi/users/create")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .header("X-User-Role", "OFFICER")
//                        .content(objectMapper.writeValueAsString(userDto)))
//                .andExpect(status().isCreated())
//                .andExpect(jsonPath("$.responseCode").value("201"));
//    }
//    
//    @Test
//    void testUpdateUserWithOfficerRole() throws Exception {
//        String emailId = "user@example.com";
//        UserDto userDto = new UserDto();
//        Response response = new Response("200", "User updated", userDto);
//
//        when(userService.updateUser(emailId, userDto)).thenReturn(response);
//
//        mockMvc.perform(put("/boi/users/update/{emailId}", emailId)
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .header("X-User-Role", "OFFICER")
//                        .content(objectMapper.writeValueAsString(userDto)))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.responseCode").value("200"));
//    }
//    
//    @Test
//    void testUpdateUser_NotOfficer_ShouldReturn403() throws Exception {
//        String emailId = "john@example.com";
//        UserDto userDto = new UserDto();
//
//        mockMvc.perform(put("/boi/users/update/{emailId}", emailId)
//                .contentType(MediaType.APPLICATION_JSON)
//                .header("X-User-Role", "CONSUMER")
//                .content(objectMapper.writeValueAsString(userDto)))
//                .andExpect(status().isForbidden())
//                .andExpect(jsonPath("$.responseCode").value("403"))
//                .andExpect(jsonPath("$.message").value("Only OFFICER role can create users"))
//                .andExpect(jsonPath("$.data").value("Your JWT has CONSUMER role"));
//    }
//    
//    @Test
//    void testGetRoleByEmail() throws Exception {
//    	  String emailId = "user@example.com";
//    	  
//    	  when(userService.getRoleByEmail(emailId)).thenReturn("CONSUMER");
//    	  
//    	  mockMvc.perform(get("/boi/users/role").param("email", "user@example.com"))
//					.andExpect(status().isOk())
//					.andExpect(content().string("CONSUMER"));
//		
//    	  
//    }
//
//    @Test
//    void testFetchUserById() throws Exception {
//        when(userService.fetchUserById(1L)).thenReturn(true);
//
//        mockMvc.perform(get("/boi/users/1"))
//                .andExpect(status().isOk())
//                .andExpect(content().string("true"));
//    }
//
//    @Test
//    void testGetUserId() throws Exception {
//        Response response = new Response("200", "User ID fetched", 1L);
//        when(userService.getUserId("test@example.com")).thenReturn(response);
//
//        mockMvc.perform(get("/boi/users/userId")
//                        .param("email", "test@example.com"))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.data").value(1));
//    }
//
//    @Test
//    void testGetUserEmail() throws Exception {
//        Response response = new Response("200", "Email fetched", "test@example.com");
//        when(userService.getUserEmail(1L)).thenReturn(response);
//
//        mockMvc.perform(get("/boi/users/email/1"))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.data").value("test@example.com"));
//    }
//
//    @Test
//    void testGetContactNumber() throws Exception {
//        when(userService.getContactNumber(1L)).thenReturn("9999999999");
//
//        mockMvc.perform(get("/boi/users/contactNumber/1"))
//                .andExpect(status().isOk())
//                .andExpect(content().string("9999999999"));
//    }
//    
//   
//
//
//} 
