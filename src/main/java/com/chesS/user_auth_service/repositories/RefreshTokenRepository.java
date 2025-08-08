package com.chesS.user_auth_service.repositories;

import com.chesS.user_auth_service.entities.RefreshToken;
import com.chesS.user_auth_service.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.UUID;
import java.util.Optional;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken , UUID > {

    Optional<RefreshToken> findByToken(String token);

    Optional<RefreshToken> findByUserId(UUID userId);

    void deleteByUserId(UUID userId);

    Optional<RefreshToken> findByUser(User user);

    void deleteAllByExpiryDateBefore(Instant now);
}
