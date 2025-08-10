package com.chesS.user_auth_service.controllers;

import com.chesS.user_auth_service.dto.request.ResetPasswordRequest;
import com.chesS.user_auth_service.dto.response.AuthResponse;
import com.chesS.user_auth_service.dto.response.ErrorResponse;
import com.chesS.user_auth_service.services.PasswordResetService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;

@RestController
@RequestMapping("/api/auth/password-reset")
@RequiredArgsConstructor
@Slf4j
public class PasswordResetController {

    private final PasswordResetService passwordResetService;

    // Simple request DTOs as inner classes
    public record PasswordResetTokenRequest(String email) {}
    public record TokenValidationRequest(String token) {}

    // Simple response DTOs
    public record PasswordResetResponse(String message, String token) {}
    public record ValidationResponse(String message, boolean valid) {}
    public record ResetSuccessResponse(String message, boolean success) {}

    /**
     * Request password reset token
     * POST /api/auth/password-reset/request
     */
    @PostMapping("/request")
    public ResponseEntity<?> requestPasswordReset(
            @RequestBody PasswordResetTokenRequest request,
            HttpServletRequest httpRequest) {

        try {
            log.info("Password reset requested for email: {}", request.email());

            String token = passwordResetService.createPasswordResetToken(request.email(), httpRequest);

            return ResponseEntity.ok(
                    new PasswordResetResponse("Password reset token generated successfully", token)
            );

        } catch (RuntimeException e) {
            log.error("Error creating password reset token for email: {}", request.email(), e);
            return ResponseEntity.badRequest().body(
                    ErrorResponse.builder()
                            .message("Failed to create password reset token")
                            .error(e.getMessage())
                            .timestamp(Instant.now())
                            .build()
            );
        } catch (Exception e) {
            log.error("Unexpected error during password reset request", e);
            return ResponseEntity.internalServerError().body(
                    ErrorResponse.builder()
                            .message("An unexpected error occurred")
                            .error("Internal server error")
                            .timestamp(Instant.now())
                            .build()
            );
        }
    }

    /**
     * Validate password reset token
     * POST /api/auth/password-reset/validate
     */
    @PostMapping("/validate")
    public ResponseEntity<?> validatePasswordResetToken(
            @RequestBody TokenValidationRequest request) {

        try {
            log.info("Validating password reset token");

            boolean isValid = passwordResetService.validatePasswordResetToken(request.token());

            if (isValid) {
                return ResponseEntity.ok(
                        new ValidationResponse("Token is valid and ready for password reset", true)
                );
            } else {
                return ResponseEntity.badRequest().body(
                        ErrorResponse.builder()
                                .message("Invalid or expired token")
                                .error("Token validation failed")
                                .timestamp(Instant.now())
                                .build()
                );
            }

        } catch (Exception e) {
            log.error("Error validating password reset token", e);
            return ResponseEntity.internalServerError().body(
                    ErrorResponse.builder()
                            .message("An unexpected error occurred")
                            .error("Internal server error")
                            .timestamp(Instant.now())
                            .build()
            );
        }
    }

    /**
     * Reset password using valid token
     * POST /api/auth/password-reset/reset
     */
    @PostMapping("/reset")
    public ResponseEntity<?> resetPassword(
            @RequestBody ResetPasswordRequest request,
            HttpServletRequest httpRequest) {

        try {
            log.info("Password reset attempted with token");

            passwordResetService.resetPassword(request.getToken(), request.getNewPassword(), httpRequest);

            return ResponseEntity.ok(
                    new ResetSuccessResponse("Password has been reset successfully", true)
            );

        } catch (RuntimeException e) {
            log.error("Error resetting password with token", e);
            return ResponseEntity.badRequest().body(
                    ErrorResponse.builder()
                            .message("Failed to reset password")
                            .error(e.getMessage())
                            .timestamp(Instant.now())
                            .build()
            );
        } catch (Exception e) {
            log.error("Unexpected error during password reset", e);
            return ResponseEntity.internalServerError().body(
                    ErrorResponse.builder()
                            .message("An unexpected error occurred")
                            .error("Internal server error")
                            .timestamp(Instant.now())
                            .build()
            );
        }
    }
}