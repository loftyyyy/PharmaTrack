package com.rho.ims.controller;

import com.rho.ims.api.exception.TokenRefreshException;
import com.rho.ims.dto.auth.AuthRequest;
import com.rho.ims.dto.auth.AuthResponse;
import com.rho.ims.dto.message.MessageResponse;
import com.rho.ims.dto.token.TokenRefreshRequest;
import com.rho.ims.dto.token.TokenRefreshResponse;
import com.rho.ims.dto.user.UserResponseDTO;
import com.rho.ims.model.User;
import com.rho.ims.security.JwtUtil;
import com.rho.ims.service.RefreshTokenService;
import com.rho.ims.service.RateLimitingService;
import com.rho.ims.service.TokenBlacklistService;
import com.rho.ims.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final UserService userService;
    private final RefreshTokenService refreshTokenService;
    private final TokenBlacklistService tokenBlacklistService;
    private final RateLimitingService rateLimitingService;

    public AuthController(AuthenticationManager authenticationManager,
                          JwtUtil jwtUtil,
                          UserService userService,
                          RefreshTokenService refreshTokenService,
                          TokenBlacklistService tokenBlacklistService,
                          RateLimitingService rateLimitingService) {
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
        this.userService = userService;
        this.refreshTokenService = refreshTokenService;
        this.tokenBlacklistService = tokenBlacklistService;
        this.rateLimitingService = rateLimitingService;
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody AuthRequest authRequest, HttpServletRequest request) {
        String clientIp = getClientIpAddress(request);
        logger.info("Login attempt for user: {} from IP: {}", authRequest.getUsername(), clientIp);
        
        // Check rate limiting
        if (!rateLimitingService.isLoginAllowed(clientIp)) {
            logger.warn("Login rate limit exceeded for IP: {}", clientIp);
            return ResponseEntity.status(429).body(null); // Too Many Requests
        }
        
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(authRequest.getUsername(), authRequest.getPassword())
            );

            String username = authentication.getName();
            String accessToken = jwtUtil.generateAccessToken(username);
            String refreshToken = jwtUtil.generateRefreshToken(username);

            // Save refresh token to database
            refreshTokenService.saveRefreshToken(username, refreshToken);

            User user = userService.findByName(username);
            AuthResponse response = new AuthResponse(accessToken, refreshToken, new UserResponseDTO(user));

            logger.info("Successful login for user: {}", username);
            return ResponseEntity.ok(response);
            
        } catch (BadCredentialsException e) {
            logger.warn("Invalid credentials for user: {}", authRequest.getUsername());
            throw new BadCredentialsException("Invalid username or password");
        } catch (DisabledException e) {
            logger.warn("Disabled account login attempt: {}", authRequest.getUsername());
            throw new BadCredentialsException("Account is disabled");
        } catch (LockedException e) {
            logger.warn("Locked account login attempt: {}", authRequest.getUsername());
            throw new BadCredentialsException("Account is locked");
        } catch (Exception e) {
            logger.error("Unexpected authentication error for user: {}", authRequest.getUsername(), e);
            throw new BadCredentialsException("Authentication failed");
        }
    }

    @PostMapping("/refresh")
    public ResponseEntity<TokenRefreshResponse> refreshToken(@Valid @RequestBody TokenRefreshRequest request, HttpServletRequest httpRequest) {
        String refreshToken = request.getRefreshToken();
        String clientIp = getClientIpAddress(httpRequest);
        logger.info("Token refresh attempt at {} from IP: {}", LocalDateTime.now(), clientIp);

        // Check rate limiting
        if (!rateLimitingService.isRefreshAllowed(clientIp)) {
            logger.warn("Refresh rate limit exceeded for IP: {}", clientIp);
            return ResponseEntity.status(429).body(null); // Too Many Requests
        }

        try {
            // Validate the refresh token format and signature
            if (!jwtUtil.isRefreshToken(refreshToken)) {
                logger.warn("Invalid refresh token type provided");
                throw new TokenRefreshException(refreshToken, "Token is not a refresh token!");
            }

            if (jwtUtil.isTokenExpired(refreshToken)) {
                logger.warn("Expired refresh token provided");
                refreshTokenService.deleteRefreshToken(refreshToken);
                throw new TokenRefreshException(refreshToken, "Refresh token was expired. Please make a new signin request");
            }

            // Check if refresh token exists in database and is valid
            if (!refreshTokenService.isRefreshTokenValid(refreshToken)) {
                logger.warn("Refresh token not found in database");
                throw new TokenRefreshException(refreshToken, "Refresh token is not in database!");
            }

            String username = jwtUtil.extractUsername(refreshToken);
            String newAccessToken = jwtUtil.generateAccessToken(username);
            
            // Generate new refresh token for rotation
            String newRefreshToken = jwtUtil.generateRefreshToken(username);
            refreshTokenService.saveRefreshToken(username, newRefreshToken);

            logger.info("Successful token refresh for user: {}", username);
            return ResponseEntity.ok(new TokenRefreshResponse(newAccessToken, newRefreshToken));

        } catch (TokenRefreshException e) {
            logger.warn("Token refresh failed: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            logger.error("Unexpected error during token refresh", e);
            throw new TokenRefreshException(refreshToken, "Invalid refresh token");
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String accessToken = authHeader.substring(7);

            try {
                String username = jwtUtil.extractUsername(accessToken);
                // Blacklist the access token
                tokenBlacklistService.blacklistToken(accessToken);
                // Delete all refresh tokens for this user
                refreshTokenService.deleteAllUserRefreshTokens(username);

                logger.info("Successful logout for user: {}", username);
                return ResponseEntity.ok(new MessageResponse("User logged out successfully!"));
            } catch (Exception e) {
                logger.warn("Logout failed with invalid token", e);
                return ResponseEntity.badRequest().body(new MessageResponse("Invalid token"));
            }
        }

        logger.warn("Logout attempt without token");
        return ResponseEntity.badRequest().body(new MessageResponse("No token provided"));
    }

    /**
     * Extract client IP address from request
     */
    private String getClientIpAddress(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            return xForwardedFor.split(",")[0].trim();
        }
        
        String xRealIp = request.getHeader("X-Real-IP");
        if (xRealIp != null && !xRealIp.isEmpty()) {
            return xRealIp;
        }
        
        return request.getRemoteAddr();
    }
}
