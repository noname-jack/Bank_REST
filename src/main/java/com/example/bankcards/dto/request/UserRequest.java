package com.example.bankcards.dto.request;

import com.example.bankcards.entity.enums.Role;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record UserRequest (
    @NotBlank
    @Size(min = 3, max = 100)
    String username,
    @NotBlank
    @Size(min = 6, max = 100)
    String password,
    @NotNull
    Role role
){
}
