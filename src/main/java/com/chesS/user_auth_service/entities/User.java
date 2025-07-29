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
    @Id
    @GeneratedValue
    @UuidGenerator
    @Column(updatable = false ,columnDefinition = "uuid")
    private UUID id ;

    private String email;

    private String password_hash;

    private boolean is_verified ;

    private String provider ;

    private String status ;

    @ManyToOne(optional = false) // or Lazy
    @JoinColumn(name = "role_id")
    private Role role ;

}