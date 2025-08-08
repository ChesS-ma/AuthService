package com.chesS.user_auth_service.services;

import com.chesS.user_auth_service.entities.RefreshToken;
import com.chesS.user_auth_service.entities.User;

import java.util.Optional;

public interface RefreshTokenService {

    public RefreshToken createRefreshToken(User user) ;

    public Optional<RefreshToken> findByToken(String token) ;

    public RefreshToken verifyExpiration(RefreshToken token) ;

    public void revokeByUser(User user) ;

    public void revokeByToken(String token) ;

    public void cleanExpiredTokens() ;
}
