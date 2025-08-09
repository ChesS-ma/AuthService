package com.chesS.user_auth_service.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.UuidGenerator;

import java.util.UUID;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "users")
public class User  {

    public enum Status {
        ACTIVE,
        INACTIVE,
        SUSPENDED,
        BANNED,
        DELETED
    }

    public enum Provider {
        LOCAL,
        GOOGLE,
        GITHUB,
        FACEBOOK,
    }


    @Id
    @GeneratedValue
    @UuidGenerator
    @Column(updatable = false )
    private UUID id ;

    @Column(unique = true)
    private String email;

    private String password;

    private String username ;

    private boolean isVerified ;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Provider provider;

    @ManyToOne(fetch = FetchType.EAGER , optional = false ) // or Eager
    @JoinColumn(name = "role_id")
    private Role role ;

}

