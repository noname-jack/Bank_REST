package com.example.bankcards.controller;

import com.example.bankcards.dto.ApiResponseDto;
import com.example.bankcards.dto.request.AuthRequest;
import com.example.bankcards.dto.response.AuthResponse;
import com.example.bankcards.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Аутентификация", description = "API для входа в систему")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @Operation(
            summary = "Вход в систему",
            description = "Аутентификация пользователя по логину и паролю"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Успешная аутентификация"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Неверные учетные данные"
            )
    })
    @PostMapping("/login")
    public ApiResponseDto<AuthResponse> login(@RequestBody AuthRequest authRequest) {
        return ApiResponseDto.success(authService.login(authRequest));
    }
}
