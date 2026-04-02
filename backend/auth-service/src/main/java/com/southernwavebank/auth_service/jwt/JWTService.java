package com.southernwavebank.auth_service.jwt;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;


@Service
@Slf4j
public class JWTService {

	@Autowired
    private JwtKeyStoreLoader keyStoreLoader;
	
	@Value("${jwt.key.alias}")
    private String keyAlias;
	
	 @Value("${jwt.key.password}")
	 private String keyPassword;
	 
	 private PrivateKey privateKey;
	 
	 private PublicKey publicKey;

	 
	 @PostConstruct
	 public void init() throws Exception {
	     this.privateKey = keyStoreLoader.getPrivateKey(keyAlias);
	     this.publicKey = keyStoreLoader.getPublicKey();
	     log.info("Loaded encryption key");
	 }

	 
	 public String generateToken(String username, String role, int tokenVersion, String userId, String targetServiceAlias) {
	        log.info("Generating token for user '{}'", username);
	        Map<String, Object> claims = new HashMap<>();
	        claims.put("role", role);
	        claims.put("tokenVersion", tokenVersion);
	        claims.put("userId", userId);
	        String service = targetServiceAlias.replace("-service", "");
	        claims.put("aud", service);
	        claims.put("tokenType", "access");
	        try {
				PrivateKey serviceKey = keyStoreLoader.getPrivateKey(targetServiceAlias);
				return createToken(claims, username, 10 * 60 * 1000L, serviceKey); // 30 minutes
			} catch (Exception e) {
				log.error("Error loading key for alias {}: {}", targetServiceAlias, e.getMessage());
				throw new RuntimeException("Failed to generate token for target service: " + targetServiceAlias);

			}
	 }
	 
	 public String generateRefreshToken(String username, int tokenVersion) {
	        Map<String, Object> claims = new HashMap<>();
	        claims.put("tokenVersion", tokenVersion);
	        claims.put("tokenType", "refresh");
	        return createToken(claims, username, 2 * 60 * 60 * 1000L); // 7 days
	 }
	 
	 
	 // For Refresh token
	 private String createToken(Map<String, Object> claims, String subject, long validityMillis) {
	        return Jwts.builder()
	                .claims(claims)
	                .subject(subject)
	                .issuedAt(new Date())
	                .expiration(new Date(System.currentTimeMillis() + validityMillis))
	                .signWith(privateKey, Jwts.SIG.RS256)
	                .compact();
	 }
	 
	 // For Access token
	 private String createToken(Map<String, Object> claims, String subject, long validityMillis,PrivateKey signingKey) {
	        return Jwts.builder()
	                .claims(claims)
	                .subject(subject)
	                .issuedAt(new Date())
	                .expiration(new Date(System.currentTimeMillis() + validityMillis))
	                .signWith(signingKey, Jwts.SIG.RS256)
	                .compact();
	 }
	 
	 public boolean validateToken(String token, int currentTokenVersion) {
	        try {
	            var claims = Jwts.parser()
	                    .verifyWith(publicKey)
	                    .build()
	                    .parseSignedClaims(token)
	                    .getPayload();
	            
	            boolean flag = true;

	            // Token version check
	            Integer tokenVersionInJwt = claims.get("tokenVersion", Integer.class);
	            if (tokenVersionInJwt == null || tokenVersionInJwt != currentTokenVersion) {
	                log.warn("Token version mismatch");
	                flag = false;
	                return flag;
	            }
	            
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
	            if (!"auth".equals(audience)) {
	                log.warn("Invalid audience");
	                flag = false;
	                return flag;
	            }
	            
	            String typeOfToken = claims.get("tokenType", String.class);
	            if(!typeOfToken.equals("access")) {
	            	log.warn("Not a access token");
	            	flag = false;
	            	return flag;
	            }
	            
	            log.info("Access Token validation result: {}", flag);
	            return true;
	        } catch (Exception e) {
	            log.warn("Token validation failed", e);
	            return false;
	        }
	 }
	 
	 public boolean validateRefreshToken(String token, int currentTokenVersion) {
	        try {
	            var claims = Jwts.parser()
	                    .verifyWith(publicKey)
	                    .build()
	                    .parseSignedClaims(token)
	                    .getPayload();

	            boolean flag = true;

	            // Token version check
	            Integer tokenVersionInJwt = claims.get("tokenVersion", Integer.class);
	            if (tokenVersionInJwt == null || tokenVersionInJwt != currentTokenVersion) {
	                log.warn("Token version mismatch");
	                flag = false;
	                return flag;
	            }
	            
	            String typeOfToken = claims.get("tokenType", String.class);
	            System.out.println(typeOfToken);
	            if(!typeOfToken.equals("refresh")) {
	            	log.warn("Not a refersh token");
	            	flag = false;
	            	return flag;
	            }
	            
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
	            
	            log.info("Refresh Token validation result: {}", flag);
	            return true;
	            
	        } catch (Exception e) {
	            return false;
	        }
	 }
	    
	 
		// Extract username from token
		public String extractUsername(String token) {
			log.info("Extracting username from token");
			return Jwts.parser().verifyWith(publicKey).build().parseSignedClaims(token).getPayload().getSubject();
		}

		// Extract role from token
		public String extractRole(String token) {
			log.info("Extracting role from token");
			return Jwts.parser().verifyWith(publicKey).build().parseSignedClaims(token).getPayload().get("role",
					String.class);
		}

		// Extract the token version
		public Integer extractTokenVersion(String token) {
			log.info("Extracting token version from token");
			return Jwts.parser().verifyWith(publicKey).build().parseSignedClaims(token).getPayload().get("tokenVersion",
					Integer.class);
		}
		
		public String extractUserId(String token) {
			return Jwts.parser().verifyWith(publicKey).build().parseSignedClaims(token).getPayload().get("userId",
					String.class);
		}
 
		
		public Claims extractClaimsFromToken(String token){
		    
		    return Jwts.parser()
                    .verifyWith(publicKey)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
		}
}