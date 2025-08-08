package com.chesS.user_auth_service.services;

import com.chesS.user_auth_service.dto.request.LoginRequest;
import com.chesS.user_auth_service.dto.request.RegisterRequest;
import com.chesS.user_auth_service.dto.response.AuthResponse;
import com.chesS.user_auth_service.entities.RefreshToken;
import com.chesS.user_auth_service.entities.Role;
import com.chesS.user_auth_service.entities.User;
import com.chesS.user_auth_service.repositories.RoleRepository;
import com.chesS.user_auth_service.repositories.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final PasswordEncoder passwordEncoder;
    private final  UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final JWTService jwtService;
    private final AuditLogService auditLogService;
    private final RefreshTokenService refreshTokenService;

    @Override
    @Transactional
    public User Register(RegisterRequest registerRequest, HttpServletRequest request) {
        String ipAddress = request.getRemoteAddr();

        if (userRepository.existsByEmail(registerRequest.getEmail())) {
            auditLogService.logAction(null, "REGISTRATION_FAILED_EMAIL_EXISTS", ipAddress);
            throw new RuntimeException("Email is already in use!");
        }

        Role defaultRole = roleRepository.findByName("PLAYER")
                .orElseThrow(() -> new RuntimeException("Default role not found"));

        User user = User.builder()
                .email(registerRequest.getEmail())
                .username(registerRequest.getUsername())
                .password(passwordEncoder.encode(registerRequest.getPassword()))
                .isVerified(false)
                .status(User.Status.ACTIVE)
                .provider(User.Provider.LOCAL)
                .role(defaultRole)
                .build();

        User savedUser = userRepository.save(user);
        auditLogService.logAction(savedUser, "USER_REGISTERED", ipAddress);

        return savedUser;
    }


    @Override
    @PreAuthorize("permitAll")
    @Transactional
    public AuthResponse Login(LoginRequest loginRequest, HttpServletRequest request) {
        String ipAddress = request.getRemoteAddr();

        try {
            User user = userRepository.findByEmail(loginRequest.getEmail())
                    .orElseThrow(() -> {
                        auditLogService.logAction(null, "LOGIN_FAILED_USER_NOT_FOUND", ipAddress);
                        return new RuntimeException("Invalid credentials");
                    });

            if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
                auditLogService.logAction(user, "LOGIN_FAILED_INVALID_PASSWORD", ipAddress);
                throw new RuntimeException("Invalid credentials");
            }

            String accessToken = jwtService.generateToken(user);
            RefreshToken refreshToken = refreshTokenService.createRefreshToken(user);

            auditLogService.logAction(user, "LOGIN_SUCCESS", ipAddress);

            return AuthResponse.builder()
                    .accessToken(accessToken)
                    .refreshToken(refreshToken.getToken())
                    .username(user.getUsername())
                    .email(user.getEmail())
                    .role(user.getRole().getName())
                    .build();
        } catch (AuthenticationException ex) {
            auditLogService.logAction(null, "LOGIN_FAILED_AUTH_ERROR", ipAddress);
            throw new RuntimeException("Authentication failed", ex);
        }
    }

    @Override
    @Transactional
    public AuthResponse refreshToken(String refreshToken, HttpServletRequest request) {
        String ipAddress = request.getRemoteAddr();

        return refreshTokenService.findByToken(refreshToken)
                .map(refreshTokenService::verifyExpiration)
                .map(RefreshToken::getUser)
                .map(user -> {
                    String newAccessToken = jwtService.generateToken(user);
                    auditLogService.logAction(user, "TOKEN_REFRESHED", ipAddress);

                    return AuthResponse.builder()
                            .accessToken(newAccessToken)
                            .refreshToken(refreshToken) // Same refresh token until it expires
                            .username(user.getUsername())
                            .email(user.getEmail())
                            .role(user.getRole().getName())
                            .build();
                })
                .orElseThrow(() -> {
                    auditLogService.logAction(null, "TOKEN_REFRESH_FAILED", ipAddress);
                    return new RuntimeException("Refresh token is invalid");
                });
    }

    @Override
    @Transactional
    public void logout(String refreshToken, HttpServletRequest request) {
        String ipAddress = request.getRemoteAddr();

        refreshTokenService.findByToken(refreshToken)
                .ifPresent(token -> {
                    auditLogService.logAction(token.getUser(), "USER_LOGOUT", ipAddress);
                    refreshTokenService.revokeByToken(refreshToken);
                });
    }
}

