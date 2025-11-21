package com.southernwavebank.user_service.model.entity;


import com.southernwavebank.user_service.model.Role;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Officer {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long officerId;
	
	private String officername;
	
	@Column(unique = true, nullable = false)
	private String emailId;
	
	private String password;
	
	@Enumerated(EnumType.STRING)
	private Role role;
	
}
