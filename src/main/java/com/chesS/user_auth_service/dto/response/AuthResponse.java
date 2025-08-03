package com.chesS.user_auth_service.dto.response;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class AuthResponse {

    private String accessToken ;

    private String refreshToken ;

    private String tokenType;

    private String email ;

    private String role ;
}
