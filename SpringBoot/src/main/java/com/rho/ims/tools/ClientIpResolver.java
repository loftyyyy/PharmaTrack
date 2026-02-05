package com.rho.ims.tools;

import jakarta.servlet.http.HttpServletRequest;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Set;
import java.util.regex.Pattern;

@Component
public class ClientIpResolver {
    private static final Logger logger = LogManager.getLogger(ClientIpResolver.class);

    // Simple pattern to quickly reject obviously invalid input (not comprehensive)
    // This is just a first-pass filter - InetAddress does the real validation
    private static final Pattern IP_BASIC_PATTERN = Pattern.compile(
            "^[0-9a-fA-F:.]+$"
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
     *
     * This method uses a two-layer approach:
     * 1. Quick regex check to reject obviously invalid input (performance optimization)
     * 2. InetAddress validation for accurate IPv4/IPv6 verification
     *
     * Note: We rely primarily on InetAddress because:
     * - IPv6 has many valid formats (compressed, IPv4-mapped, link-local, etc.)
     * - Writing comprehensive regex for all IPv6 formats is error-prone
     * - InetAddress.getByName() handles all edge cases correctly
     *
     * @param ip the IP address string to validate
     * @return true if valid IPv4 or IPv6 address, false otherwise
     */
    private boolean isValidIpAddress(String ip) {
        // Basic sanity checks
        if (ip == null || ip.isEmpty()) {
            return false;
        }

        // IPv6 can be up to 45 chars with zone ID (e.g., "fe80::1%eth0")
        // IPv4 is max 15 chars ("255.255.255.255")
        if (ip.length() > 50) {
            return false;
        }

        // Quick pattern check to reject obviously invalid input
        // This catches injection attempts and malformed strings quickly
        if (!IP_BASIC_PATTERN.matcher(ip).matches()) {
            return false;
        }

        // Authoritative validation using InetAddress
        // This handles all valid IPv4 and IPv6 formats:
        // - Standard IPv4: 192.168.1.1
        // - Standard IPv6: 2001:db8::1
        // - Compressed IPv6: ::1, 2001:db8::8a2e:370:7334
        // - IPv4-mapped IPv6: ::ffff:192.0.2.1
        // - Link-local with zone: fe80::1%eth0
        try {
            InetAddress addr = InetAddress.getByName(ip);

            // Additional check: ensure it's an IP address, not a hostname
            // InetAddress.getByName() can resolve hostnames, which we don't want
            String hostAddress = addr.getHostAddress();

            // For IPv6 with zone ID (e.g., "fe80::1%eth0"), compare without zone
            if (ip.contains("%")) {
                String ipWithoutZone = ip.split("%")[0];
                String addrWithoutZone = hostAddress.split("%")[0];
                return ipWithoutZone.equalsIgnoreCase(addrWithoutZone);
            }

            // For regular addresses, compare normalized forms
            // Use equalsIgnoreCase for case-insensitive IPv6 comparison
            return ip.equalsIgnoreCase(hostAddress);

        } catch (UnknownHostException e) {
            logger.debug("Invalid IP address format: {}", ip);
            return false;
        }
    }
}
