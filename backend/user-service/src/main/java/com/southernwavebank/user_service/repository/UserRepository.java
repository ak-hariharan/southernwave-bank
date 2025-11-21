package com.southernwavebank.user_service.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.southernwavebank.user_service.model.entity.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long>{

	Optional<User> findByContactNumber(String contactNumber);

	Boolean existsByEmailId(String email);

	Optional<User> findByEmailId(String email);

	Optional<User> findByUsername(String username);
	
	boolean existsByEmailIdAndUserIdNot(String emailId, Long userId);

	boolean existsByUsernameAndUserIdNot(String newUsername, Long userId);

	boolean existsByContactNumberAndUserIdNot(String newContact, Long userId);


}
