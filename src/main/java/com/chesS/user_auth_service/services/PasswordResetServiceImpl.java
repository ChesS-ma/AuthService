package com.chesS.user_auth_service.services;


import com.chesS.user_auth_service.entities.PasswordResetToken;
import com.chesS.user_auth_service.entities.User;
import com.chesS.user_auth_service.repositories.AuditLogRepository;
import com.chesS.user_auth_service.repositories.PasswordResetTokenRepository;
import com.chesS.user_auth_service.repositories.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PasswordResetServiceImpl implements PasswordResetService{

    private final UserRepository userRepository ;
    private final PasswordResetTokenRepository passwordResetTokenRepository ;
    private final AuditLogService auditLogService ;
    private final PasswordEncoder passwordEncoder ;

    @Value("${password-reset.token.expiration}")
    private long passwordResetTokenExpiration ;

    @Override
    @Transactional
    public String createPasswordResetToken(String email , HttpServletRequest request ){
        String ipAddress = request.getRemoteAddr();

        User user = userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));

        passwordResetTokenRepository.findByUserId(user.getId()).ifPresent(passwordResetTokenRepository::delete) ;

        String token = UUID.randomUUID().toString() ;

        PasswordResetToken passwordResetToken = PasswordResetToken.builder()
                .token(token)
                .user(user)
                .expiryDate(Instant.now().plusMillis(passwordResetTokenExpiration))
                .build() ;

        passwordResetTokenRepository.save(passwordResetToken) ;

        auditLogService.logAction(user, "PASSWORD_RESET_TOKEN_CREATED", ipAddress);

        return token  ;
    }

    public User getUserByPasswordResetToken(String token , String  ipAddress) {
        PasswordResetToken resetToken = passwordResetTokenRepository.findByToken(token)
                .orElseThrow(() -> new RuntimeException("Invalid password reset token"));

        if (resetToken.getExpiryDate().isBefore(Instant.now())) {
            passwordResetTokenRepository.delete(resetToken);
            auditLogService.logAction(resetToken.getUser(), "PASSWORD_RESET_FAILED_EXPIRED_TOKEN", ipAddress);
            throw new RuntimeException("Password reset token has expired");
        }

        return resetToken.getUser();
    }

    @Override
    @Transactional
    public void resetPassword(String token ,String newPassword ,  HttpServletRequest request ){
        String ipAddress = request.getRemoteAddr() ;

        PasswordResetToken resetToken = passwordResetTokenRepository.findByToken(token)
                .orElseThrow(() -> new RuntimeException("Invalid password reset token"));

        User user = getUserByPasswordResetToken(token , ipAddress) ;
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user) ;
        passwordResetTokenRepository.delete(resetToken);

        auditLogService.logAction(user, "PASSWORD_RESET_SECCESS", ipAddress);

    }

    @Override
    @Transactional
    public boolean validatePasswordResetToken(String token ){
        return passwordResetTokenRepository.findByToken(token)
                .map(resetToken -> resetToken.getExpiryDate().isAfter(Instant.now()))
                .orElse(false);
    }
}
