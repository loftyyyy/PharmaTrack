package com.rho.ims.security;

import com.rho.ims.service.TokenBlacklistService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.io.IOException;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JwtAuthenticationFilterTest {

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private UserDetailsService userDetailsService;

    @Mock
    private TokenBlacklistService tokenBlacklistService;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain filterChain;

    @InjectMocks
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    private UserDetails userDetails;

    @BeforeEach
    void setUp() {
        userDetails = User.builder()
                .username("testuser")
                .password("password")
                .authorities("ROLE_USER")
                .build();
    }

    @Test
    void testDoFilterInternalWithValidToken() throws ServletException, IOException {
        // Arrange
        String token = "valid.jwt.token";
        when(request.getHeader("Authorization")).thenReturn("Bearer " + token);
        when(tokenBlacklistService.isTokenBlacklisted(token)).thenReturn(false);
        when(jwtUtil.isAccessToken(token)).thenReturn(true);
        when(jwtUtil.extractUsername(token)).thenReturn("testuser");
        when(userDetailsService.loadUserByUsername("testuser")).thenReturn(userDetails);
        when(jwtUtil.isTokenValid(token, userDetails)).thenReturn(true);

        // Act
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // Assert
        verify(tokenBlacklistService).isTokenBlacklisted(token);
        verify(jwtUtil).isAccessToken(token);
        verify(jwtUtil).extractUsername(token);
        verify(userDetailsService).loadUserByUsername("testuser");
        verify(jwtUtil).isTokenValid(token, userDetails);
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void testDoFilterInternalWithBlacklistedToken() throws ServletException, IOException {
        // Arrange
        String token = "blacklisted.jwt.token";
        when(request.getHeader("Authorization")).thenReturn("Bearer " + token);
        when(tokenBlacklistService.isTokenBlacklisted(token)).thenReturn(true);

        // Act
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // Assert
        verify(tokenBlacklistService).isTokenBlacklisted(token);
        verify(filterChain).doFilter(request, response);
        // Should not proceed with token validation
        verify(jwtUtil, never()).isAccessToken(token);
    }

    @Test
    void testDoFilterInternalWithRefreshToken() throws ServletException, IOException {
        // Arrange
        String token = "refresh.jwt.token";
        when(request.getHeader("Authorization")).thenReturn("Bearer " + token);
        when(tokenBlacklistService.isTokenBlacklisted(token)).thenReturn(false);
        when(jwtUtil.isAccessToken(token)).thenReturn(false);

        // Act
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // Assert
        verify(tokenBlacklistService).isTokenBlacklisted(token);
        verify(jwtUtil).isAccessToken(token);
        verify(filterChain).doFilter(request, response);
        // Should not proceed with authentication for refresh tokens
        verify(jwtUtil, never()).extractUsername(token);
    }

    @Test
    void testDoFilterInternalWithNoAuthorizationHeader() throws ServletException, IOException {
        // Arrange
        when(request.getHeader("Authorization")).thenReturn(null);

        // Act
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // Assert
        verify(filterChain).doFilter(request, response);
        verify(tokenBlacklistService, never()).isTokenBlacklisted(any());
    }

    @Test
    void testDoFilterInternalWithInvalidAuthorizationHeader() throws ServletException, IOException {
        // Arrange
        when(request.getHeader("Authorization")).thenReturn("InvalidHeader token");

        // Act
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // Assert
        verify(filterChain).doFilter(request, response);
        verify(tokenBlacklistService, never()).isTokenBlacklisted(any());
    }

    @Test
    void testDoFilterInternalWithInvalidToken() throws ServletException, IOException {
        // Arrange
        String token = "invalid.jwt.token";
        when(request.getHeader("Authorization")).thenReturn("Bearer " + token);
        when(tokenBlacklistService.isTokenBlacklisted(token)).thenReturn(false);
        when(jwtUtil.isAccessToken(token)).thenReturn(true);
        when(jwtUtil.extractUsername(token)).thenThrow(new RuntimeException("Invalid token"));

        // Act
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // Assert
        verify(tokenBlacklistService).isTokenBlacklisted(token);
        verify(jwtUtil).isAccessToken(token);
        verify(jwtUtil).extractUsername(token);
        verify(filterChain).doFilter(request, response);
        // Should not proceed with authentication due to invalid token
        verify(userDetailsService, never()).loadUserByUsername(any());
    }

    @Test
    void testDoFilterInternalWithInvalidUserDetails() throws ServletException, IOException {
        // Arrange
        String token = "valid.jwt.token";
        when(request.getHeader("Authorization")).thenReturn("Bearer " + token);
        when(tokenBlacklistService.isTokenBlacklisted(token)).thenReturn(false);
        when(jwtUtil.isAccessToken(token)).thenReturn(true);
        when(jwtUtil.extractUsername(token)).thenReturn("testuser");
        when(userDetailsService.loadUserByUsername("testuser")).thenReturn(userDetails);
        when(jwtUtil.isTokenValid(token, userDetails)).thenReturn(false);

        // Act
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // Assert
        verify(tokenBlacklistService).isTokenBlacklisted(token);
        verify(jwtUtil).isAccessToken(token);
        verify(jwtUtil).extractUsername(token);
        verify(userDetailsService).loadUserByUsername("testuser");
        verify(jwtUtil).isTokenValid(token, userDetails);
        verify(filterChain).doFilter(request, response);
        // Should not set authentication due to invalid token
    }

    @Test
    void testDoFilterInternalWithExistingAuthentication() throws ServletException, IOException {
        // Arrange
        String token = "valid.jwt.token";
        when(request.getHeader("Authorization")).thenReturn("Bearer " + token);
        when(tokenBlacklistService.isTokenBlacklisted(token)).thenReturn(false);
        when(jwtUtil.isAccessToken(token)).thenReturn(true);
        when(jwtUtil.extractUsername(token)).thenReturn("testuser");

        // Set existing authentication
        Authentication existingAuth = mock(Authentication.class);
        SecurityContextHolder.getContext().setAuthentication(existingAuth);

        // Act
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // Assert
        verify(tokenBlacklistService).isTokenBlacklisted(token);
        verify(jwtUtil).isAccessToken(token);
        verify(jwtUtil).extractUsername(token);
        verify(filterChain).doFilter(request, response);
        // Should not load user details due to existing authentication
        verify(userDetailsService, never()).loadUserByUsername(any());
    }
}
