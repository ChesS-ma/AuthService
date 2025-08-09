package com.chesS.user_auth_service.controllers;

import com.chesS.user_auth_service.entities.AuditLog;
import com.chesS.user_auth_service.entities.User;
import com.chesS.user_auth_service.repositories.UserRepository;
import com.chesS.user_auth_service.services.AuditLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/audit")
@RequiredArgsConstructor
public class AuditLogController {

    private final AuditLogService auditLogService;
    private final UserRepository userRepository;

    @GetMapping("/user/{email}")
    @PreAuthorize("hasRole('PLAYER') or #email == authentication.principal.username")
    public ResponseEntity<?> getUserAuditLogs(@PathVariable String email) {
        try {
            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            List<AuditLog> auditLogs = auditLogService.getUserAuditLogs(user);

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "data", auditLogs,
                    "total", auditLogs.size()
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "error", e.getMessage()
            ));
        }
    }

    @GetMapping("/action/{action}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getAuditLogsByAction(@PathVariable String action) {
        try {
            List<AuditLog> auditLogs = auditLogService.getAuditLogsByAction(action);
            return ResponseEntity.ok(auditLogs);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/actions")
    @PreAuthorize("hasAnyRole('ADMIN', 'MODERATOR')")
    public ResponseEntity<?> getAvailableActions() {

//        System.out.println("=== Controller Debug ===");
//        System.out.println("Endpoint hit successfully");
//        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
//        System.out.println("Current user: " + auth.getName());
//        System.out.println("Authorities: " + auth.getAuthorities());
//        System.out.println("=== End Controller Debug ===");


        List<String> actions = List.of(
                "USER_REGISTERED",
                "LOGIN_SUCCESS",
                "LOGIN_FAILED_USER_NOT_FOUND",
                "LOGIN_FAILED_INVALID_PASSWORD",
                "LOGIN_FAILED_AUTH_ERROR",
                "TOKEN_REFRESHED",
                "TOKEN_REFRESH_FAILED",
                "USER_LOGOUT",
                "LOGOUT_FAILED",
                "PASSWORD_RESET_TOKEN_CREATED",
                "PASSWORD_RESET_SUCCESS",
                "PASSWORD_RESET_FAILED_EXPIRED_TOKEN",
                "REGISTRATION_FAILED_EMAIL_EXISTS",
                "REGISTRATION_FAILED"
        );
        return ResponseEntity.ok(actions);
    }
}