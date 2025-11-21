package com.southernwavebank.user_service.service;

import java.time.LocalDateTime;

import org.springframework.http.ResponseEntity;

import com.southernwavebank.user_service.model.dto.UpdatePasswordRequest;
import com.southernwavebank.user_service.model.dto.UserDto;
import com.southernwavebank.user_service.model.externaldto.ForgetPasswordRequest;
import com.southernwavebank.user_service.model.externaldto.RegisterRequest;
import com.southernwavebank.user_service.model.response.Response;

/**
 * Core service API for user and officer operations.
 * Declares operations for registration, authentication, lifecycle and lookup actions.
 */
public interface UserService {
	
	/**
	 * Register a new officer in the system and persist to repository.
	 * Returns a ResponseEntity with the creation result and payload.
	 *
	 * @param registerRequest officer registration DTO
	 * @return ResponseEntity<Response> with creation outcome
	 * @throws none
	 */
	ResponseEntity<Response> registerOfficer(RegisterRequest registerRequest);
	
	/**
	 * Validate credentials for a consumer or officer and return the matched entity.
	 * Used by authentication components to verify login details.
	 *
	 * @param email email to search for
	 * @param password raw password to validate
	 * @return ResponseEntity<Response> with found user/officer or NOT_FOUND
	 * @throws none
	 */
	ResponseEntity<Response> validateUser(String email, String password);
	
	/**
	 * Verify existence for the forgot-password flow by email and name.
	 * Returns the matching entity or triggers a not-found error.
	 *
	 * @param forgetPasswordRequest DTO containing email and name
	 * @return ResponseEntity<Response> with found user/officer data
	 * @throws com.southernwavebank.user_service.exception.ResourceNotFound when no match exists
	 */
	ResponseEntity<Response> forgetPassword(ForgetPasswordRequest forgetPasswordRequest);
	
	/**
	 * Update password for a user or officer and emit any related notifications.
	 * Persists the encoded password and returns success status.
	 *
	 * @param updatePasswordRequest DTO containing email and new password
	 * @return ResponseEntity<Response> indicating update result
	 * @throws com.southernwavebank.user_service.exception.ResourceNotFound when no corresponding user/officer is found
	 */
	ResponseEntity<Response> generateNewPassword(UpdatePasswordRequest updatePasswordRequest);
	
	/**
	 * Create a new consumer user, persist it and publish downstream events.
	 * Generates a temporary password, saves the user and returns a summary DTO.
	 *
	 * @param user consumer user DTO
	 * @param time timestamp to include in notification event
	 * @return ResponseEntity<Response> with created consumer summary
	 * @throws com.southernwavebank.user_service.exception.ResourceConflict when email already exists
	 */
	ResponseEntity<Response> createUser(UserDto user, LocalDateTime time);
	
	/**
	 * Update an existing user's details identified by email.
	 * Performs uniqueness checks, maps fields and persists the updated user.
	 *
	 * @param emailId email of the user to update
	 * @param userDto DTO containing updated fields
	 * @return ResponseEntity<Response> with updated UserDto on success
	 * @throws com.southernwavebank.user_service.exception.ResourceNotFound when user not found
	 * @throws com.southernwavebank.user_service.exception.DuplicateFieldException when unique field constraints are violated
	 */
	ResponseEntity<Response> updateUser(String emailId, UserDto userDto);
	
	/**
	 * Retrieve basic user data (id and role) for a given email.
	 * Returns role/id mapping or NOT_FOUND when absent.
	 *
	 * @param email email to lookup
	 * @return ResponseEntity<Response> with UserData or NOT_FOUND
	 * @throws none
	 */
	ResponseEntity<Response> getUser(String email);
	
	/**
	 * Return the email address for a given user id.
	 * Used by downstream services for notifications.
	 *
	 * @param userId id of the user
	 * @return ResponseEntity<String> containing the email
	 * @throws com.southernwavebank.user_service.exception.ResourceNotFound if id is not found
	 */
	ResponseEntity<String> getUserEmail(Long userId);
	
	/**
	 * Return the contact number for a given user id.
	 * Used by downstream services for notifications.
	 *
	 * @param userId id of the user
	 * @return ResponseEntity<String> containing the contact number
	 * @throws com.southernwavebank.user_service.exception.ResourceNotFound if id is not found
	 */
	ResponseEntity<String> getContactNumber(Long userId);

	/**
	 * Retrieve full UserDto for a given user id.
	 * Maps the User entity to UserDto and returns it.
	 *
	 * @param userId id of the user
	 * @return ResponseEntity<?> with mapped UserDto
	 * @throws com.southernwavebank.user_service.exception.ResourceNotFound if the id does not exist
	 */
	ResponseEntity<?> getUserData(Long userId);
	
}
