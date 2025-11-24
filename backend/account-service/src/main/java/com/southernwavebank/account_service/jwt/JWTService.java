package com.southernwavebank.account_service.jwt;

import java.security.PublicKey;
import java.util.Date;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class JWTService {

	@Autowired
	private JwtKeyLoader keyLoader;

	private PublicKey publicKey;

	@PostConstruct
	public void init() throws Exception {
		this.publicKey = keyLoader.getPublicKey();
		log.info("Loaded encryption key");
	}

	public boolean isTokenValid(String token) {
		 try {
	            var claims = Jwts.parser()
	                    .verifyWith(publicKey)
	                    .build()
	                    .parseSignedClaims(token)
	                    .getPayload();
	            
	            boolean flag = true;
	            
	            // Expiration check
	            Date expiration = claims.getExpiration();
	            if (expiration == null || expiration.before(new Date())) {
	                log.warn("Token is expired");
	                flag = false;
	                return flag;
	            }

	            // Issued at check
	            Date issuedAt = claims.getIssuedAt();
	            if (issuedAt != null && issuedAt.after(new Date())) {
	                log.warn("Token issued in the future");
	                flag = false;
	                return flag;
	            }

	            // Audience check
	            Set<String> audienceSet = claims.getAudience();
	            String audience =  audienceSet.iterator().next();
	            if (!"account".equals(audience)) {
	                log.warn("Invalid audience");
	                flag = false;
	                return flag;
	            }
	            

	            log.info("Token validation result: {}", flag);
	            return true;
	        } catch (Exception e) {
	            log.warn("Token validation failed", e);
	            return false;
	        }
	}

	public Claims extractClaimsFromToken(String token) {

		return Jwts.parser().verifyWith(publicKey).build().parseSignedClaims(token).getPayload();
	}
}
