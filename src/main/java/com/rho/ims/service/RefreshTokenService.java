package com.rho.ims.service;

import com.rho.ims.model.RefreshToken;
import com.rho.ims.respository.RefreshTokenRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@Transactional
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;
    private final UserService userService;

    public RefreshTokenService(RefreshTokenRepository refreshTokenRepository, UserService userService) {
        this.refreshTokenRepository = refreshTokenRepository;
        this.userService = userService;
    }

    public void saveRefreshToken(String username, String refreshToken) {
        // Remove existing refresh tokens for this user
        refreshTokenRepository.deleteByUsername(username);

        // Save new refresh token
        RefreshToken tokenEntity = new RefreshToken();
        tokenEntity.setUsername(username);
        tokenEntity.setToken(refreshToken);
        tokenEntity.setCreatedAt(LocalDateTime.now());
        tokenEntity.setExpiresAt(LocalDateTime.now().plusDays(7));

        refreshTokenRepository.save(tokenEntity);
    }

    public boolean isRefreshTokenValid(String refreshToken) {
        Optional<RefreshToken> tokenEntity = refreshTokenRepository.findByToken(refreshToken);

        if (tokenEntity.isEmpty()) {
            return false;
        }

        RefreshToken token = tokenEntity.get();

        // Check if token is expired
        if (token.getExpiresAt().isBefore(LocalDateTime.now())) {
            refreshTokenRepository.delete(token);
            return false;
        }

        return true;
    }

    public String getUsernameFromRefreshToken(String refreshToken) {
        Optional<RefreshToken> tokenEntity = refreshTokenRepository.findByToken(refreshToken);
        return tokenEntity.map(RefreshToken::getUsername).orElse(null);
    }

    public void deleteRefreshToken(String refreshToken) {
        refreshTokenRepository.deleteByToken(refreshToken);
    }

    public void deleteAllUserRefreshTokens(String username) {
        refreshTokenRepository.deleteByUsername(username);
    }
}
