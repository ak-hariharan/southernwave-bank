package com.southernwavebank.user_service.controller;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import com.southernwavebank.user_service.model.dto.*;
import com.southernwavebank.user_service.model.externaldto.*;
import com.southernwavebank.user_service.model.requestgroups.*;
import com.southernwavebank.user_service.model.response.*;
import com.southernwavebank.user_service.service.*;

import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.slf4j.Slf4j;

/**
 * REST controller exposing user-related endpoints for officers and internal services.
 * Delegates business logic to UserService and handles request/response mapping and logging.
 */
@RestController
@RequestMapping("/boi/users")
@Slf4j
public class UserController {
	
	private UserService userService;
	
	@Autowired
	public UserController(UserService userService) {
		this.userService = userService;
	}


	/**
	 * Handle officer registration requests and delegate persistence to the service.
	 * Returns the service response containing creation result and any payload.
	 *
	 * @param registerRequest officer registration DTO
	 * @return ResponseEntity<Response> with creation result
	 */
	@PostMapping("/register")
	public ResponseEntity<Response> registerOfficer(@RequestBody RegisterRequest registerRequest) {
		log.info("Officer registration request received");
		ResponseEntity<Response> response = userService.registerOfficer(registerRequest);
		log.info("Officer registration response sent");
		return response;
	}

	/**
	 * Validate user credentials for authentication purposes.
	 * Delegates validation to UserService and returns the result for JWT operations.
	 *
	 * @param request login request with email and password
	 * @return ResponseEntity<Response> with validation result or NOT_FOUND
	 */
	@PostMapping("/login")
	public ResponseEntity<Response> validateUser(@RequestBody LoginRequest request) {
		log.info("Login validation request received");
		ResponseEntity<Response> response = userService.validateUser(request.getEmailId(), request.getPassword());
		log.info("Login validation response sent");
		return response;
	}
	
	/**
	 * Verify user existence for the forgot-password flow by email and name.
	 * Returns the matching entity or an error response if not found.
	 *
	 * @param forgetPasswordRequest DTO containing email and name
	 * @return ResponseEntity<Response> with found user/officer data
	 */
	@PostMapping("/forgot-password")
	public ResponseEntity<Response> forgetPassword(@RequestBody ForgetPasswordRequest forgetPasswordRequest){
		log.info("New password generation request received");
		ResponseEntity<Response> response = userService.forgetPassword(forgetPasswordRequest);
		log.info("Password generation finished");
		return response;
	}
	
	/**
	 * Accept a password update request and delegate generation/persistence to the service.
	 * Returns the service response indicating success or failure of the update.
	 *
	 * @param updatePasswordRequest DTO with email and new password
	 * @return ResponseEntity<Response> indicating update status
	 */
	@PutMapping("/update-password")
	public ResponseEntity<Response> generateNewPassword(@RequestBody UpdatePasswordRequest updatePasswordRequest){
		log.info("New password generation request received");
		ResponseEntity<Response> response = userService.generateNewPassword(updatePasswordRequest);
		log.info("Password generation finished");
		return response;
	}
	
	/**
	 * Create a new consumer user; accessible to officers only.
	 * Validates input and delegates creation and event publishing to the service.
	 *
	 * @param userDto consumer user DTO
	 * @return ResponseEntity<Response> with created consumer summary
	 */
	@PostMapping("/create")
	public ResponseEntity<Response> createUser(@Validated @RequestBody UserDto userDto) {
		log.info("User creation request received");
		ResponseEntity<Response> response = userService.createUser(userDto, LocalDateTime.now());
		log.info("User creation response sent");
		return response;
	}
	
	/**
	 * Partially update user details identified by email; officer-only operation.
	 * Validates unique fields and delegates the update to the service.
	 *
	 * @param emailId email of the user to update
	 * @param userDto DTO containing update fields
	 * @return ResponseEntity<Response> with updated user data
	 */
	@PatchMapping("/update/{emailId}")
	public ResponseEntity<Response> updateUser(@PathVariable(required = true) String emailId, @Validated(OnUpdate.class) @RequestBody UserDto userDto) {
		 log.info("User update request received");
		 ResponseEntity<Response> response = userService.updateUser(emailId, userDto);
		 log.info("User update response sent");
		 return response;

	}
	//---------------------------------For internal service communication-----------------------------------
	// Retrieving the user details against the DB(for refresh token)

	/**
	 * Fetch basic user data by email for internal services (hidden in API docs).
	 * Delegates retrieval to the service and returns role/id mapping.
	 *
	 * @param email email to lookup
	 * @return ResponseEntity<Response> with UserData or NOT_FOUND
	 */
	@GetMapping("/userdata")
	@Operation(hidden = true)
    public ResponseEntity<Response> getUser(@RequestParam String email) {
		log.info("Fetching user details from the database");
        ResponseEntity<Response> response = userService.getUser(email);
        log.info("Sending user details response");
        return response;
    }

	//--------------------------------For Notification service----------------------------------------------
	/**
	 * Return the email associated with the given user ID for notification usage.
	 * Delegates to service and returns the email string.
	 *
	 * @param userId id of the user
	 * @return ResponseEntity<String> containing the email
	 */
	@GetMapping("/email/{userId}")
	@Operation(hidden = true)
    public ResponseEntity<String> getUserEmail(@PathVariable Long userId){
		log.info("Request to fetch user email by ID received");
		ResponseEntity<String> response = userService.getUserEmail(userId);
		log.info("Get user email response sent");
		return response;

	}
	
	/**
	 * Return the contact number associated with the given user ID for notification usage.
	 * Delegates to service and returns the contact number string.
	 *
	 * @param userId id of the user
	 * @return ResponseEntity<String> containing the contact number
	 */
	@GetMapping("/contactNumber/{userId}")
	@Operation(hidden = true)
	public ResponseEntity<String> getContactNumber(@PathVariable Long userId) {
		log.info("Request to fetch user contact number by ID received");
		ResponseEntity<String> response = userService.getContactNumber(userId);
		log.info("Get contact number response sent");
		return response;
	}
	//-------------------------------------------------------------------------------------------------------

	/**
	 * Fetch full user DTO by user ID for internal communication (hidden in API docs).
	 * Delegates to service and returns the mapped UserDto payload.
	 *
	 * @param userId id of the user
	 * @return ResponseEntity<?> with mapped UserDto
	 */
	@GetMapping("/{userId}")
	@Operation(hidden = true)
    public ResponseEntity<?> getUserData(@PathVariable("userId") Long userId){
		log.info("Request to fetch user data by ID received");
		ResponseEntity<?> response = userService.getUserData(userId);
		log.info("Get user data response sent");
		return response;

	}
}
