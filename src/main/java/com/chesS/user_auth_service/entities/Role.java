package com.chesS.user_auth_service.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.UuidGenerator;

import java.util.UUID ;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "roles")
public class Role {
    @Id
    @GeneratedValue
    @UuidGenerator
    @Column(updatable = false, columnDefinition = "uuid")
    private UUID id ;

    @Column(unique = true)
    private String name ;

    private String label ;


}