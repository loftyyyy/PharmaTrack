package com.rho.ims.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TokenBlacklistServiceTest {

    private TokenBlacklistService tokenBlacklistService;

    @BeforeEach
    void setUp() {
        tokenBlacklistService = new TokenBlacklistService();
    }

    @Test
    void testBlacklistToken() {
        String token = "test.jwt.token";
        
        // Initially token should not be blacklisted
        assertFalse(tokenBlacklistService.isTokenBlacklisted(token));
        
        // Blacklist the token
        tokenBlacklistService.blacklistToken(token);
        
        // Token should now be blacklisted
        assertTrue(tokenBlacklistService.isTokenBlacklisted(token));
    }

    @Test
    void testBlacklistNullToken() {
        // Should handle null token gracefully
        tokenBlacklistService.blacklistToken(null);
        assertFalse(tokenBlacklistService.isTokenBlacklisted(null));
    }

    @Test
    void testBlacklistEmptyToken() {
        // Should handle empty token gracefully
        tokenBlacklistService.blacklistToken("");
        tokenBlacklistService.blacklistToken("   ");
        assertFalse(tokenBlacklistService.isTokenBlacklisted(""));
        assertFalse(tokenBlacklistService.isTokenBlacklisted("   "));
    }

    @Test
    void testIsTokenBlacklistedWithNull() {
        // Should return false for null token
        assertFalse(tokenBlacklistService.isTokenBlacklisted(null));
    }

    @Test
    void testIsTokenBlacklistedWithEmptyToken() {
        // Should return false for empty token
        assertFalse(tokenBlacklistService.isTokenBlacklisted(""));
        assertFalse(tokenBlacklistService.isTokenBlacklisted("   "));
    }

    @Test
    void testGetBlacklistedTokenCount() {
        // Initially should be 0
        assertEquals(0, tokenBlacklistService.getBlacklistedTokenCount());
        
        // Add some tokens
        tokenBlacklistService.blacklistToken("token1");
        tokenBlacklistService.blacklistToken("token2");
        tokenBlacklistService.blacklistToken("token3");
        
        // Should be 3
        assertEquals(3, tokenBlacklistService.getBlacklistedTokenCount());
    }

    @Test
    void testClearBlacklist() {
        // Add some tokens
        tokenBlacklistService.blacklistToken("token1");
        tokenBlacklistService.blacklistToken("token2");
        assertEquals(2, tokenBlacklistService.getBlacklistedTokenCount());
        
        // Clear blacklist
        tokenBlacklistService.clearBlacklist();
        
        // Should be 0
        assertEquals(0, tokenBlacklistService.getBlacklistedTokenCount());
        
        // Tokens should no longer be blacklisted
        assertFalse(tokenBlacklistService.isTokenBlacklisted("token1"));
        assertFalse(tokenBlacklistService.isTokenBlacklisted("token2"));
    }

    @Test
    void testConcurrentBlacklisting() throws InterruptedException {
        int numberOfThreads = 10;
        Thread[] threads = new Thread[numberOfThreads];
        
        // Create multiple threads adding tokens
        for (int i = 0; i < numberOfThreads; i++) {
            final int index = i;
            threads[i] = new Thread(() -> {
                tokenBlacklistService.blacklistToken("token" + index);
            });
        }
        
        // Start all threads
        for (Thread thread : threads) {
            thread.start();
        }
        
        // Wait for all threads to complete
        for (Thread thread : threads) {
            thread.join();
        }
        
        // Should have exactly 10 tokens
        assertEquals(10, tokenBlacklistService.getBlacklistedTokenCount());
        
        // All tokens should be blacklisted
        for (int i = 0; i < numberOfThreads; i++) {
            assertTrue(tokenBlacklistService.isTokenBlacklisted("token" + i));
        }
    }

    @Test
    void testDuplicateTokenBlacklisting() {
        String token = "duplicate.token";
        
        // Blacklist the same token multiple times
        tokenBlacklistService.blacklistToken(token);
        tokenBlacklistService.blacklistToken(token);
        tokenBlacklistService.blacklistToken(token);
        
        // Should still be blacklisted and count should be 1
        assertTrue(tokenBlacklistService.isTokenBlacklisted(token));
        assertEquals(1, tokenBlacklistService.getBlacklistedTokenCount());
    }
}
