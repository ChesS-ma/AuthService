package com.chesS.user_auth_service.dto.request;

import lombok.Data;

@Data
public class ResetPasswordRequest {

    private String token ;
    private String newPassword ;
}
