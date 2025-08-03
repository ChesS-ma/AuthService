package com.chesS.user_auth_service.dto.request;


import lombok.Data;

@Data
public class RegisterRequest {
    public String email ;
    private String password ;
}
