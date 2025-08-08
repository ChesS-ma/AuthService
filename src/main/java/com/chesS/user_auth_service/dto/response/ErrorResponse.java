package com.chesS.user_auth_service.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;

@Builder
@Data
@AllArgsConstructor
public class ErrorResponse {

    private String error;
    private String message;
    private Instant timestamp;
}
