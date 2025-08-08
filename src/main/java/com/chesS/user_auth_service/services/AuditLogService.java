package com.chesS.user_auth_service.services;

import com.chesS.user_auth_service.entities.AuditLog;
import com.chesS.user_auth_service.entities.User;

import java.util.List;

public interface AuditLogService {

    public void logAction(User user, String action , String ipAdress) ;

    public List<AuditLog> getUserAuditLogs(User user ) ;

    public List<AuditLog> getAuditLogsByAction(String action) ;

}
