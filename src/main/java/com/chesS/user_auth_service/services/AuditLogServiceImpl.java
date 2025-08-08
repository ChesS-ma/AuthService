package com.chesS.user_auth_service.services;

import com.chesS.user_auth_service.entities.AuditLog;
import com.chesS.user_auth_service.entities.User;
import com.chesS.user_auth_service.repositories.AuditLogRepository;
import com.chesS.user_auth_service.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuditLogServiceImpl implements AuditLogService {

    private final UserRepository userRepository ;
    private final AuditLogRepository auditLogRepository ;

    @Override
    public void logAction(User user, String action , String ipAdress){
        try {
            AuditLog auditLog = AuditLog.builder()
                    .user(user)
                    .action(action)
                    .ipAddress(ipAdress)
                    .createdAt(Instant.now())
                    .build();

            auditLogRepository.save(auditLog);
            log.info("Audit Log created: {} with the user: {} " , action , user != null? user.getUsername() : "Unknown User") ;
        }catch (Exception e){
            log.error("Failed to create audit log: {} for user: {}", action, user != null ? user.getEmail() : "unknown", e);
        }
    }


    @Override
    public List<AuditLog> getUserAuditLogs(User user) {
        return auditLogRepository.findTop10ByUserIdOrderByCreatedAtDesc(user.getId()) ;
    }

    @Override
    public List<AuditLog> getAuditLogsByAction(String action) {
        return auditLogRepository.findByActionOrderByCreatedAtDesc(action) ;
    }


}
