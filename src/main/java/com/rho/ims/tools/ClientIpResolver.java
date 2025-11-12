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
    }

    public String getClientIp(HttpServletRequest request) {
        String remoteAddr = request.getRemoteAddr();

        // Only process X-Forwarded-For from trusted proxies
        if (trustedProxies.contains(remoteAddr)) {
            String xForwardedFor = request.getHeader("X-Forwarded-For");

            if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
                // Split and get the leftmost IP (original client)
                String[] ips = xForwardedFor.split(",");

                for (String ip : ips) {
                    String clientIp = ip.trim();

                    // Validate the IP address
                    if (isValidIpAddress(clientIp)) {
                        return clientIp;
                    }
                }

                // If no valid IP found in X-Forwarded-For, log warning
                logger.warn("Invalid X-Forwarded-For header from trusted proxy {}: {}",
                        remoteAddr, xForwardedFor);
            }
        }

        // Fallback to remote address (validate it too)
        return isValidIpAddress(remoteAddr) ? remoteAddr : "unknown";
    }

    /**
     * Validates if a string is a valid IP address (IPv4 or IPv6)
     * Uses both regex and InetAddress validation for defense in depth
     */
    private boolean isValidIpAddress(String ip) {
        if (ip == null || ip.isEmpty() || ip.length() > 45) {
            return false;
        }

        // Quick regex check first (faster)
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
