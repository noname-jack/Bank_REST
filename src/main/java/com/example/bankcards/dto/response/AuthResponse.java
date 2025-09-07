package com.example.bankcards.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Ответ с токеном доступа после аутентификации")
public record AuthResponse(
        @Schema(description = "Токен доступа JWT", example = "eyJhbGciO...")
        String accessToken
) {
}