package com.rho.ims.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

class JwtUtilTest {

    private JwtUtil jwtUtil;
    private final String TEST_SECRET = "testSecretKeyForJWTTestingPurposesOnly123456789";
    private final long ACCESS_TOKEN_EXPIRATION = 900000; // 15 minutes
    private final long REFRESH_TOKEN_EXPIRATION = 604800000; // 7 days

    @BeforeEach
    void setUp() {
        jwtUtil = new JwtUtil(TEST_SECRET, ACCESS_TOKEN_EXPIRATION, REFRESH_TOKEN_EXPIRATION);
    }

    @Test
    void testGenerateAccessToken() {
        String username = "testuser";
        String token = jwtUtil.generateAccessToken(username);
        
        assertNotNull(token);
        assertTrue(token.contains("."));
        assertEquals(username, jwtUtil.extractUsername(token));
        assertTrue(jwtUtil.isAccessToken(token));
        assertFalse(jwtUtil.isRefreshToken(token));
    }

    @Test
    void testGenerateRefreshToken() {
        String username = "testuser";
        String token = jwtUtil.generateRefreshToken(username);
        
        assertNotNull(token);
        assertTrue(token.contains("."));
        assertEquals(username, jwtUtil.extractUsername(token));
        assertTrue(jwtUtil.isRefreshToken(token));
        assertFalse(jwtUtil.isAccessToken(token));
    }

    @Test
    void testTokenExpiration() throws InterruptedException {
        String username = "testuser";
        String token = jwtUtil.generateAccessToken(username);
        
        // Token should be valid initially
        assertFalse(jwtUtil.isTokenExpired(token));
        
        // Create a token with very short expiration (1ms)
        ReflectionTestUtils.setField(jwtUtil, "ACCESS_TOKEN_EXPIRATION", 1L);
        String expiredToken = jwtUtil.generateAccessToken(username);
        
        // Wait for token to expire
        Thread.sleep(10);
        assertTrue(jwtUtil.isTokenExpired(expiredToken));
    }

    @Test
    void testTokenValidationWithUserDetails() {
        String username = "testuser";
        String token = jwtUtil.generateAccessToken(username);
        
        UserDetails userDetails = User.builder()
                .username(username)
                .password("password")
                .authorities("ROLE_USER")
                .build();
        
        assertTrue(jwtUtil.isTokenValid(token, userDetails));
        
        // Test with wrong username
        UserDetails wrongUser = User.builder()
                .username("wronguser")
                .password("password")
                .authorities("ROLE_USER")
                .build();
        
        assertFalse(jwtUtil.isTokenValid(token, wrongUser));
    }

    @Test
    void testTokenSignatureValidation() {
        String username = "testuser";
        String validToken = jwtUtil.generateAccessToken(username);
        
        assertTrue(jwtUtil.isTokenSignatureValid(validToken));
        
        // Test with tampered token
        String tamperedToken = validToken.substring(0, validToken.length() - 1) + "X";
        assertFalse(jwtUtil.isTokenSignatureValid(tamperedToken));
    }

    @Test
    void testTokenNotBeforeValidation() {
        String username = "testuser";
        String token = jwtUtil.generateAccessToken(username);
        
        // Token should be valid (no notBefore claim)
        assertTrue(jwtUtil.isTokenNotBeforeValid(token));
    }

    @Test
    void testTokenRecentlyIssued() {
        String username = "testuser";
        String token = jwtUtil.generateAccessToken(username);
        
        // Token should be recently issued
        assertTrue(jwtUtil.isTokenRecentlyIssued(token));
    }

    @Test
    void testExtractUsernameFromInvalidToken() {
        String invalidToken = "invalid.token.here";
        
        assertThrows(RuntimeException.class, () -> {
            jwtUtil.extractUsername(invalidToken);
        });
    }

    @Test
    void testExtractTokenType() {
        String username = "testuser";
        String accessToken = jwtUtil.generateAccessToken(username);
        String refreshToken = jwtUtil.generateRefreshToken(username);
        
        assertEquals("ACCESS", jwtUtil.extractTokenType(accessToken));
        assertEquals("REFRESH", jwtUtil.extractTokenType(refreshToken));
    }

    @Test
    void testExtractExpiration() {
        String username = "testuser";
        String token = jwtUtil.generateAccessToken(username);
        
        Date expiration = jwtUtil.extractExpiration(token);
        assertNotNull(expiration);
        assertTrue(expiration.after(new Date()));
    }

    @Test
    void testExtractIssuedAt() {
        String username = "testuser";
        String token = jwtUtil.generateAccessToken(username);
        
        Date issuedAt = jwtUtil.extractIssuedAt(token);
        assertNotNull(issuedAt);
        assertTrue(issuedAt.before(new Date()) || issuedAt.equals(new Date()));
    }
}
