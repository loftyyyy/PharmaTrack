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
import com.rho.ims.service.TokenBlacklistService;
import com.rho.ims.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
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

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final UserService userService;
    private final RefreshTokenService refreshTokenService;
    private final TokenBlacklistService tokenBlacklistService;

    public AuthController(AuthenticationManager authenticationManager,
                          JwtUtil jwtUtil,
                          UserService userService,
                          RefreshTokenService refreshTokenService,
                          TokenBlacklistService tokenBlacklistService) {
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
        this.userService = userService;
        this.refreshTokenService = refreshTokenService;
        this.tokenBlacklistService = tokenBlacklistService;
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody AuthRequest authRequest) {
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

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            throw new BadCredentialsException("Bad credentials");
        }
    }

    @PostMapping("/refresh")
    public ResponseEntity<TokenRefreshResponse> refreshToken(@Valid @RequestBody TokenRefreshRequest request) {
        String refreshToken = request.getRefreshToken();
        System.out.println("TOKEN REFRESHED at " + LocalDateTime.now());

        try {
            // Validate the refresh token format and signature
            if (!jwtUtil.isRefreshToken(refreshToken)) {
                throw new TokenRefreshException(refreshToken, "Token is not a refresh token!");
            }

            if (jwtUtil.isTokenExpired(refreshToken)) {
                refreshTokenService.deleteRefreshToken(refreshToken);
                throw new TokenRefreshException(refreshToken, "Refresh token was expired. Please make a new signin request");
            }

            // Check if refresh token exists in database and is valid
            if (!refreshTokenService.isRefreshTokenValid(refreshToken)) {
                throw new TokenRefreshException(refreshToken, "Refresh token is not in database!");
            }

            String username = jwtUtil.extractUsername(refreshToken);
            String newAccessToken = jwtUtil.generateAccessToken(username);

            return ResponseEntity.ok(new TokenRefreshResponse(newAccessToken, refreshToken));

        } catch (Exception e) {
            if (e instanceof TokenRefreshException) {
                throw e;
            }
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

                return ResponseEntity.ok(new MessageResponse("User logged out successfully!"));
            } catch (Exception e) {
                return ResponseEntity.badRequest().body(new MessageResponse("Invalid token"));
            }
        }

        return ResponseEntity.badRequest().body(new MessageResponse("No token provided"));
    }
}
