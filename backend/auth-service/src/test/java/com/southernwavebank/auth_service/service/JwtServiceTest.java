package com.southernwavebank.auth_service.service;
//package com.bankofindia.auth_service.service;
//
//
//import io.jsonwebtoken.Claims;
//import jakarta.annotation.PostConstruct;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.springframework.test.util.ReflectionTestUtils;
//
//import java.lang.reflect.Field;
//import java.util.Base64;
//import java.util.Date;
//
//import static org.junit.jupiter.api.Assertions.*;
//
//public class JwtServiceTest {
//
//    private JwtService jwtService;
//
//    private String username = "user@example.com";
//    private String role = "CONSUMER";
//    private int tokenVersion = 1;
//
//    @BeforeEach
//    void setUp() throws Exception {
//        jwtService = new JwtService();
//        // Manually trigger @PostConstruct
//        
//        String rawSecret = "p9KGUq1SUPzjOlR7AFmiIBWenYR31jQY90qGQXjjBpXMQ="; 
//        String encodedKey = Base64.getEncoder().encodeToString(rawSecret.getBytes());
//        
//        Field field = JwtService.class.getDeclaredField("encodedKey");
//        field.setAccessible(true);
//        field.set(jwtService, encodedKey);
//        
//        jwtService.init();
//    }
//
//    @Test
//    void testGenerateToken_notNullOrEmpty() {
//        String token = jwtService.generateToken(username, role, tokenVersion);
//        assertNotNull(token);
//        assertFalse(token.isEmpty());
//    }
//
//    @Test
//    void testValidateToken_validToken() {
//        String token = jwtService.generateToken(username, role, tokenVersion);
//        boolean isValid = jwtService.validateToken(token, tokenVersion);
//        assertTrue(isValid);
//    }
//    
//    @Test
//    void testValidateToken_invalidTokenVersion() {
//        String token = jwtService.generateToken(username, role, tokenVersion);
//        boolean isValid = jwtService.validateToken(token, tokenVersion + 1); // wrong version
//        assertFalse(isValid);
//    }
//    
//    @Test
//    void testValidateRefreshToken_validToken() {
//    	String token = jwtService.generateRefreshToken(username, tokenVersion);
//    	boolean isValid = jwtService.validateRefreshToken(token, tokenVersion);
//    	assertTrue(isValid);
//    }
//    
//    @Test
//    void testValidateRefreshToken_invalidTokenVersion() {
//        String token = jwtService.generateRefreshToken(username, tokenVersion);
//        boolean isValid = jwtService.validateRefreshToken(token, tokenVersion + 1); 
//        assertFalse(isValid);
//    }
//    
//    
//
//    @Test
//    void testExtractUsername() {
//        String token = jwtService.generateToken(username, role, tokenVersion);
//        String extractedUsername = jwtService.extractUsername(token);
//        assertEquals(username, extractedUsername);
//    }
//
//    @Test
//    void testExtractRole() {
//        String token = jwtService.generateToken(username, role, tokenVersion);
//        String extractedRole = jwtService.extractRole(token);
//        assertEquals(role, extractedRole);
//    }
//
//    @Test
//    void testExtractTokenVersion() {
//        String token = jwtService.generateToken(username, role, tokenVersion);
//        Integer extractedVersion = jwtService.extractTokenVersion(token);
//        assertEquals(tokenVersion, extractedVersion);
//    }
//
//}
//
