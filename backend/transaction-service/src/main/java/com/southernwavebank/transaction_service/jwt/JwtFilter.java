package com.southernwavebank.transaction_service.jwt;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.southernwavebank.transaction_service.model.entity.CustomPrincipal;

import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class JwtFilter extends OncePerRequestFilter {

	private JWTService jwtService;

	@Autowired
	public JwtFilter(JWTService jwtService) {
		this.jwtService = jwtService;
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {

		String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);

		if (authHeader == null || !authHeader.startsWith("Bearer ")) {
			filterChain.doFilter(request, response);
			return;
		}

		String jwt = authHeader.substring(7);
		try {

			Claims claims = jwtService.extractClaimsFromToken(jwt);
			log.info("Extracted claims from the token");

			if (!jwtService.isTokenValid(jwt)) {
				log.error("Token expired or audience mismatch: {}", claims);
				throw new RuntimeException("Invalid or unauthorized token");
			}

			String username = claims.getSubject();
			String role = claims.get("role", String.class);
			String userId = claims.get("userId", String.class);

			CustomPrincipal principal = new CustomPrincipal(username, role, userId);

			UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(principal,
					null, principal.getAuthorities());
			SecurityContextHolder.getContext().setAuthentication(authentication);

		} catch (Exception ex) {
			log.error("JWT validation failed: {}", ex.getMessage(), ex);
			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			response.getWriter().write("Invalid JWT Token");
			return;
		}

		filterChain.doFilter(request, response);
	}

}
