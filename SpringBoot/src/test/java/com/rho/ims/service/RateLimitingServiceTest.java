//package com.rho.ims.service;
//
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//
//import static org.junit.jupiter.api.Assertions.*;
//
//class RateLimitingServiceTest {
//
//    private RateLimitingService rateLimitingService;
//
//    @BeforeEach
//    void setUp() {
//        rateLimitingService = new RateLimitingService();
//    }
//
//    @Test
//    void testLoginRateLimit() {
//        String clientIp = "192.168.1.1";
//
//        // First 5 attempts should be allowed
//        for (int i = 0; i < 5; i++) {
//            assertTrue(rateLimitingService.isLoginAllowed(clientIp),
//                "Login attempt " + (i + 1) + " should be allowed");
//        }
//
//        // 6th attempt should be blocked
//        assertFalse(rateLimitingService.isLoginAllowed(clientIp),
//            "6th login attempt should be blocked");
//    }
//
//    @Test
//    void testRefreshRateLimit() {
//        String clientIp = "192.168.1.2";
//
//        // First 10 attempts should be allowed
//        for (int i = 0; i < 10; i++) {
//            assertTrue(rateLimitingService.isRefreshAllowed(clientIp),
//                "Refresh attempt " + (i + 1) + " should be allowed");
//        }
//
//        // 11th attempt should be blocked
//        assertFalse(rateLimitingService.isRefreshAllowed(clientIp),
//            "11th refresh attempt should be blocked");
//    }
//
//    @Test
//    void testDifferentIpAddresses() {
//        String ip1 = "192.168.1.1";
//        String ip2 = "192.168.1.2";
//
//        // Exhaust rate limit for IP1
//        for (int i = 0; i < 5; i++) {
//            rateLimitingService.isLoginAllowed(ip1);
//        }
//
//        // IP1 should be blocked
//        assertFalse(rateLimitingService.isLoginAllowed(ip1));
//
//        // IP2 should still be allowed
//        assertTrue(rateLimitingService.isLoginAllowed(ip2));
//    }
//
//    @Test
//    void testGetRemainingLoginAttempts() {
//        String clientIp = "192.168.1.3";
//
//        // Initially should have 5 attempts
//        assertEquals(5, rateLimitingService.getRemainingLoginAttempts(clientIp));
//
//        // After 2 attempts, should have 3 remaining
//        rateLimitingService.isLoginAllowed(clientIp);
//        rateLimitingService.isLoginAllowed(clientIp);
//        assertEquals(3, rateLimitingService.getRemainingLoginAttempts(clientIp));
//    }
//
//    @Test
//    void testGetRemainingRefreshAttempts() {
//        String clientIp = "192.168.1.4";
//
//        // Initially should have 10 attempts
//        assertEquals(10, rateLimitingService.getRemainingRefreshAttempts(clientIp));
//
//        // After 3 attempts, should have 7 remaining
//        rateLimitingService.isRefreshAllowed(clientIp);
//        rateLimitingService.isRefreshAllowed(clientIp);
//        rateLimitingService.isRefreshAllowed(clientIp);
//        assertEquals(7, rateLimitingService.getRemainingRefreshAttempts(clientIp));
//    }
//
//    @Test
//    void testClearRateLimit() {
//        String clientIp = "192.168.1.5";
//
//        // Exhaust rate limit
//        for (int i = 0; i < 5; i++) {
//            rateLimitingService.isLoginAllowed(clientIp);
//        }
//
//        // Should be blocked
//        assertFalse(rateLimitingService.isLoginAllowed(clientIp));
//
//        // Clear rate limit
//        rateLimitingService.clearRateLimit(clientIp);
//
//        // Should be allowed again
//        assertTrue(rateLimitingService.isLoginAllowed(clientIp));
//    }
//
//    @Test
//    void testConcurrentAccess() throws InterruptedException {
//        String clientIp = "192.168.1.6";
//        int numberOfThreads = 10;
//        Thread[] threads = new Thread[numberOfThreads];
//        boolean[] results = new boolean[numberOfThreads];
//
//        // Create multiple threads trying to access the same IP
//        for (int i = 0; i < numberOfThreads; i++) {
//            final int index = i;
//            threads[i] = new Thread(() -> {
//                results[index] = rateLimitingService.isLoginAllowed(clientIp);
//            });
//        }
//
//        // Start all threads
//        for (Thread thread : threads) {
//            thread.start();
//        }
//
//        // Wait for all threads to complete
//        for (Thread thread : threads) {
//            thread.join();
//        }
//
//        // Count successful attempts
//        int successCount = 0;
//        for (boolean result : results) {
//            if (result) successCount++;
//        }
//
//        // Should have exactly 5 successful attempts
//        assertEquals(5, successCount);
//    }
//}
