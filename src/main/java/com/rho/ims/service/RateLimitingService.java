package com.rho.ims.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;

import jakarta.annotation.PostConstruct;
import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class RateLimitingService {

    private static final Logger logger = LoggerFactory.getLogger(RateLimitingService.class);

    // Store buckets with last access time
    private final Map<String, BucketEntry> buckets = new ConcurrentHashMap<>();

    // Configurable rate limit settings
    @Value("${security.rate-limit.login.attempts:5}")
    private int loginAttempts;

    @Value("${security.rate-limit.login.window:60}")
    private int loginWindowSeconds;

    @Value("${security.rate-limit.refresh.attempts:10}")
    private int refreshAttempts;

    @Value("${security.rate-limit.refresh.window:60}")
    private int refreshWindowSeconds;

    @Value("${security.token-cleanup.enabled:true}")
    private boolean cleanupEnabled;

    @Value("${security.token-cleanup.retention-hours}")
    private long retentionHours;

    private Bandwidth loginBandwidth;
    private Bandwidth refreshBandwidth;

    @PostConstruct
    public void init() {
        // Initialize bandwidth configurations after properties are injected
        this.loginBandwidth = Bandwidth.classic(
                loginAttempts,
                Refill.intervally(loginAttempts, Duration.ofSeconds(loginWindowSeconds))
        );
        this.refreshBandwidth = Bandwidth.classic(
                refreshAttempts,
                Refill.intervally(refreshAttempts, Duration.ofSeconds(refreshWindowSeconds))
        );

        logger.info("Rate limiting initialized - Login: {} attempts per {} seconds, Refresh: {} attempts per {} seconds",
                loginAttempts, loginWindowSeconds, refreshAttempts, refreshWindowSeconds);
        logger.info("Token cleanup enabled: {}", cleanupEnabled);

    }

    // Inner class to track last access
    private static class BucketEntry {
        final Bucket bucket;
        final AtomicLong lastAccessTime;

        BucketEntry(Bucket bucket) {
            this.bucket = bucket;
            this.lastAccessTime = new AtomicLong(System.currentTimeMillis());
        }
    }

    // Cleanup old entries - runs based on configured interval
    @Scheduled(fixedRateString = "${security.token-cleanup.interval:3600000}")
    @ConditionalOnProperty(name = "security.token-cleanup.enabled", havingValue = "true", matchIfMissing = true)
    public void cleanupOldBuckets() {

        long cutoffTime = System.currentTimeMillis() - (retentionHours * 3600000); // 2 hours old
        int removedCount = 0;

        for (Map.Entry<String, BucketEntry> entry : buckets.entrySet()) {
            if (entry.getValue().lastAccessTime.get() < cutoffTime) {
                if (buckets.remove(entry.getKey()) != null) {
                    removedCount++;
                    logger.debug("Removed stale rate limit bucket for: {}", entry.getKey());
                }
            }
        }

        if (removedCount > 0) {
            logger.info("Cleanup completed: removed {} stale rate limit buckets", removedCount);
        }

    }

    public boolean isLoginAllowed(String clientIp) {

        BucketEntry entry = buckets.computeIfAbsent(clientIp + "_login",
                k -> new BucketEntry(Bucket.builder().addLimit(loginBandwidth).build()));
        entry.lastAccessTime.set(System.currentTimeMillis());
        boolean allowed = entry.bucket.tryConsume(1);
        if (!allowed) {
            logger.warn("Login rate limit exceeded for IP: {} - {} attempts allowed per {} seconds",
                    clientIp, loginAttempts, loginWindowSeconds);
        }
        return allowed;

    }

    public boolean isRefreshAllowed(String clientIp) {

        BucketEntry entry = buckets.computeIfAbsent(clientIp + "_refresh",
                k -> new BucketEntry(Bucket.builder().addLimit(refreshBandwidth).build()));
        entry.lastAccessTime.set(System.currentTimeMillis());
        boolean allowed = entry.bucket.tryConsume(1);
        if (!allowed) {
            logger.warn("Refresh rate limit exceeded for IP: {} - {} attempts allowed per {} seconds",
                    clientIp, refreshAttempts, refreshWindowSeconds);
        }
        return allowed;

    }

    public long getRemainingLoginAttempts(String clientIp) {
        BucketEntry entry = buckets.get(clientIp + "_login");
        return entry != null ? entry.bucket.getAvailableTokens() : loginAttempts;
    }

    public long getRemainingRefreshAttempts(String clientIp) {
        BucketEntry entry = buckets.get(clientIp + "_refresh");
        return entry != null ? entry.bucket.getAvailableTokens() : refreshAttempts;
    }

    public void clearRateLimit(String clientIp) {
        buckets.remove(clientIp + "_login");
        buckets.remove(clientIp + "_refresh");
        logger.info("Rate limit cleared for IP: {}", clientIp);
    }

    // Utility method to get current configuration
    public String getConfiguration() {
        return String.format("Login: %d/%ds, Refresh: %d/%ds, Cleanup: %s",
                loginAttempts, loginWindowSeconds,
                refreshAttempts, refreshWindowSeconds,
                cleanupEnabled ? "enabled" : "disabled");
    }
}
