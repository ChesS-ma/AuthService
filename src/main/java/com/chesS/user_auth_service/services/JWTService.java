package com.chesS.user_auth_service.services;

import com.chesS.user_auth_service.entities.User;
import io.jsonwebtoken.Claims;

public interface JWTService {

     String generateToken(User user ) ;

//     String generateRefreshToken(User user ) ;

     boolean isTokenValid(String token, User user);

     public String extractEmail(String token) ;

     public String extractUsername(String token) ;

     public Long extractUserId(String token) ;
}
