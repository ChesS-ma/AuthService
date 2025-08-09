package com.chesS.user_auth_service.controllers;

import com.chesS.user_auth_service.services.JWTService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HealthTestController {

    @Autowired
    private JWTService Jwtservice;

    @GetMapping("/health")
    public String healthCheck() {
        return "Auth Service is running!";
    }

}