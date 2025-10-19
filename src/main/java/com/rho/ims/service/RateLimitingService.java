package com.rho.ims.service;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class RateLimitingService {

    private static final Logger logger = LoggerFactory.getLogger(RateLimitingService.class);
    
    // Store buckets per IP address
    private final Map<String, Bucket> buckets = new ConcurrentHashMap<>();
    
    // Login rate limit: 5 attempts per minute
    private final Bandwidth loginBandwidth = Bandwidth.classic(5, Refill.intervally(5, Duration.ofMinutes(1)));
    
    // Refresh token rate limit: 10 attempts per minute
    private final Bandwidth refreshBandwidth = Bandwidth.classic(10, Refill.intervally(10, Duration.ofMinutes(1)));

    /**
     * Check if login is allowed for the given IP
     * @param clientIp The client IP address
     * @return true if login is allowed, false if rate limited
     */
    public boolean isLoginAllowed(String clientIp) {
        Bucket bucket = buckets.computeIfAbsent(clientIp + "_login", 
            k -> Bucket.builder().addLimit(loginBandwidth).build());
        
        boolean allowed = bucket.tryConsume(1);
        if (!allowed) {
            logger.warn("Login rate limit exceeded for IP: {}", clientIp);
        }
        return allowed;
    }

    /**
     * Check if token refresh is allowed for the given IP
     * @param clientIp The client IP address
     * @return true if refresh is allowed, false if rate limited
     */
    public boolean isRefreshAllowed(String clientIp) {
        Bucket bucket = buckets.computeIfAbsent(clientIp + "_refresh", 
            k -> Bucket.builder().addLimit(refreshBandwidth).build());
        
        boolean allowed = bucket.tryConsume(1);
        if (!allowed) {
            logger.warn("Refresh rate limit exceeded for IP: {}", clientIp);
        }
        return allowed;
    }

    /**
     * Get remaining tokens for login attempts
     * @param clientIp The client IP address
     * @return number of remaining login attempts
     */
    public long getRemainingLoginAttempts(String clientIp) {
        Bucket bucket = buckets.get(clientIp + "_login");
        return bucket != null ? bucket.getAvailableTokens() : loginBandwidth.getCapacity();
    }

    /**
     * Get remaining tokens for refresh attempts
     * @param clientIp The client IP address
     * @return number of remaining refresh attempts
     */
    public long getRemainingRefreshAttempts(String clientIp) {
        Bucket bucket = buckets.get(clientIp + "_refresh");
        return bucket != null ? bucket.getAvailableTokens() : refreshBandwidth.getCapacity();
    }

    /**
     * Clear rate limit for a specific IP (useful for testing or admin operations)
     * @param clientIp The client IP address
     */
    public void clearRateLimit(String clientIp) {
        buckets.remove(clientIp + "_login");
        buckets.remove(clientIp + "_refresh");
        logger.info("Rate limit cleared for IP: {}", clientIp);
    }
}
