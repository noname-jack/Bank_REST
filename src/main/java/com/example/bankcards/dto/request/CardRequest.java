package com.example.bankcards.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;

@Schema(description = "Данные для создания новой карты")
public record CardRequest(
        @NotNull
        @Schema(description = "ID владельца карты", example = "1")
        Long userId,
        @NotNull
        @Future
        @Schema(description = "Дата окончания действия карты", example = "2026-12-31")
        LocalDate expirationDate,
        @NotNull
        @Schema(description = "Баланс карты", example = "1000.50")
        BigDecimal balance) {
}
