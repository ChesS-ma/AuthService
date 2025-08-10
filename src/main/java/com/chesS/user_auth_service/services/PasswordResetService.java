package com.chesS.user_auth_service.services;

import com.chesS.user_auth_service.entities.User;
import jakarta.servlet.http.HttpServletRequest;

public interface PasswordResetService {

    public String createPasswordResetToken(String email , HttpServletRequest request ) ;


    public boolean validatePasswordResetToken(String token ) ;

    public void resetPassword(String token , String newPassword , HttpServletRequest request ) ;
}
