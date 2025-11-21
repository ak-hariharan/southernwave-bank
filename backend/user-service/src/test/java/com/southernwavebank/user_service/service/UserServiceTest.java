package com.southernwavebank.user_service.service;
//package com.bankofindia.user_service.service;
//
//
//import com.bankofindia.user_service.exception.DuplicateEmailException;
//import com.bankofindia.user_service.exception.ResourceNotFound;
//import com.bankofindia.user_service.kafka.AccountCreatedEventProducer;
//import com.bankofindia.user_service.kafka.PasswordUpdationEventProducer;
//import com.bankofindia.user_service.kafka.UserEventProducer;
//import com.bankofindia.user_service.model.Role;
//import com.bankofindia.user_service.model.dto.ConsumerDto;
//import com.bankofindia.user_service.model.dto.UpdatePasswordRequest;
//import com.bankofindia.user_service.model.dto.UserDto;
//import com.bankofindia.user_service.model.entity.Officer;
//import com.bankofindia.user_service.model.entity.User;
//import com.bankofindia.user_service.model.externaldto.ForgetPasswordRequest;
//import com.bankofindia.user_service.model.externaldto.NotificationDto;
//import com.bankofindia.user_service.model.externaldto.RegisterRequest;
//import com.bankofindia.user_service.model.response.Response;
//import com.bankofindia.user_service.repository.OfficerRepository;
//import com.bankofindia.user_service.repository.UserRepository;
//import com.bankofindia.user_service.serviceimpl.UserServiceImpl;
//
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.modelmapper.ModelMapper;
//import org.modelmapper.PropertyMap;
//import org.modelmapper.TypeMap;
//import org.mockito.ArgumentCaptor;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//import org.mockito.junit.jupiter.MockitoSettings;
//import org.mockito.quality.Strictness;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.security.crypto.password.PasswordEncoder;
//
//import java.lang.reflect.Field;
//import java.time.LocalDate;
//import java.time.format.DateTimeFormatter;
//import java.util.Optional;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.Mockito.*;
//
//import org.modelmapper.ModelMapper;
//import org.modelmapper.config.Configuration;
//import org.modelmapper.TypeMap;
//import org.modelmapper.PropertyMap;
//
//@ExtendWith(MockitoExtension.class)
//@MockitoSettings(strictness = Strictness.LENIENT)
//public class UserServiceTest {
//
//    @InjectMocks
//    private UserServiceImpl userService;
//
//    @Mock
//    private OfficerRepository officerRepo;
//
//    @Mock
//    private UserRepository userRepo;
//
//    @Mock
//    private PasswordEncoder passwordEncoder;
//
//    @Mock
//    private ModelMapper modelMapper;
//
//    @Mock
//    private UserEventProducer userEventProducer;
//
//    @Mock
//    private AccountCreatedEventProducer accountCreatedEventProducer;
//    
//    @Mock
//    private PasswordUpdationEventProducer passwordUpdateEvent;
//    
//    @Mock
//    private TypeMap<UserDto, User> typeMap;
//
//    @BeforeEach
//    void setUp() throws Exception {
//        Field successField = UserServiceImpl.class.getDeclaredField("success");
//        successField.setAccessible(true);
//        successField.set(userService, "200");
//
//        Field conflictField = UserServiceImpl.class.getDeclaredField("conflict");
//        conflictField.setAccessible(true);
//        conflictField.set(userService, "409");
//        
//        Field badRequestField = UserServiceImpl.class.getDeclaredField("bad_request");
//        badRequestField.setAccessible(true);
//        badRequestField.set(userService, "400");
//        
//        Field notFoundField = UserServiceImpl.class.getDeclaredField("not_found");
//        notFoundField.setAccessible(true);
//        notFoundField.set(userService, "404");
//        
//        Field internalServerErrorField = UserServiceImpl.class.getDeclaredField("internal_server_error");
//        internalServerErrorField.setAccessible(true);
//        internalServerErrorField.set(userService, "500");
//        
//    }
//
//    //-------------------------registerOfficer() tests----------------------------------------------
//
//    @Test
//    void testRegisterOfficer_EmailAlreadyExists() {
//        RegisterRequest request = new RegisterRequest();
//        request.setEmailId("already@exists.com");
//        request.setRole(Role.OFFICER);
//
//        when(officerRepo.existsByEmailId(request.getEmailId())).thenReturn(true);
//
//        Response response = userService.registerOfficer(request);
//
//        assertEquals("409", response.getResponseCode());
//        assertEquals("Email already exists", response.getMessage());
//        assertNull(response.getData());
//        verify(officerRepo, never()).save(any());
//    }
//    
//    @Test
//    void testRegisterOfficer_SuccessfullySavesOfficer() {
//        RegisterRequest request = new RegisterRequest();
//        request.setEmailId("officer@bank.com");
//        request.setRole(Role.OFFICER);
//        request.setPassword("rawpass");
//
//        Officer mappedUser = new Officer();
//        mappedUser.setEmailId(request.getEmailId());
//
//        when(officerRepo.existsByEmailId(request.getEmailId())).thenReturn(false);
//        when(passwordEncoder.encode("rawpass")).thenReturn("encodedPass");
////        when(modelMapper.map(request, Officer.class)).thenReturn(mappedUser);
//
//        Response response = userService.registerOfficer(request);
//
//        assertEquals("200", response.getResponseCode());
//        assertEquals("User created successfully", response.getMessage());
//        assertEquals(request, response.getData());
//
//        // ✅ ArgumentCaptor for deep value matching
//        ArgumentCaptor<Officer> officerCaptor = ArgumentCaptor.forClass(Officer.class);
//        verify(officerRepo).save(officerCaptor.capture());
//        Officer savedOfficer = officerCaptor.getValue();
//
//        assertEquals("officer@bank.com", savedOfficer.getEmailId());
//        assertEquals("encodedPass", savedOfficer.getPassword());
//        assertEquals(Role.OFFICER, savedOfficer.getRole());
//    }
//
//    @Test
//    void testRegisterOfficer_InvalidRole_Consumer() {
//        RegisterRequest request = new RegisterRequest();
//        request.setEmailId("consumer@user.com");
//        request.setRole(Role.CONSUMER);
//
//        Response response = userService.registerOfficer(request);
//
//        assertEquals("409", response.getResponseCode());
//        assertEquals("Registration is only for Officer role", response.getMessage());
//        assertEquals(request, response.getData());
//    }
//
//    //-------------------------createUser() tests----------------------------------------------
//
//    @Test
//    void testCreateUser_EmailAlreadyExists() {
//        UserDto userDto = new UserDto();
//        userDto.setEmailId("existing@user.com");
//
//        when(userRepo.existsByEmailId(userDto.getEmailId())).thenReturn(true);
//
//        Response response = userService.createUser(userDto);
//
//        assertEquals("409", response.getResponseCode());
//        assertEquals("Email already exists", response.getMessage());
//    }
//
//    @Test
//    void testCreateUser_Success() {
//        UserDto userDto = new UserDto();
//        userDto.setUsername("john");
//        userDto.setEmailId("john@bank.com");
//        userDto.setContactNumber("9876543210");
//
//        when(userRepo.existsByEmailId(userDto.getEmailId())).thenReturn(false);
//
//        // Password logic based on username and contact
//        String expectedTempPassword = "john" + LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")) + "@10";
//        String encodedPassword = "encodedPassword123";
//        when(passwordEncoder.encode(expectedTempPassword)).thenReturn(encodedPassword);
//
//        // Mocking the modelMapper typeMap and addMappings
//        TypeMap<UserDto, User> typeMapMock = mock(TypeMap.class);
//
//        // Explicitly mocking the behavior of the modelMapper's typeMap call
//        when(modelMapper.typeMap(eq(UserDto.class), eq(User.class))).thenReturn(typeMapMock);
//
//        // Handle the 'addMappings' method properly by skipping it for now, focusing on the actual mapping logic
//        when(typeMapMock.addMappings(any(PropertyMap.class))).thenReturn(null);  // No operation on addMappings()
//
//        // Now mock the modelMapper.map() call manually
//        doAnswer(invocation -> {
//            UserDto source = invocation.getArgument(0);
//            User destination = invocation.getArgument(1);
//            destination.setUsername(source.getUsername());
//            destination.setEmailId(source.getEmailId());
//            destination.setContactNumber(source.getContactNumber());
//            return null;
//        }).when(modelMapper).map(eq(userDto), any(User.class));
//
//        // Create the user that will be saved in the repository
//        User savedUser = new User();
//        savedUser.setUsername(userDto.getUsername());
//        savedUser.setEmailId(userDto.getEmailId());
//        savedUser.setContactNumber(userDto.getContactNumber());
//        savedUser.setPassword(encodedPassword);
//        savedUser.setRole(Role.CONSUMER);
//
//        when(userRepo.save(any(User.class))).thenReturn(savedUser);
//
//        // Call the method under test
//        Response response = userService.createUser(userDto);
//
//        // Validate the response
//        assertEquals("200", response.getResponseCode());
//        assertEquals("User created successfully", response.getMessage());
//
//        ConsumerDto responseData = (ConsumerDto) response.getData();
//        assertEquals(userDto.getEmailId(), responseData.getEmailId());
//        assertEquals(userDto.getUsername(), responseData.getUsername());
//        assertEquals(userDto.getContactNumber(), responseData.getContactNumber());
//
//        // Verify interactions with mocks
//        verify(userRepo).existsByEmailId(userDto.getEmailId());
//        verify(passwordEncoder).encode(expectedTempPassword);
//        verify(userRepo).save(any(User.class));
//        verify(userEventProducer).sendUserCreatedEvent(any(NotificationDto.class));
//        verify(accountCreatedEventProducer).sendAccountCreatedEvent(userDto);
//    }
//    
//    //----------------------getRoleByEmail()--------------------------------------------
//    @Test
//    void testGetRoleByEmail_officerExists_returnsOfficer() {
//        String email = "officer@example.com";
//        when(officerRepo.findByEmailId(email)).thenReturn(Optional.of(new Officer()));
//        
//        String role = userService.getRoleByEmail(email);
//
//        assertEquals("OFFICER", role);
//    }
//
//    @Test
//    void testGetRoleByEmail_userExists_returnsConsumer() {
//        String email = "user@example.com";
//        when(officerRepo.findByEmailId(email)).thenReturn(Optional.empty());
//        when(userRepo.findByEmailId(email)).thenReturn(Optional.of(new User()));
//        
//        String role = userService.getRoleByEmail(email);
//
//        assertEquals("CONSUMER", role);
//    }
//
//    @Test
//    void testGetRoleByEmail_notFound_returnsNone() {
//        String email = "ghost@example.com";
//        when(officerRepo.findByEmailId(email)).thenReturn(Optional.empty());
//        when(userRepo.findByEmailId(email)).thenReturn(Optional.empty());
//        
//        String role = userService.getRoleByEmail(email);
//
//        assertEquals("NONE", role);
//    }
//
//    
//    
//    
//    //-----------------------fetchUserId()----------------------------------------------
//
//    @Test
//    void testFetchUserById_UserExists() {
//        // Arrange
//        Long userId = 1L;
//        when(userRepo.existsById(userId)).thenReturn(true);  // Mock repository call
//
//        // Act
//        boolean result = userService.fetchUserById(userId);
//
//        // Assert
//        assertTrue(result);  // The method should return true if user exists
//
//        // Verify repository interaction
//        verify(userRepo).existsById(userId);
//    }
//    
//    @Test
//    void testFetchUserById_UserDoesNotExist() {
//        // Arrange
//        Long userId = 1L;
//        when(userRepo.existsById(userId)).thenReturn(false);  // Mock repository call
//
//        // Act
//        boolean result = userService.fetchUserById(userId);
//
//        // Assert
//        assertFalse(result);  // The method should return false if user does not exist
//
//        // Verify repository interaction
//        verify(userRepo).existsById(userId);
//    }
//
//    //-----------------------------updateUser()----------------------------------
//    
//    @Test
//    void testUpdateUser_UserNotFound() {
//        String emailId = "notfound@bank.com";
//        UserDto userDto = new UserDto();
//
//        when(userRepo.findByEmailId(emailId)).thenReturn(Optional.empty());
//
//        ResourceNotFound ex = assertThrows(ResourceNotFound.class, () -> {
//            userService.updateUser(emailId, userDto);
//        });
//
//        assertEquals("User not found for: "+emailId, ex.getMessage());
//    }
//    
//    @Test
//    void testUpdateUser_DuplicateEmailExists() {
//        String currentEmail = "john@bank.com";
//        String newEmail = "jane@bank.com";
//
//        User existingUser = new User();
//        existingUser.setUserId(123L);
//        existingUser.setEmailId(currentEmail);
//
//        User anotherUser = new User(); // another user with same newEmail
//        anotherUser.setUserId(456L);
//        anotherUser.setEmailId(newEmail);
//
//        UserDto userDto = new UserDto();
//        userDto.setEmailId(newEmail); // trying to update to this email
//
//        when(userRepo.findByEmailId(currentEmail)).thenReturn(Optional.of(existingUser));
//        when(userRepo.findByEmailId(newEmail)).thenReturn(Optional.of(anotherUser));
//
//        assertThrows(DuplicateEmailException.class, () -> {
//            userService.updateUser(currentEmail, userDto);
//        });
//    }
//
//    @Test
//    void testUpdateUser_SuccessWithEmailChange() {
//        String currentEmail = "john@bank.com";
//        String newEmail = "newjohn@bank.com";
//
//        User existingUser = new User();
//        existingUser.setUserId(123L);
//        existingUser.setEmailId(currentEmail);
//
//        UserDto userDto = new UserDto();
//        userDto.setEmailId(newEmail);
//        userDto.setUsername("John Updated");
//        userDto.setContactNumber("9876543210");
//
//        // Mock behavior for finding the existing user
//        when(userRepo.findByEmailId(currentEmail)).thenReturn(Optional.of(existingUser));
//
//        // Mock behavior for checking if the new email is already in use
//        when(userRepo.findByEmailId(newEmail)).thenReturn(Optional.empty());
//
//        // Mock TypeMap and modelMapper.typeMap behavior
//        TypeMap<UserDto, User> typeMapMock = mock(TypeMap.class);
//        when(modelMapper.typeMap(eq(UserDto.class), eq(User.class))).thenReturn(typeMapMock);
//        when(typeMapMock.addMappings(any(PropertyMap.class))).thenReturn(null);
//
//        // ✅ Mock modelMapper.getConfiguration().setSkipNullEnabled(true)
//        Configuration configurationMock = mock(Configuration.class);
//        when(modelMapper.getConfiguration()).thenReturn(configurationMock);
//        when(configurationMock.setSkipNullEnabled(true)).thenReturn(configurationMock);
//
//        // Mock modelMapper.map() behavior to manually map the fields
//        doAnswer(invocation -> {
//            UserDto source = invocation.getArgument(0);
//            User destination = invocation.getArgument(1);
//            destination.setEmailId(source.getEmailId());
//            destination.setUsername(source.getUsername());
//            destination.setContactNumber(source.getContactNumber());
//            return null;
//        }).when(modelMapper).map(eq(userDto), any(User.class));
//
//        // Mock userRepo.save behavior
//        when(userRepo.save(any(User.class))).thenAnswer(i -> i.getArgument(0));
//
//        // Call the method under test
//        Response response = userService.updateUser(currentEmail, userDto);
//
//        // Assertions
//        assertEquals("200", response.getResponseCode());
//        assertEquals("User updated successfully", response.getMessage());
//
//        // Verify that the data field contains the UserDto, not the User
//        UserDto updatedUserDto = (UserDto) response.getData();
//        assertEquals(newEmail, updatedUserDto.getEmailId());
//        assertEquals("John Updated", updatedUserDto.getUsername());
//        assertEquals("9876543210", updatedUserDto.getContactNumber());
//
//        // Verify interactions
//        verify(userRepo).save(any(User.class));
//        verify(modelMapper).typeMap(eq(UserDto.class), eq(User.class));
//        verify(modelMapper).map(eq(userDto), any(User.class));
//    }
//
//    
//    @Test
//    void testUpdateUser_SuccessWithoutEmailChange() {
//        String emailId = "same@bank.com";
//
//        User existingUser = new User();
//        existingUser.setUserId(321L);
//        existingUser.setEmailId(emailId);
//
//        UserDto userDto = new UserDto();
//        userDto.setEmailId(emailId); // same email
//        userDto.setUsername("Updated Name");
//        userDto.setContactNumber("1234567890");
//
//        when(userRepo.findByEmailId(emailId)).thenReturn(Optional.of(existingUser));
//
//        TypeMap<UserDto, User> typeMapMock = mock(TypeMap.class);
//        when(modelMapper.typeMap(eq(UserDto.class), eq(User.class))).thenReturn(typeMapMock);
//        when(typeMapMock.addMappings(any(PropertyMap.class))).thenReturn(null);
//
//        Configuration configurationMock = mock(Configuration.class);
//        when(modelMapper.getConfiguration()).thenReturn(configurationMock);
//        when(configurationMock.setSkipNullEnabled(true)).thenReturn(configurationMock);
//        
//        doAnswer(invocation -> {
//            UserDto source = invocation.getArgument(0);
//            User destination = invocation.getArgument(1);
//            destination.setUsername(source.getUsername());
//            destination.setContactNumber(source.getContactNumber());
//            return null;
//        }).when(modelMapper).map(eq(userDto), any(User.class));
//
//        when(userRepo.save(any(User.class))).thenAnswer(i -> i.getArgument(0));
//
//        Response response = userService.updateUser(emailId, userDto);
//
//        assertEquals("200", response.getResponseCode());
//        assertEquals("User updated successfully", response.getMessage());
//        UserDto updatedUser = (UserDto) response.getData();
//        assertEquals("Updated Name", updatedUser.getUsername());
//        assertEquals("1234567890", updatedUser.getContactNumber());
//    }
//
//    //----------------------------getUserEmail()------------------------------
//    @Test
//    void testGetUserEmail_Success() {
//        Long userId = 101L;
//        String email = "john.doe@bank.com";
//
//        User user = new User();
//        user.setUserId(userId);
//        user.setEmailId(email);
//
//        when(userRepo.findById(userId)).thenReturn(Optional.of(user));
//
//        Response response = userService.getUserEmail(userId);
//
//        assertEquals("200", response.getResponseCode());
//        assertEquals("Email found successfully", response.getMessage());
//        assertEquals(email, response.getData());
//    }
//    
//    @Test
//    void testGetUserEmail_UserNotFound() {
//        Long userId = 999L;
//
//        when(userRepo.findById(userId)).thenReturn(Optional.empty());
//
//        ResourceNotFound exception = assertThrows(ResourceNotFound.class, () -> {
//            userService.getUserEmail(userId);
//        });
//
//        assertEquals("User not found for: " + userId, exception.getMessage());
//        assertEquals(userId.toString(), exception.getIdentifier());
//    }
//
//    //---------------------------------getContactNumber()------------------------
//
//    @Test
//    void testGetContactNumber_UserNotFound() {
//        Long userId = 999L;
//
//        when(userRepo.findById(userId)).thenReturn(Optional.empty());
//
//        ResourceNotFound exception = assertThrows(ResourceNotFound.class, () -> {
//            userService.getContactNumber(userId);
//        });
//
//        assertEquals("User not found for: " + userId, exception.getMessage());
//        assertEquals(userId.toString(), exception.getIdentifier());
//    }
//
//    @Test
//    void testGetContactNumber_Success() {
//        Long userId = 123L;
//        String expectedContactNumber = "9876543210";
//
//        User user = new User();
//        user.setUserId(userId);
//        user.setContactNumber(expectedContactNumber);
//
//        when(userRepo.findById(userId)).thenReturn(Optional.of(user));
//
//        String contactNumber = userService.getContactNumber(userId);
//
//        assertEquals(expectedContactNumber, contactNumber);
//    }
//
//    //-------------------------------------validateUser()----------------------------
//    @Test
//    void testValidateUser_UserFound_Success() {
//        String email = "user@bank.com";
//        String password = "password123";
//        User user = new User();
//        user.setEmailId(email);
//        user.setPassword(passwordEncoder.encode(password));  // Assuming password is encoded
//
//        when(userRepo.findByEmailId(email)).thenReturn(Optional.of(user));
//        when(passwordEncoder.matches(password, user.getPassword())).thenReturn(true);
//
//        Response response = userService.validateUser(email, password);
//
//        assertEquals("200", response.getResponseCode());
//        assertEquals("User found successfully", response.getMessage());
//        assertEquals(user, response.getData());
//    }
//
//    @Test
//    void testValidateUser_UserFound_PasswordMismatch() {
//        String email = "user@bank.com";
//        String password = "wrongpassword";
//        User user = new User();
//        user.setEmailId(email);
//        user.setPassword(passwordEncoder.encode("correctpassword"));  // Assuming password is encoded
//
//        when(userRepo.findByEmailId(email)).thenReturn(Optional.of(user));
//        when(passwordEncoder.matches(password, user.getPassword())).thenReturn(false);
//
//        Response response = userService.validateUser(email, password);
//
//        assertEquals("FAILURE", response.getResponseCode());
//        assertEquals("No user found or invalid credentials", response.getMessage());
//    }
//
//    @Test
//    void testValidateUser_UserNotFound_OfficerFound_Success() {
//        String email = "officer@bank.com";
//        String password = "officerpassword";
//        Officer officer = new Officer();
//        officer.setEmailId(email);
//        officer.setPassword(passwordEncoder.encode(password));  // Assuming password is encoded
//
//        when(userRepo.findByEmailId(email)).thenReturn(Optional.empty());  // Not found in user repo
//        when(officerRepo.findByEmailId(email)).thenReturn(Optional.of(officer));
//        when(passwordEncoder.matches(password, officer.getPassword())).thenReturn(true);
//
//        Response response = userService.validateUser(email, password);
//
//        assertEquals("200", response.getResponseCode());
//        assertEquals("User found successfully", response.getMessage());
//        assertEquals(officer, response.getData());
//    }
//
//    @Test
//    void testValidateUser_UserNotFound_InBothRepositories() {
//        String email = "notfound@bank.com";
//        String password = "password123";
//
//        when(userRepo.findByEmailId(email)).thenReturn(Optional.empty());  // Not found in user repo
//        when(officerRepo.findByEmailId(email)).thenReturn(Optional.empty());  // Not found in officer repo
//
//        Response response = userService.validateUser(email, password);
//
//        assertEquals("FAILURE", response.getResponseCode());
//        assertEquals("No user found or invalid credentials", response.getMessage());
//    }
//
//    @Test
//    void testValidateUser_OfficerFound_PasswordMismatch() {
//        String email = "officer@bank.com";
//        String password = "wrongpassword";
//        Officer officer = new Officer();
//        officer.setEmailId(email);
//        officer.setPassword(passwordEncoder.encode("correctpassword"));  // Assuming password is encoded
//
//        when(userRepo.findByEmailId(email)).thenReturn(Optional.empty());  // Not found in user repo
//        when(officerRepo.findByEmailId(email)).thenReturn(Optional.of(officer));
//        when(passwordEncoder.matches(password, officer.getPassword())).thenReturn(false);
//
//        Response response = userService.validateUser(email, password);
//
//        assertEquals("FAILURE", response.getResponseCode());
//        assertEquals("No user found or invalid credentials", response.getMessage());
//    }
//    
//    //---------------------------checkUserExist()-------------------------------------
//    @Test
//    void testCheckUserExist_UserExistsWithMatchingName() {
//        ForgetPasswordRequest request = new ForgetPasswordRequest();
//        request.setEmailId("user@example.com");
//        request.setName("John");
//
//        User user = new User();
//        user.setEmailId("user@example.com");
//        user.setUsername("John");
//
//        when(userRepo.findByEmailId("user@example.com")).thenReturn(Optional.of(user));
//
//        Response result = userService.checkUserExist(request);
//
//        assertEquals("200", result.getResponseCode());
//        assertEquals("User exist", result.getMessage());
//        assertTrue((Boolean) result.getData());
//    }
//    
//    @Test
//    void testCheckUserExist_UserDoesNotExist() {
//        ForgetPasswordRequest request = new ForgetPasswordRequest();
//        request.setEmailId("nonexistent@example.com");
//        request.setName("John");
//
//        when(userRepo.findByEmailId("nonexistent@example.com")).thenReturn(Optional.empty());
//
//        Response result = userService.checkUserExist(request);
//
//        assertEquals("400", result.getResponseCode());
//        assertEquals("User not exist", result.getMessage());
//        assertFalse((Boolean) result.getData());
//    }
//    
//    //------------------------------generateNewPassword()----------------------------
//    @Test
//    void testGenerateNewPassword_UserExists() {
//        String email = "user@example.com";
//        String newPassword = "newPass";
//        String encodedPassword = "encodedPass";
//
//        UpdatePasswordRequest request = new UpdatePasswordRequest(email, newPassword);
//        User user = new User();
//        user.setEmailId(email);
//
//        when(userRepo.findByEmailId(email)).thenReturn(Optional.of(user));
//        when(passwordEncoder.encode(newPassword)).thenReturn(encodedPassword);
//
//        Response response = userService.generateNewPassword(request);
//
//        assertEquals("200", response.getResponseCode());
//        assertEquals("Password updated successfully for user", response.getMessage());
//        verify(userRepo).save(user);
//        verify(passwordUpdateEvent).sendPasswordUpdateEvent(any(NotificationDto.class));
//    }
//    
//    @Test
//    void testGenerateNewPassword_OfficerExists() {
//        String email = "officer@example.com";
//        String newPassword = "newPass";
//        String encodedPassword = "encodedPass";
//
//        UpdatePasswordRequest request = new UpdatePasswordRequest(email, newPassword);
//        Officer officer = new Officer();
//        officer.setEmailId(email);
//
//        when(userRepo.findByEmailId(email)).thenReturn(Optional.empty());
//        when(officerRepo.findByEmailId(email)).thenReturn(Optional.of(officer));
//        when(passwordEncoder.encode(newPassword)).thenReturn(encodedPassword);
//
//        Response response = userService.generateNewPassword(request);
//
//        assertEquals("200", response.getResponseCode());
//        assertEquals("Password updated successfully for officer", response.getMessage());
//        verify(officerRepo).save(officer);
//        verify(passwordUpdateEvent).sendPasswordUpdateEvent(any(NotificationDto.class));
//    }
//    
//    @Test
//    void testGenerateNewPassword_NoUserOrOfficer() {
//        String email = "unknown@example.com";
//        UpdatePasswordRequest request = new UpdatePasswordRequest(email, "pass");
//
//        when(userRepo.findByEmailId(email)).thenReturn(Optional.empty());
//        when(officerRepo.findByEmailId(email)).thenReturn(Optional.empty());
//
//        Response response = userService.generateNewPassword(request);
//
//        assertEquals("404", response.getResponseCode());
//        assertEquals("No user or officer found with the given email", response.getMessage());
//    }
//    
//    @Test
//    void testGenerateNewPassword_ExceptionThrown() {
//        String email = "error@example.com";
//        UpdatePasswordRequest request = new UpdatePasswordRequest(email, "pass");
//
//        when(userRepo.findByEmailId(email)).thenThrow(new RuntimeException("DB error"));
//
//        Response response = userService.generateNewPassword(request);
//
//        assertEquals("500", response.getResponseCode());
//        assertEquals("Internal server error", response.getMessage());
//    }
//    //-----------------------------getUserId()----------------------------------------
//    @Test
//    void testGetUserId_UserFound_Success() {
//        String emailId = "user@bank.com";
//        Long expectedUserId = 123L;
//        
//        User user = new User();
//        user.setUserId(expectedUserId);
//        user.setEmailId(emailId);
//
//        when(userRepo.findByEmailId(emailId)).thenReturn(Optional.of(user));
//
//        Response response = userService.getUserId(emailId);
//
//        assertEquals("200", response.getResponseCode());
//        assertEquals("User id found successfully", response.getMessage());
//        assertEquals(expectedUserId, response.getData());
//    }
//
//    @Test
//    void testGetUserId_UserNotFound() {
//        String emailId = "notfound@bank.com";
//        
//        when(userRepo.findByEmailId(emailId)).thenReturn(Optional.empty());
//
//        Response response = userService.getUserId(emailId);
//
//        assertEquals("FAILURE", response.getResponseCode());
//        assertEquals("User not found", response.getMessage());
//    }
//
//    
//
//}
