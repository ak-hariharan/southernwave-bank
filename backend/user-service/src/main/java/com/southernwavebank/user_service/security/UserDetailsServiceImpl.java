package com.southernwavebank.user_service.security;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.southernwavebank.user_service.repository.OfficerRepository;
import com.southernwavebank.user_service.repository.UserRepository;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;
    private final OfficerRepository officerRepository;

    public UserDetailsServiceImpl(UserRepository userRepository,
                                  OfficerRepository officerRepository) {
        this.userRepository = userRepository;
        this.officerRepository = officerRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    	log.info("User loaded to user details");
        return userRepository.findByEmailId(username)
                .map(UserPrincipal::new)
                .orElseGet(() -> new UserPrincipal(officerRepository.findByEmailId(username)
                        .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username))));
    }
}