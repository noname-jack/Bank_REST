package com.example.bankcards.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Данные для аутентификации пользователя")
public record AuthRequest(
        @Schema(description = "Имя пользователя", example = "admin")
        String username,

        @Schema(description = "Пароль пользователя", example = "123456")
        String password
) {}
