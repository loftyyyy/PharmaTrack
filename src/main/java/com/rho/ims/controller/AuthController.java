package com.rho.ims.controller;

import com.rho.ims.dto.AuthRequest;
import com.rho.ims.dto.AuthResponse;
import com.rho.ims.dto.RegisterRequest;
import com.rho.ims.dto.UserResponseDTO;
import com.rho.ims.model.User;
import com.rho.ims.security.JwtUtil;
import com.rho.ims.service.UserService;
import com.rho.ims.service.TokenBlacklistService;
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

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final UserService userService;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final TokenBlacklistService tokenBlacklistService;

    public AuthController(UserService userService,
                          AuthenticationManager authenticationManager,
                          JwtUtil jwtUtil,
                          TokenBlacklistService tokenBlacklistService) {
        this.userService = userService;
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
        this.tokenBlacklistService = tokenBlacklistService;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest registerRequest) {
        User user = userService.saveUser(registerRequest);
        return ResponseEntity.ok().body(new UserResponseDTO(user));
    }

    @PostMapping("/login")
    public AuthResponse login(@Valid @RequestBody AuthRequest authRequest) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(authRequest.getUsername(), authRequest.getPassword())
            );
            String token = jwtUtil.generateToken(authentication.getName());
            User user = userService.findByName(authRequest.getUsername());
            return new AuthResponse(token, new UserResponseDTO(user));
        } catch (Exception e) {
            // Always throw BadCredentialsException for security
            throw new BadCredentialsException("Bad credentials");
        }

    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletRequest request) {
        String token = extractTokenFromRequest(request);

        if (token != null) {
            tokenBlacklistService.blacklistToken(token);
            return ResponseEntity.ok().body(new LogoutResponse("Successfully logged out"));
        }

        return ResponseEntity.badRequest().body(new LogoutResponse("No valid token found"));
    }

    private String extractTokenFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }

    // Response DTO for logout
    public static class LogoutResponse {
        private String message;

        public LogoutResponse(String message) {
            this.message = message;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }
    }
}
