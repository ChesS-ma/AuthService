package com.chesS.user_auth_service.repositories;

import com.chesS.user_auth_service.entities.AuditLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List ;
import java.util.UUID;

@Repository
public interface AuditLogRepository extends JpaRepository<AuditLog , UUID> {

    List<AuditLog> findByUserId(UUID userId);

    List<AuditLog> findTop10ByUserIdOrderByCreatedAtDesc(UUID userId);

    List<AuditLog> findByActionOrderByCreatedAtDesc(String action);
}
