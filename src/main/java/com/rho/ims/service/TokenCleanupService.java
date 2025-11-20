package com.rho.ims.service;

import com.rho.ims.respository.RefreshTokenRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class TokenCleanupService {

    private static final Logger logger = LoggerFactory.getLogger(TokenCleanupService.class);
    private final RefreshTokenRepository refreshTokenRepository;

    public TokenCleanupService(RefreshTokenRepository refreshTokenRepository) {
        this.refreshTokenRepository = refreshTokenRepository;
    }

    /**
     * Clean up expired refresh tokens every hour
     */
    @Scheduled(fixedRate = 3600000) // Every hour
    @Transactional
    public void cleanupExpiredTokens() {

        try {
            LocalDateTime now = LocalDateTime.now();
            int deletedCount = refreshTokenRepository.deleteExpiredTokens(now);
            
            if (deletedCount > 0) {
                logger.info("Cleaned up {} expired refresh tokens", deletedCount);
            }
        } catch (Exception e) {
            logger.error("Error during token cleanup", e);
        }
    }

    /**
     * Clean up expired tokens manually (for testing or manual triggers)
     */
    @Transactional
    public int manualCleanup() {
        LocalDateTime now = LocalDateTime.now();
        int deletedCount = refreshTokenRepository.deleteExpiredTokens(now);
        logger.info("Manual cleanup: removed {} expired tokens", deletedCount);
        return deletedCount;
    }
}
