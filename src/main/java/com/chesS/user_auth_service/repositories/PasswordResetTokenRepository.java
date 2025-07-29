package com.chesS.user_auth_service.repositories;

import com.chesS.user_auth_service.entities.PasswordResetToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, UUID> {

    Optional<PasswordResetToken> findByToken(String token);

    Optional<PasswordResetToken> findByUserId(UUID userId);

    void deleteByUserId(UUID userId);
}
