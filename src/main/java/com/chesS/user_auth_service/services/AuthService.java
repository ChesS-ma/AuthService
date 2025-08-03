package com.chesS.user_auth_service.services;

import com.chesS.user_auth_service.dto.request.LoginRequest;
import com.chesS.user_auth_service.dto.request.RegisterRequest;
import com.chesS.user_auth_service.entities.User;

public interface AuthService {

    public User Register(RegisterRequest registerRequest) ;

    public User Login(LoginRequest loginRequest) ;
}
