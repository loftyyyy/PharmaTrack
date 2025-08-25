package com.rho.ims.service;

import org.springframework.stereotype.Service;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class TokenBlacklistService {

    // Use ConcurrentHashMap for thread safety
    private final Set<String> blacklistedTokens = ConcurrentHashMap.newKeySet();

    /**
     * Add a token to the blacklist
     * @param token The JWT token to blacklist
     */
    public void blacklistToken(String token) {
        if (token != null && !token.trim().isEmpty()) {
            blacklistedTokens.add(token);
        }
    }

    /**
     * Check if a token is blacklisted
     * @param token The JWT token to check
     * @return true if the token is blacklisted, false otherwise
     */
    public boolean isTokenBlacklisted(String token) {
        if (token == null || token.trim().isEmpty()) {
            return false;
        }
        return blacklistedTokens.contains(token);
    }

    /**
     * Get the current number of blacklisted tokens (useful for monitoring)
     * @return The count of blacklisted tokens
     */
    public int getBlacklistedTokenCount() {
        return blacklistedTokens.size();
    }

    /**
     * Clear all blacklisted tokens (useful for testing or maintenance)
     */
    public void clearBlacklist() {
        blacklistedTokens.clear();
    }
}
