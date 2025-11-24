package com.southernwavebank.account_service.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.southernwavebank.account_service.exception.CustomAccessDeniedHandler;
import com.southernwavebank.account_service.exception.CustomAuthenticationEntryPoint;
import com.southernwavebank.account_service.jwt.JwtFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
	
	private JwtFilter jwtFilter;
	private CustomAuthenticationEntryPoint entryPoint;
	private CustomAccessDeniedHandler accessDeniedHandler;
	
	@Autowired
	public SecurityConfig(JwtFilter jwtFilter, CustomAuthenticationEntryPoint entryPoint,
			CustomAccessDeniedHandler accessDeniedHandler) {
		this.jwtFilter = jwtFilter;
		this.entryPoint = entryPoint;
		this.accessDeniedHandler = accessDeniedHandler;
	}
	
	@Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth
            		.requestMatchers("/swb/account/details/*", "/swb/account/balance/*",
                            "/swb/account/deposit", "/swb/account/withdraw").hasAnyAuthority("CONSUMER", "OFFICER")
            		.requestMatchers("/swb/account/update/*").hasAuthority("OFFICER")
            		.anyRequest().permitAll()
            )
            .exceptionHandling(ex -> ex
	                .authenticationEntryPoint(entryPoint)
	                .accessDeniedHandler(accessDeniedHandler)
	            )
            .sessionManagement(session -> session
                    .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )
            .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
        	.build();
    }

}
