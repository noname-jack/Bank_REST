package com.example.bankcards.dto.response;

import com.example.bankcards.entity.enums.Role;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Данные о пользователе")
public record UserResponse(
        @Schema(description = "ID пользователя", example = "1")
        Long id,

        @Schema(description = "Имя пользователя", example = "admin")
        String username,

        @Schema(description = "Роль пользователя", example = "ADMIN")
        Role role
) {
}