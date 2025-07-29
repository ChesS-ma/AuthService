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
@Table(name = "password_reset_token")
public class PasswordResetToken {
    @Id
    @GeneratedValue
    @UuidGenerator
    private UUID id;

    @OneToOne
    private User user;

    @Column(unique = true)
    private String token;

    private Instant expiryDate;
}
