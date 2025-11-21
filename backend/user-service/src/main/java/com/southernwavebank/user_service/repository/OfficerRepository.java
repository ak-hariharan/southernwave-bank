package com.southernwavebank.user_service.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.southernwavebank.user_service.model.entity.Officer;

@Repository
public interface OfficerRepository extends JpaRepository<Officer, Long>{

	boolean existsByEmailId(String emailId);

	Optional<Officer> findByEmailId(String email);
	
	Optional<Officer> findByOfficername(String officername);
}
