package com.southernwavebank.transaction_service.model.entity;

import java.util.Collection;
import java.util.Collections;


import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

public class CustomPrincipal implements UserDetails{
	
	private String username;
	private String role;
	private String userId;
	
	public CustomPrincipal(String username, String role, String userId) {
		this.username = username;
		this.role = role;
		this.userId = userId;
	}

	@Override
	public String getUsername() {
		return username;
	}
	
	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return Collections.singletonList(new SimpleGrantedAuthority(role));
	}
	
	public String getUserId() {
		return userId;
	}

	@Override
	public String getPassword() {
		return null;
	}


}
