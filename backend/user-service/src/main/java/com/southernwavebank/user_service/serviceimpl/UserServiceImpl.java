package com.southernwavebank.user_service.serviceimpl;

import java.time.*;
import java.time.format.*;
import java.util.*;
import java.util.function.*;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.southernwavebank.user_service.annotations.*;
import com.southernwavebank.user_service.exception.*;
import com.southernwavebank.user_service.kafka.*;
import com.southernwavebank.user_service.model.*;
import com.southernwavebank.user_service.model.dto.*;
import com.southernwavebank.user_service.model.entity.*;
import com.southernwavebank.user_service.model.externaldto.*;
import com.southernwavebank.user_service.model.response.*;
import com.southernwavebank.user_service.repository.*;
import com.southernwavebank.user_service.service.*;

import lombok.extern.slf4j.Slf4j;

/**
  * Service implementation for managing consumers and officers.
  * Handles registration, authentication, password management and event publishing.
  */
@Slf4j
@Service
public class UserServiceImpl implements UserService {

    private OfficerRepository officerRepo;
    private UserRepository userRepo;
    private PasswordEncoder passwordEncoder;
    private ModelMapper modelMapper;
    private UserEventProducer userEventProducer;
    private AccountCreatedEventProducer accountCreatedEventProducer;
    private PasswordUpdationEventProducer passwordUpdateEvent;
    private OfficerEventProducer officerEventProducer;

    @Autowired
    public UserServiceImpl(OfficerRepository officerRepo, UserRepository userRepo, PasswordEncoder passwordEncoder,
            ModelMapper modelMapper, UserEventProducer userEventProducer,
            AccountCreatedEventProducer accountCreatedEventProducer,
            PasswordUpdationEventProducer passwordUpdateEvent,
            OfficerEventProducer officerEventProducer) {
        this.officerRepo = officerRepo;
        this.userRepo = userRepo;
        this.passwordEncoder = passwordEncoder;
        this.modelMapper = modelMapper;
        this.userEventProducer = userEventProducer;
        this.accountCreatedEventProducer = accountCreatedEventProducer;
        this.passwordUpdateEvent = passwordUpdateEvent;
        this.officerEventProducer = officerEventProducer;
    }

    /**
     * Register a new officer and persist to the repository.
     * Validates role and email uniqueness, encodes password, and returns creation result.
     *
     * @param registerRequest DTO containing officer registration details
     * @return ResponseEntity with creation result and the saved officer id on success
     * @throws none
     */
    @Override
    @Transactional
    public ResponseEntity<Response> registerOfficer(RegisterRequest registerRequest) {
        log.info("Starting user registration process");
        if (registerRequest.getRole() == Role.OFFICER) {
            log.debug("Checking if officer email already exists");
            if (officerRepo.existsByEmailId(registerRequest.getEmailId())) {
                log.warn("Officer registration failed: Email already exists");

                return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body(Response.builder().message("Email already exists").data(registerRequest).build());
            }

        }
        if (registerRequest.getRole() != Role.OFFICER) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(
                    Response.builder().message("Registration is only for Officer role").data(registerRequest).build());

        }

        log.debug("Creating new Officer entity");
        // Generate a random dummy password since the actual setup happens via email OTP link
        String dummyPassword = java.util.UUID.randomUUID().toString();
        String hashedPassword = passwordEncoder.encode(dummyPassword);
        Officer officer = new Officer();

        officer.setOfficername(registerRequest.getName());
        officer.setEmailId(registerRequest.getEmailId());
        officer.setPassword(hashedPassword);
        officer.setRole(Role.OFFICER);
        Officer savedOfficer = officerRepo.save(officer);
        log.info("New Officer registered successfully");

        // Dispatch Welcome Email Event
        NotificationDto welcomeNotification = new NotificationDto();
        welcomeNotification.setEmailId(savedOfficer.getEmailId());
        welcomeNotification.setUsername(savedOfficer.getOfficername());
        welcomeNotification.setUserId(savedOfficer.getOfficerId());
        welcomeNotification.setTime(LocalDateTime.now());
        officerEventProducer.sendOfficerCreatedEvent(welcomeNotification);
        
