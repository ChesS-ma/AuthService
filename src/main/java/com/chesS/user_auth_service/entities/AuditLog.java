package com.chesS.user_auth_service.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.UuidGenerator;

import java.time.Instant;
import java.util.UUID;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "audit_log")
public class AuditLog {
    @Id
    @GeneratedValue
    @UuidGenerator
    private UUID id;

    @ManyToOne
    private User user;

    private String action;  // e.g., "LOGIN_FAILED", "PASSWORD_CHANGE"

    private String ipAddress;

    private Instant createdAt;
}