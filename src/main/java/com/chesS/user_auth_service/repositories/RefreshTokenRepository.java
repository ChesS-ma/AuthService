package com.chesS.user_auth_service.repositories;

import com.chesS.user_auth_service.entities.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;
import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken , UUID > {

    Optional<RefreshToken> findByToken(String token);

    Optional<RefreshToken> findByUserId(UUID userId);

    void deleteByUserId(UUID userId);
}
