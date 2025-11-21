package com.southernwavebank.user_service.security;

import java.util.Collection;
import java.util.Collections;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.southernwavebank.user_service.model.Role;
import com.southernwavebank.user_service.model.entity.Officer;
import com.southernwavebank.user_service.model.entity.User;

public class UserPrincipal implements UserDetails {

    private final String username;
    private final String password;
    private final Role role;

    public UserPrincipal(User user) {
        this.username = user.getUsername();
        this.password = user.getPassword();
        this.role = user.getRole();
    }

    public UserPrincipal(Officer officer) {
        this.username = officer.getOfficername();
        this.password = officer.getPassword();
        this.role = officer.getRole();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singletonList(new SimpleGrantedAuthority(role.toString()));
    }
    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}

