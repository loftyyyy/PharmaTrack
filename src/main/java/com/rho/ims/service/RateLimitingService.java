package com.rho.ims.service;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class RateLimitingService {
    private static final Logger logger = LoggerFactory.getLogger(RateLimitingService.class);

    // Store buckets with last access time
    private final Map<String, BucketEntry> buckets = new ConcurrentHashMap<>();

    private final Bandwidth loginBandwidth = Bandwidth.classic(5, Refill.intervally(5, Duration.ofMinutes(1)));
    private final Bandwidth refreshBandwidth = Bandwidth.classic(10, Refill.intervally(10, Duration.ofMinutes(1)));

    // Inner class to track last access
    private static class BucketEntry {
        final Bucket bucket;
        volatile long lastAccessTime;

        BucketEntry(Bucket bucket) {
            this.bucket = bucket;
            this.lastAccessTime = System.currentTimeMillis();
        }
    }

    // Cleanup old entries every hour
    @Scheduled(fixedRate = 3600000) // 1 hour
    public void cleanupOldBuckets() {
        long cutoffTime = System.currentTimeMillis() - (2 * 3600000); // 2 hours old

        buckets.entrySet().removeIf(entry -> {
            if (entry.getValue().lastAccessTime < cutoffTime) {
                logger.debug("Removed stale rate limit bucket for: {}", entry.getKey());
                return true;
            }
            return false;
        });
    }

    public boolean isLoginAllowed(String clientIp) {
        BucketEntry entry = buckets.computeIfAbsent(clientIp + "_login",
                k -> new BucketEntry(Bucket.builder().addLimit(loginBandwidth).build()));

        entry.lastAccessTime = System.currentTimeMillis();
        boolean allowed = entry.bucket.tryConsume(1);

        if (!allowed) {
            logger.warn("Login rate limit exceeded for IP: {}", clientIp);
        }
        return allowed;
    }

    public boolean isRefreshAllowed(String clientIp) {
        BucketEntry entry = buckets.computeIfAbsent(clientIp + "_refresh",
                k -> new BucketEntry(Bucket.builder().addLimit(refreshBandwidth).build()));

        entry.lastAccessTime = System.currentTimeMillis();
        boolean allowed = entry.bucket.tryConsume(1);

        if (!allowed) {
            logger.warn("Refresh rate limit exceeded for IP: {}", clientIp);
        }
        return allowed;
    }

    public long getRemainingLoginAttempts(String clientIp) {
        BucketEntry entry = buckets.get(clientIp + "_login");
        return entry != null ? entry.bucket.getAvailableTokens() : loginBandwidth.getCapacity();
    }

    public long getRemainingRefreshAttempts(String clientIp) {
        BucketEntry entry = buckets.get(clientIp + "_refresh");
        return entry != null ? entry.bucket.getAvailableTokens() : refreshBandwidth.getCapacity();
    }

    public void clearRateLimit(String clientIp) {
        buckets.remove(clientIp + "_login");
        buckets.remove(clientIp + "_refresh");
        logger.info("Rate limit cleared for IP: {}", clientIp);
    }
}
