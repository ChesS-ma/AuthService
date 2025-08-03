package com.chesS.user_auth_service.controllers;

import com.chesS.user_auth_service.entities.User;
import com.chesS.user_auth_service.services.JWTService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HealthTestController {

    @Autowired
    private JWTService Jwtservice;

    @GetMapping("/health")
    public String healthCheck() {
        return "Auth Service is running!";
    }


    @PostMapping("/test_login")
    public String login(@RequestBody User user) {
        return Jwtservice.generateToken(user);
    }

}