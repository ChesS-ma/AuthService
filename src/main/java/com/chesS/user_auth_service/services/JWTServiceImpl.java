package com.chesS.user_auth_service.services;

import com.chesS.user_auth_service.entities.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.CredentialsExpiredException;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.function.Function;

@Service
public class JWTServiceImpl implements JWTService{

    @Value("${jwt.secret}")
    private String secret ;

    @Value("${jwt.access-token.expiration}")
    private long accessTokenExpiration;

    @Value("${jwt.refresh-token.expiration}")
    private long refreshTokenExpiration;

    private Key key;

    @PostConstruct
    public void init() {
        byte[] keyBytes = Decoders.BASE64.decode(secret);
        this.key = Keys.hmacShaKeyFor(keyBytes);
    }


    @Override
    public String generateToken(User user) {
        return Jwts.builder()
                .subject(user.getEmail())
                .claim("userId", user.getId())
                .claim("username", user.getUsername())
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + accessTokenExpiration))
                .signWith(key)
                .compact();
    }

//    @Override
//    public String generateRefreshToken(User user) {
//        return Jwts.builder()
//                .subject(user.getEmail())
//                .claim("userId", user.getId())
//                .claim("username", user.getUsername())
//                .issuedAt(new Date(System.currentTimeMillis()))
//                .expiration(new Date(System.currentTimeMillis() + refreshTokenExpiration))
//                .signWith(key)
//                .compact();
//    }


    @Override
    public boolean isTokenValid(String token, User user) {
        final String email = extractEmail(token);
        return (email.equals(user.getEmail())) && !isTokenExpired(token);
    }

    @Override
    public String extractEmail(String token) {
        try{
            return extractClaim(token, Claims::getSubject);
        } catch (Exception e) {
        throw new IllegalArgumentException("Invalid token or missing Email Subject", e);
        }
    }

    @Override
    public String extractUsername(String token) {
        try{
            return extractClaim(token , claims -> claims.get("username" , String.class)) ;
        } catch(Exception e){
            throw new IllegalArgumentException("Invalid token or missing username claim" , e) ;
        }
    }

    @Override
    public Long extractUserId(String token) {
        try {
            return extractClaim(token, claims -> claims.get("userId", Long.class));
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid token or missing userId claim", e);
        }
    }



    public Claims extractAllClaims(String token) {
        try{
            return Jwts.parser()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (ExpiredJwtException ex) {
            throw new CredentialsExpiredException("Token expired", ex);
        } catch (JwtException ex) {
            throw new BadCredentialsException("Invalid token", ex);
        }
    }

    private <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private boolean isTokenExpired(String token) {
        return extractClaim(token, Claims::getExpiration).before(new Date());
    }
}
