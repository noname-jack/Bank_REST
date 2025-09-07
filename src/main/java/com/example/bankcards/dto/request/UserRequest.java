package com.example.bankcards.dto.request;

import com.example.bankcards.entity.enums.Role;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Schema(description = "Данные для создания/обновления пользователя")
public record UserRequest (
    @NotBlank
    @Size(min = 3, max = 100)
    @Schema(description = "Имя пользователя", example = "admin")
    String username,

    @Schema(description = "Пароль пользователя", example = "123456")
    @NotBlank
    @Size(min = 6, max = 100)
    String password,

    @Schema(description = "Роль пользователя")
    @NotNull
    Role role
){
}
