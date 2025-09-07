package com.example.bankcards.entity.enums;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Роль пользователя в системе")
public enum Role {
    @Schema(description = "Администратор с полными правами доступа")
    ADMIN,

    @Schema(description = "Обычный пользователь с ограниченными правами")
    USER
}