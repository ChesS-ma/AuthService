package com.chesS.user_auth_service.services;

import com.chesS.user_auth_service.entities.RefreshToken;
import com.chesS.user_auth_service.entities.User;
import com.chesS.user_auth_service.repositories.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RefreshTokenServiceImpl implements RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;

    @Value("${jwt.refresh-token.expiration}")
    private long refreshTokenExpiration;

    @Transactional
    public RefreshToken createRefreshToken(User user) {
        // Revoke any existing refresh tokens for this user
        revokeByUser(user);

        RefreshToken refreshToken = RefreshToken.builder()
                .user(user)
                .token(UUID.randomUUID().toString())
                .expiryDate(Instant.now().plusMillis(refreshTokenExpiration))
                .build();

        return refreshTokenRepository.save(refreshToken);
    }

    public Optional<RefreshToken> findByToken(String token) {
        return refreshTokenRepository.findByToken(token);
    }

    public RefreshToken verifyExpiration(RefreshToken token) {
        if (token.getExpiryDate().isBefore(Instant.now())) {
            refreshTokenRepository.delete(token);
            throw new RuntimeException("Refresh token was expired. Please make a new login request");
        }
        return token;
    }

    @Transactional
    public void revokeByUser(User user) {
        refreshTokenRepository.findByUser(user)
                .ifPresent(refreshTokenRepository::delete);
    }

    @Transactional
    public void revokeByToken(String token) {
        refreshTokenRepository.findByToken(token)
                .ifPresent(refreshTokenRepository::delete);
    }

    @Transactional
    public void cleanExpiredTokens() {
        refreshTokenRepository.deleteAllByExpiryDateBefore(Instant.now());
    }
}