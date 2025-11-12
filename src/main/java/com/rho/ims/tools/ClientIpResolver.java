package com.rho.ims.tools;

import jakarta.servlet.http.HttpServletRequest;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Set;
import java.util.regex.Pattern;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class ClientIpResolver {
    private static final Logger logger = LogManager.getLogger(ClientIpResolver.class);

    // IPv4 pattern (strict validation)
    private static final Pattern IPV4_PATTERN = Pattern.compile(
            "^((25[0-5]|(2[0-4]|1\\d|[1-9]|)\\d)\\.?\\b){4}$"
    );

    // IPv6 pattern (simplified - full validation is complex)
    private static final Pattern IPV6_PATTERN = Pattern.compile(
            "^(?:[0-9a-fA-F]{1,4}:){7}[0-9a-fA-F]{1,4}$|" +
                    "^::(?:[0-9a-fA-F]{1,4}:){0,6}[0-9a-fA-F]{1,4}$|" +
                    "^(?:[0-9a-fA-F]{1,4}:){1,6}:$"
    );

    private final Set<String> trustedProxies;

    public ClientIpResolver(@Value("${security.trusted-proxies}") Set<String> trustedProxies) {
        this.trustedProxies = trustedProxies;
        logger.info("ClientIpResolver initialized with trusted proxies: {}", trustedProxies);
    }

    /**
     * Extracts the real client IP address from the request.
     *
     * Security considerations:
     * - Only trusts X-Forwarded-For from verified trusted proxies
     * - Works backwards from rightmost IP to prevent spoofing
     * - Skips all trusted proxy IPs to find the real client
     * - Validates all IPs to prevent injection attacks
     *
     * @param request the HTTP servlet request
     * @return the real client IP address, or "unknown" if it cannot be determined
     */
    public String getClientIp(HttpServletRequest request) {
        String remoteAddr = request.getRemoteAddr();

        // Only process X-Forwarded-For if the direct connection is from a trusted proxy
        if (trustedProxies.contains(remoteAddr)) {
            String xForwardedFor = request.getHeader("X-Forwarded-For");

            if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
                String[] ips = xForwardedFor.split(",");

                // Work backwards from rightmost IP (closest to our infrastructure)
                // This prevents clients from spoofing IPs by prepending fake values
                for (int i = ips.length - 1; i >= 0; i--) {
                    String candidateIp = ips[i].trim();

                    // Skip trusted proxies - we want the first non-trusted IP
                    if (trustedProxies.contains(candidateIp)) {
                        logger.debug("Skipping trusted proxy IP: {}", candidateIp);
                        continue;
                    }

                    // First valid IP that's NOT a trusted proxy is the real client
                    if (isValidIpAddress(candidateIp)) {
                        logger.debug("Resolved client IP: {} from X-Forwarded-For: {}",
                                candidateIp, xForwardedFor);
                        return candidateIp;
                    }

                    logger.warn("Invalid IP address in X-Forwarded-For: {}", candidateIp);
                }

                // If we've gone through all IPs and they're all trusted proxies
                logger.warn("All IPs in X-Forwarded-For are trusted proxies or invalid. " +
                        "Remote: {}, X-Forwarded-For: {}", remoteAddr, xForwardedFor);
            }
        }

        // Fallback to remote address (direct connection or no valid X-Forwarded-For)
        if (isValidIpAddress(remoteAddr)) {
            logger.debug("Using remote address as client IP: {}", remoteAddr);
            return remoteAddr;
        }

        logger.error("Unable to determine valid client IP. Remote address: {}", remoteAddr);
        return "unknown";
    }

    /**
     * Validates if a string is a valid IP address (IPv4 or IPv6).
     * Uses both regex and InetAddress validation for defense in depth.
     *
     * @param ip the IP address string to validate
     * @return true if valid, false otherwise
     */
    private boolean isValidIpAddress(String ip) {
        if (ip == null || ip.isEmpty() || ip.length() > 45) {
            return false;
        }

        // Quick regex check first (faster, prevents obvious injection attempts)
        if (!IPV4_PATTERN.matcher(ip).matches() &&
                !IPV6_PATTERN.matcher(ip).matches()) {
            return false;
        }

        // Secondary validation using InetAddress (catches edge cases)
        try {
            InetAddress.getByName(ip);
            return true;
        } catch (UnknownHostException e) {
            logger.debug("Invalid IP address format: {}", ip);
            return false;
        }
    }
}