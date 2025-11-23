package com.southernwavebank.auth_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.southernwavebank.auth_service.model.entity.UserTokenInfo;

@Repository
public interface UserTokenInfoRepository extends JpaRepository<UserTokenInfo, String>{

	Integer findByEmailId(String emailId);
	
}
