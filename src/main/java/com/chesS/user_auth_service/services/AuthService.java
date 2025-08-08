package com.chesS.user_auth_service.services;

import com.chesS.user_auth_service.dto.request.LoginRequest;
import com.chesS.user_auth_service.dto.request.RegisterRequest;
import com.chesS.user_auth_service.dto.response.AuthResponse;
import com.chesS.user_auth_service.entities.User;
import jakarta.servlet.http.HttpServletRequest;

public interface AuthService {

    public User Register(RegisterRequest registerRequest , HttpServletRequest request) ;

    public AuthResponse Login(LoginRequest loginRequest , HttpServletRequest request) ;

    public AuthResponse refreshToken(String refreshToken, HttpServletRequest request) ;

    public void logout(String refreshToken, HttpServletRequest request) ;
}
