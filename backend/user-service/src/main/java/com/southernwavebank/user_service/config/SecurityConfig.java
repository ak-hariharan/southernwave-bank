package com.southernwavebank.user_service.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.southernwavebank.user_service.exception.CustomAuthenticationEntryPoint;
import com.southernwavebank.user_service.jwt.JwtFilter;

import lombok.extern.slf4j.Slf4j;


@Configuration
@EnableWebSecurity
@Slf4j
public class SecurityConfig {

	private JwtFilter jwtFilter;
	private UserDetailsService userDetailsService;
	private CustomAuthenticationEntryPoint entryPoint;

	@Autowired
	public SecurityConfig(JwtFilter jwtFilter, UserDetailsService userDetailsService,
			CustomAuthenticationEntryPoint entryPoint) {
		this.jwtFilter = jwtFilter;
		this.userDetailsService = userDetailsService;
		this.entryPoint = entryPoint;
	}


	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		return http.csrf(customizer -> customizer.disable())
				.authorizeHttpRequests(
						 request -> request
						.requestMatchers("/swb/users/register").hasAuthority("SUPER_OFFICER")
						.requestMatchers("/swb/users/create", "/swb/users/update/{emailId}").hasAuthority("OFFICER")
						.anyRequest().permitAll()
						)
				.exceptionHandling(ex -> ex
		                .authenticationEntryPoint(entryPoint)
		            )
				.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
				.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
				.build();

	}

	@Bean
	public AuthenticationProvider authenticationProvider() {
		DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
		provider.setPasswordEncoder(new BCryptPasswordEncoder(12));
		provider.setUserDetailsService(userDetailsService);

		return provider;
	}

	@Bean
	public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
		return config.getAuthenticationManager();

	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

}
