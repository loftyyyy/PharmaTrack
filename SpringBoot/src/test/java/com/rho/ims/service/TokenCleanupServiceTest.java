package com.rho.ims.service;

import com.rho.ims.model.RefreshToken;
import com.rho.ims.respository.RefreshTokenRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TokenCleanupServiceTest {

    @Mock
    private RefreshTokenRepository refreshTokenRepository;

    @InjectMocks
    private TokenCleanupService tokenCleanupService;

    @Test
    void testCleanupExpiredTokens() {
        // Arrange
        LocalDateTime now = LocalDateTime.now();
        when(refreshTokenRepository.deleteExpiredTokens(any(LocalDateTime.class))).thenReturn(3);

        // Act
        tokenCleanupService.cleanupExpiredTokens();

        // Assert
        verify(refreshTokenRepository).deleteExpiredTokens(any(LocalDateTime.class));
    }

    @Test
    void testCleanupExpiredTokensWithException() {
        // Arrange
        when(refreshTokenRepository.deleteExpiredTokens(any(LocalDateTime.class)))
                .thenThrow(new RuntimeException("Database error"));

        // Act & Assert - Should not throw exception
        tokenCleanupService.cleanupExpiredTokens();

        verify(refreshTokenRepository).deleteExpiredTokens(any(LocalDateTime.class));
    }

    @Test
    void testManualCleanup() {
        // Arrange
        LocalDateTime now = LocalDateTime.now();
        when(refreshTokenRepository.deleteExpiredTokens(any(LocalDateTime.class))).thenReturn(5);

        // Act
        int deletedCount = tokenCleanupService.manualCleanup();

        // Assert
        assertEquals(5, deletedCount);
        verify(refreshTokenRepository).deleteExpiredTokens(any(LocalDateTime.class));
    }

    @Test
    void testManualCleanupWithNoExpiredTokens() {
        // Arrange
        when(refreshTokenRepository.deleteExpiredTokens(any(LocalDateTime.class))).thenReturn(0);

        // Act
        int deletedCount = tokenCleanupService.manualCleanup();

        // Assert
        assertEquals(0, deletedCount);
        verify(refreshTokenRepository).deleteExpiredTokens(any(LocalDateTime.class));
    }
}
