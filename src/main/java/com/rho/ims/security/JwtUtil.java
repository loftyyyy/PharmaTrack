package com.rho.ims.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.logging.LogManager;
import java.util.logging.Logger;

@Component
public class JwtUtil {
    private final Logger logger = Logger.getLogger(JwtUtil.class.getName());

    private final SecretKey SECRET_KEY;
    private final long ACCESS_TOKEN_EXPIRATION;
    private final long REFRESH_TOKEN_EXPIRATION;

    public JwtUtil(@Value("${jwt.secret}") String secretString,
                   @Value("${jwt.access-token-expiration:900000}") long accessTokenExpiration,
                   @Value("${jwt.refresh-token-expiration:604800000}") long refreshTokenExpiration) {
        this.SECRET_KEY = Keys.hmacShaKeyFor(secretString.getBytes(StandardCharsets.UTF_8));
        this.ACCESS_TOKEN_EXPIRATION = accessTokenExpiration;
        this.REFRESH_TOKEN_EXPIRATION = refreshTokenExpiration;
    }

    public String generateAccessToken(String username) {
        logger.info("Access Token Generated");
        return generateToken(username, ACCESS_TOKEN_EXPIRATION, "ACCESS");
    }

    public String generateRefreshToken(String username) {
        logger.info("Refresh Token Generated");
        return generateToken(username, REFRESH_TOKEN_EXPIRATION, "REFRESH");
    }

    private String generateToken(String username, long expiration, String tokenType) {
        return Jwts.builder()
                .subject(username)
                .claim("type", tokenType)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(SECRET_KEY)
                .compact();
    }

    public String extractUsername(String token) {
        try {
            return Jwts.parser()
                    .verifyWith(SECRET_KEY)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload()
                    .getSubject();
        } catch (JwtException e) {
            throw new RuntimeException("Invalid JWT token", e);
        }
    }

    public String extractTokenType(String token) {
        try {
            return Jwts.parser()
                    .verifyWith(SECRET_KEY)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload()
                    .get("type", String.class);
        } catch (JwtException e) {
            throw new RuntimeException("Invalid JWT token", e);
        }
    }

    public Date extractExpiration(String token) {
        try {
            return Jwts.parser()
                    .verifyWith(SECRET_KEY)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload()
                    .getExpiration();
        } catch (JwtException e) {
            throw new RuntimeException("Invalid JWT token", e);
        }
    }

    public boolean isTokenExpired(String token) {
        try {
            return extractExpiration(token).before(new Date());
        } catch (Exception e) {
            return true; // Consider invalid tokens as expired
        }
    }

    public boolean isAccessToken(String token) {
        try {
            return "ACCESS".equals(extractTokenType(token));
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isRefreshToken(String token) {
        try {
            return "REFRESH".equals(extractTokenType(token));
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isTokenValid(String token, UserDetails userDetails) {
        try {
            final String username = extractUsername(token);
            return (username.equals(userDetails.getUsername())) && 
                   !isTokenExpired(token) && 
                   isTokenSignatureValid(token) &&
                   isTokenNotBeforeValid(token);
        } catch (Exception e) {
            return false; // Invalid token
        }
    }

    public boolean isTokenValid(String token, String username) {
        try {
            final String tokenUsername = extractUsername(token);
            return (tokenUsername.equals(username)) && 
                   !isTokenExpired(token) && 
                   isTokenSignatureValid(token) &&
                   isTokenNotBeforeValid(token);
        } catch (Exception e) {
            return false; // Invalid token
        }
    }

    /**
     * Validate token signature
     */
    public boolean isTokenSignatureValid(String token) {
        try {
            Jwts.parser()
                .verifyWith(SECRET_KEY)
                .build()
                .parseSignedClaims(token);
            return true;
        } catch (JwtException e) {
            return false;
        }
    }

    /**
     * Check if token is not used before its issued time
     */
    public boolean isTokenNotBeforeValid(String token) {
        try {
            Date notBefore = extractAllClaims(token).getNotBefore();
            if (notBefore == null) {
                return true; // No notBefore claim means it's valid
            }
            return !notBefore.after(new Date());
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Extract issued at time from token
     */
    public Date extractIssuedAt(String token) {
        try {
            return extractAllClaims(token).getIssuedAt();
        } catch (JwtException e) {
            throw new RuntimeException("Invalid JWT token", e);
        }
    }

    /**
     * Check if token was issued recently (within last 24 hours for security)
     */
    public boolean isTokenRecentlyIssued(String token) {
        try {
            Date issuedAt = extractIssuedAt(token);
            long timeDiff = System.currentTimeMillis() - issuedAt.getTime();
            return timeDiff <= 86400000; // 24 hours in milliseconds
        } catch (Exception e) {
            return false;
        }
    }

    // Helper method to extract all claims from token
    private Claims extractAllClaims(String token) {
        try {
            return Jwts.parser()
                    .verifyWith(SECRET_KEY)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (JwtException e) {
            throw new RuntimeException("Invalid JWT token", e);
        }
    }
}
