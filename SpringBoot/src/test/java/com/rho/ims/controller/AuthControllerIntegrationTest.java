package com.rho.ims.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rho.ims.dto.auth.AuthRequest;
import com.rho.ims.dto.token.TokenRefreshRequest;
import com.rho.ims.model.User;
import com.rho.ims.respository.RefreshTokenRepository;
import com.rho.ims.respository.UserRepository;
import com.rho.ims.security.JwtUtil;
import com.rho.ims.service.RateLimitingService;
import com.rho.ims.service.RefreshTokenService;
import com.rho.ims.service.TokenBlacklistService;
import com.rho.ims.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureWebMvc
@ActiveProfiles("test")
class AuthControllerIntegrationTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    @MockBean
    private AuthenticationManager authenticationManager;

    @MockBean
    private UserService userService;

    @MockBean
    private RefreshTokenService refreshTokenService;

    @MockBean
    private TokenBlacklistService tokenBlacklistService;

    @MockBean
    private RateLimitingService rateLimitingService;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    @Autowired
    private UserRepository userRepository;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        objectMapper = new ObjectMapper();
        
        // Mock rate limiting to allow requests
        when(rateLimitingService.isLoginAllowed(any())).thenReturn(true);
        when(rateLimitingService.isRefreshAllowed(any())).thenReturn(true);
    }

    @Test
    void testSuccessfulLogin() throws Exception {
        // Arrange
        AuthRequest authRequest = new AuthRequest();
        authRequest.setUsername("testuser");
        authRequest.setPassword("password");

        User mockUser = new User();
        mockUser.setUsername("testuser");
        mockUser.setId(1L);

        Authentication mockAuth = mock(Authentication.class);
        when(mockAuth.getName()).thenReturn("testuser");
        when(authenticationManager.authenticate(any())).thenReturn(mockAuth);
        when(userService.findByName("testuser")).thenReturn(mockUser);

        // Act & Assert
        mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(authRequest))
                .header("X-Forwarded-For", "192.168.1.1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").exists())
                .andExpect(jsonPath("$.refreshToken").exists())
                .andExpect(jsonPath("$.user.username").value("testuser"));

        verify(refreshTokenService).saveRefreshToken("testuser", any());
    }

    @Test
    void testLoginWithInvalidCredentials() throws Exception {
        // Arrange
        AuthRequest authRequest = new AuthRequest();
        authRequest.setUsername("testuser");
        authRequest.setPassword("wrongpassword");
        when(authenticationManager.authenticate(any()))
                .thenThrow(new BadCredentialsException("Invalid credentials"));

        // Act & Assert
        mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(authRequest))
                .header("X-Forwarded-For", "192.168.1.1"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void testLoginRateLimitExceeded() throws Exception {
        // Arrange
        AuthRequest authRequest = new AuthRequest();
        authRequest.setUsername("testuser");
        authRequest.setPassword("password");

        when(rateLimitingService.isLoginAllowed(any())).thenReturn(false);

        // Act & Assert
        mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(authRequest))
                .header("X-Forwarded-For", "192.168.1.1"))
                .andExpect(status().is(429)); // Too Many Requests
    }

    @Test
    void testSuccessfulTokenRefresh() throws Exception {
        // Arrange
        String refreshToken = jwtUtil.generateRefreshToken("testuser");
        TokenRefreshRequest request = new TokenRefreshRequest(refreshToken);

        when(refreshTokenService.isRefreshTokenValid(refreshToken)).thenReturn(true);

        // Act & Assert
        mockMvc.perform(post("/auth/refresh")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
                .header("X-Forwarded-For", "192.168.1.1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").exists())
                .andExpect(jsonPath("$.refreshToken").exists())
                .andExpect(jsonPath("$.tokenType").value("Bearer"))
                .andExpect(jsonPath("$.expiresIn").value(900));

        verify(refreshTokenService).saveRefreshToken("testuser", any());
    }

    @Test
    void testTokenRefreshWithInvalidToken() throws Exception {
        // Arrange
        TokenRefreshRequest request = new TokenRefreshRequest("invalid.token.here");
        when(refreshTokenService.isRefreshTokenValid("invalid.token.here")).thenReturn(false);

        // Act & Assert
        mockMvc.perform(post("/auth/refresh")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
                .header("X-Forwarded-For", "192.168.1.1"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testTokenRefreshRateLimitExceeded() throws Exception {
        // Arrange
        String refreshToken = jwtUtil.generateRefreshToken("testuser");
        TokenRefreshRequest request = new TokenRefreshRequest(refreshToken);
        when(rateLimitingService.isRefreshAllowed(any())).thenReturn(false);

        // Act & Assert
        mockMvc.perform(post("/auth/refresh")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
                .header("X-Forwarded-For", "192.168.1.1"))
                .andExpect(status().is(429)); // Too Many Requests
    }

    @Test
    void testSuccessfulLogout() throws Exception {
        // Arrange
        String accessToken = jwtUtil.generateAccessToken("testuser");

        // Act & Assert
        mockMvc.perform(post("/auth/logout")
                .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("User logged out successfully!"));

        verify(tokenBlacklistService).blacklistToken(accessToken);
        verify(refreshTokenService).deleteAllUserRefreshTokens("testuser");
    }

    @Test
    void testLogoutWithoutToken() throws Exception {
        // Act & Assert
        mockMvc.perform(post("/auth/logout"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("No token provided"));
    }

    @Test
    void testLogoutWithInvalidToken() throws Exception {
        // Act & Assert
        mockMvc.perform(post("/auth/logout")
                .header("Authorization", "Bearer invalid.token.here"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Invalid token"));
    }

    @Test
    void testClientIpExtraction() throws Exception {
        // Test X-Forwarded-For header
        AuthRequest authRequest = new AuthRequest();
        authRequest.setUsername("testuser");
        authRequest.setPassword("password");

        Authentication mockAuth = mock(Authentication.class);
        when(mockAuth.getName()).thenReturn("testuser");
        when(authenticationManager.authenticate(any())).thenReturn(mockAuth);

        User mockUser = new User();
        mockUser.setUsername("testuser");
        when(userService.findByName("testuser")).thenReturn(mockUser);

        mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(authRequest))
                .header("X-Forwarded-For", "203.0.113.195, 70.41.3.18, 150.172.238.178"))
                .andExpect(status().isOk());

        verify(rateLimitingService).isLoginAllowed("203.0.113.195");
    }
}
