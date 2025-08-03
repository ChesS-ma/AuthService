package com.chesS.user_auth_service.controllers;

import com.chesS.user_auth_service.dto.request.LoginRequest;
import com.chesS.user_auth_service.dto.request.RegisterRequest;
import com.chesS.user_auth_service.dto.response.AuthResponse;
import com.chesS.user_auth_service.entities.User;
import com.chesS.user_auth_service.services.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<User> register(@RequestBody RegisterRequest request){
        return ResponseEntity.ok(authService.Register(request));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest request){
        return ResponseEntity.ok(authService.Login(request));
    }


}
