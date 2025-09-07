package com.example.bankcards.dto.response;

import com.example.bankcards.entity.enums.CardStatus;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.LocalDate;

@Schema(description = "Данные о карте")
public record CardResponse(
        @Schema(description = "ID карты", example = "1")
        Long id,

        @Schema(description = "Номер карты", example = "****-****-****-3456")
        String cardNumber,

        @Schema(description = "Дата окончания действия карты", example = "2026-12-31")
        LocalDate expirationDate,

        @Schema(description = "Статус карты", example = "ACTIVE")
        CardStatus status,

        @Schema(description = "Баланс карты", example = "1000.50")
        BigDecimal balance
) {
}