        registerRequest.setOfficerId(savedOfficer.getOfficerId());
        log.info("Registration process completed");
        return ResponseEntity.status(HttpStatus.OK)
                .body(Response.builder().message("User created successfully").data(registerRequest).build());

    }

    /**
     * Validate credentials for a consumer or officer.
     * Checks repositories and compares provided password against stored hash.
     *
     * @param email email to search for
     * @param password raw password to validate
     * @return ResponseEntity containing the found user/officer or NOT_FOUND on failure
     * @throws none
     */
    @Override
    public ResponseEntity<Response> validateUser(String email, String password) {
        log.info("User validation process started");
        Optional<User> userOptional = userRepo.findByEmailId(email);
        if (userOptional.isPresent()) {
            log.debug("User found in user repository");

            User user = userOptional.get();
            if (passwordEncoder.matches(password, user.getPassword())) {
                log.info("User credentials matched successfully for a consumer");
                return ResponseEntity.status(HttpStatus.OK)
                        .body(Response.builder().message("User found successfully").data(user).build());
            } else {
                log.warn("Password mismatch for user found in user repository");
            }
        } else {
            log.debug("User not found in user repository, checking officer repository");
        }

        Optional<Officer> officerOptional = officerRepo.findByEmailId(email);
        if (officerOptional.isPresent()) {
            log.debug("User found in officer repository");
            Officer officer = officerOptional.get();
            if (passwordEncoder.matches(password, officer.getPassword())) {
                log.info("User credentials matched successfully for an officer");
                return ResponseEntity.status(HttpStatus.OK)
                        .body(Response.builder().message("User found successfully").data(officer).build());
            } else {
                log.warn("Password mismatch for user found in officer repository");
            }
        } else {
            log.debug("User not found in officer repository");
        }

        log.warn("User validation failed: No user found or invalid credentials");
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Response.builder().message("No user found or invalid credentials").build());

    }


    /**
     * Verify existence for forget-password workflow by email and name.
     * Returns the matching entity if found or throws when no match exists.
     *
     * @param forgetPasswordRequest DTO containing email and name to verify
     * @return ResponseEntity with the found entity on success
     * @throws ResourceNotFound when no matching user/officer is found
     */
    @Override
    public ResponseEntity<Response> forgetPassword(ForgetPasswordRequest forgetPasswordRequest) {
        log.info("Checking if email exists in the system");

        Optional<User> userOptional = userRepo.findByEmailId(forgetPasswordRequest.getEmailId());
        if (userOptional.isPresent()) {
            if (userOptional.get().getUsername().equals(forgetPasswordRequest.getName())) {
                log.info("Consumer exists in the system");
                return ResponseEntity.status(HttpStatus.OK)
                        .body(Response.builder().message("User exist").data(userOptional.get()).build());
            }
        }

        Optional<Officer> officerOptional = officerRepo.findByEmailId(forgetPasswordRequest.getEmailId());
        if (officerOptional.isPresent()) {
            if (officerOptional.get().getOfficername().equals(forgetPasswordRequest.getName())) {

                log.info("Officer exist in the system");
                return ResponseEntity.status(HttpStatus.OK)
                        .body(Response.builder().message("Officer exist").data(officerOptional.get()).build());
            }
        }
        throw new ResourceNotFound(forgetPasswordRequest.getEmailId());
    }


    /**
     * Update password for a user or officer and emit notification event.
     * Encodes the provided password, persists it, and sends a password update event.
     *
     * @param updatePasswordRequest DTO containing email and new password
     * @return ResponseEntity with a success message
     * @throws ResourceNotFound when no corresponding user/officer is found
     */
    @Override
    @Transactional
    public ResponseEntity<Response> generateNewPassword(UpdatePasswordRequest updatePasswordRequest) {
        String emailId = updatePasswordRequest.getEmailId();
        String newRawPassword = updatePasswordRequest.getNewPassword();
        String encodedPassword = passwordEncoder.encode(newRawPassword);

        Optional<User> optionalUser = userRepo.findByEmailId(emailId);
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            user.setPassword(encodedPassword);
            userRepo.save(user);

            NotificationDto notificationDto = new NotificationDto();
            notificationDto.setEmailId(emailId);
            notificationDto.setTime(LocalDateTime.now());
            notificationDto.setContactNumber(user.getContactNumber());
            passwordUpdateEvent.sendPasswordUpdateEvent(notificationDto);

            return ResponseEntity.status(HttpStatus.OK)
                    .body(Response.builder().message("Password updated successfully for user").build());
        }

        Optional<Officer> optionalOfficer = officerRepo.findByEmailId(emailId);
        if (optionalOfficer.isPresent()) {
            Officer officer = optionalOfficer.get();
            officer.setPassword(encodedPassword);
            officerRepo.save(officer);

            NotificationDto notificationDto = new NotificationDto();
            notificationDto.setEmailId(emailId);
            notificationDto.setTime(LocalDateTime.now());
            notificationDto.setContactNumber("999123765"); // Hard coding for dummy values
            passwordUpdateEvent.sendPasswordUpdateEvent(notificationDto);

            return ResponseEntity.status(HttpStatus.OK)
                    .body(Response.builder().message("Password updated successfully for officer").build());
        }

        throw new ResourceNotFound(updatePasswordRequest.getEmailId());
    }

    /**
     * Create a new consumer user, persist it, and publish related events.
     * Generates a temporary password, saves the user, and emits notification/account events.
     *
     * @param userDto DTO representing user input
     * @param time timestamp to include in notification event
     * @return ResponseEntity with created consumer summary
     * @throws ResourceConflict when email already exists
     */
    @Override
    @ValidateUserDto
    @Transactional
    public ResponseEntity<Response> createUser(UserDto userDto, LocalDateTime time) {
        log.info("User creation process started");
        if (userRepo.existsByEmailId(userDto.getEmailId())) {
            log.warn("User creation failed: Email already exists");
            throw new ResourceConflict(userDto);
        }

        User user = new User();
        modelMapper.map(userDto, user);

        user.setRole(Role.CONSUMER);
        String username = userDto.getUsername();
        String contactNumber = userDto.getContactNumber();
        String creationDate = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")); // 20250409
        log.debug("Temporary password generated");
        String tempPassword = username.substring(0, 4) + creationDate + "@"
                + contactNumber.substring(contactNumber.length() - 2);
        String hashedPassword = passwordEncoder.encode(tempPassword);
        user.setPassword(hashedPassword);

        User savedUser = userRepo.save(user);
        userDto.setUserId(savedUser.getUserId()); 
        log.info("User with given email successfully saved to the database");

        ConsumerDto consumerDto = new ConsumerDto();
        consumerDto.setUsername(savedUser.getUsername());
        consumerDto.setEmailId(savedUser.getEmailId());
        consumerDto.setContactNumber(savedUser.getContactNumber());

        NotificationDto notificationDto = new NotificationDto();
        notificationDto.setUsername(savedUser.getUsername());
        notificationDto.setEmailId(savedUser.getEmailId());
        notificationDto.setContactNumber(savedUser.getContactNumber());
        notificationDto.setTime(time);

        log.info("Publishing 'user-created' event to Kafka");
        userEventProducer.sendUserCreatedEvent(notificationDto);

        log.info("Publishing 'account-create-initiate' event to Kafka");
        accountCreatedEventProducer.sendAccountCreatedEvent(userDto);

        log.info("User creation process completed successfully");
        return ResponseEntity.status(HttpStatus.OK)
                .body(Response.builder().message("User created successfully").data(consumerDto).build());
    }

    /**
     * Update an existing user's details with uniqueness checks.
     * Validates unique fields, maps remaining fields, and persists the updated user.
     *
     * @param emailId email of the user to update (lookup key)
     * @param userDto DTO containing updated fields
     * @return ResponseEntity with updated UserDto on success
     * @throws ResourceNotFound when user with the provided email is not found
     * @throws DuplicateFieldException when unique field constraints are violated
     */
    @Transactional
    @Override
    public ResponseEntity<Response> updateUser(String emailId, UserDto userDto) {
            log.info("User update process started");
            User user = userRepo.findByEmailId(emailId).orElseThrow(() -> {
                log.warn("User update failed: User not found");
                return new ResourceNotFound(emailId);
            });

            Map<String, BiConsumer<UserDto, User>> uniqueFieldUpdaters = Map.of("emailId", (dto, entity) -> {
                log.info("Upadting email..");
                String newEmail = dto.getEmailId();
                if (newEmail != null) {
                    if (newEmail.equalsIgnoreCase(entity.getEmailId())) {
                        throw new DuplicateFieldException("Don't provide the old email", newEmail);
                    }
                    if (userRepo.existsByEmailIdAndUserIdNot(newEmail, entity.getUserId())) {
                        throw new DuplicateFieldException("Email already in use", newEmail);
                    }
                    entity.setEmailId(newEmail);
                }
            }, "contactNumber", (dto, entity) -> {
                log.info("Upadting contact number..");
                String newContact = dto.getContactNumber();
                if (newContact != null) {
                    if (newContact.equalsIgnoreCase(entity.getContactNumber())) {
                        throw new DuplicateFieldException("Don't provide the old number", newContact);
                    }
                    if (userRepo.existsByContactNumberAndUserIdNot(newContact, entity.getUserId())) {
                        throw new DuplicateFieldException("Contact number already in use", newContact);
                    }

                }
                entity.setContactNumber(newContact);
            });

            // Apply all unique field validations dynamically
            uniqueFieldUpdaters.values().forEach(updater -> updater.accept(userDto, user));

            // Map remaining non-unique fields from DTO
            modelMapper.map(userDto, user);

            User updatedUser = userRepo.save(user);
            UserDto updatedDto = modelMapper.map(updatedUser, UserDto.class);

            log.info("User updated successfully");
            return ResponseEntity.ok(Response.builder().message("User updated successfully").data(updatedDto).build());
    }

    /**
     * Retrieve basic user data (id and role) for a given email.
     * Checks officer repository first, then user repository, returns NOT_FOUND if absent.
     *
     * @param email email to lookup
     * @return ResponseEntity with UserData or NOT_FOUND
     * @throws none
     */
    @Override
    @Transactional
    public ResponseEntity<Response> getUser(String email) {
            log.info("Fetching the user details process started");
            Optional<Officer> officer = officerRepo.findByEmailId(email);

            if (officer.isPresent()) {
                log.info("Officer found for given email");
                UserData userData = new UserData();
                userData.setUserId(officer.get().getOfficerId());
                userData.setRole("OFFICER");
                log.info("Sending the fetched officer details");
                return ResponseEntity.status(HttpStatus.OK)
                        .body(Response.builder().message("Officer found").data(userData).build());
            }
            Optional<User> user = userRepo.findByEmailId(email);
            if (user.isPresent()) {
                log.info("Consumer found for given email");
                UserData userData = new UserData();
                userData.setUserId(user.get().getUserId());
                userData.setRole("CONSUMER");
                log.info("Sending the fetched consumer details");
                return ResponseEntity.status(HttpStatus.OK)
                        .body(Response.builder().message("Consumer found").data(userData).build());
            }
            log.info("User details not found for given email");
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Response.builder().message("No data found").data(null).build());
    }

    /**
     * Return the email for a user id.
     * Looks up the user by id and returns the associated email string.
     *
     * @param userId id of the user
     * @return ResponseEntity containing the email
     * @throws ResourceNotFound if id is not found
     */
    @Override
    public ResponseEntity<String> getUserEmail(Long userId) {
            log.info("Fetching user email by user ID");

            Optional<User> optionalUser = userRepo.findById(userId);
            if (optionalUser.isPresent()) {
                log.info("User email fetched successfully");
                return ResponseEntity.ok(optionalUser.get().getEmailId());
            }

            Optional<Officer> optionalOfficer = officerRepo.findById(userId);
            if (optionalOfficer.isPresent()) {
                log.info("Officer email fetched successfully");
                return ResponseEntity.ok(optionalOfficer.get().getEmailId());
            }

            log.warn("Email not found for this userid");
            throw new ResourceNotFound(userId);
    }

    /**
     * Return the contact number for a user id.
     * Fetches user by id and returns the contact number string.
     *
     * @param userId id of the user
     * @return ResponseEntity containing the contact number
     * @throws ResourceNotFound if id is not found
     */
    @Override
    public ResponseEntity<String> getContactNumber(Long userId) {
            log.info("Fetching contact number for user");

            Optional<User> optionalUser = userRepo.findById(userId);
            if (optionalUser.isPresent()) {
                log.info("Contact number fetched successfully");
                return ResponseEntity.ok(optionalUser.get().getContactNumber());
            }

            Optional<Officer> optionalOfficer = officerRepo.findById(userId);
            if (optionalOfficer.isPresent()) {
                log.info("Returning dummy contact number for officer");
                return ResponseEntity.ok("999123765");
            }

            log.warn("Contact number not found for this userid");
            throw new ResourceNotFound(userId);
    }

    /**
     * Retrieve full UserDto for a given user id.
     * Maps the User entity to UserDto and returns it if found.
     *
     * @param userId id of the user
     * @return ResponseEntity with mapped UserDto
     * @throws ResourceNotFound if the id does not exist
     */
    @Override
    public ResponseEntity<?> getUserData(Long userId) {
            log.info("Fetching user data for user id");

            Optional<User> optionalUser = userRepo.findById(userId);
            if (optionalUser.isPresent()) {
                log.info("User data fetched successfully");
                UserDto userDto = modelMapper.map(optionalUser.get(), UserDto.class);
                return ResponseEntity.status(HttpStatus.OK).body(userDto);
            }

            Optional<Officer> optionalOfficer = officerRepo.findById(userId);
            if (optionalOfficer.isPresent()) {
                log.info("Officer data fetched successfully");
                return ResponseEntity.status(HttpStatus.OK).body(optionalOfficer.get());
            }

            log.warn("User/Officer not found for this userid");
            throw new ResourceNotFound(userId);
    }

}
