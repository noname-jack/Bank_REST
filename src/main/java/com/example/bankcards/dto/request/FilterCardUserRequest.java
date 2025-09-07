package com.example.bankcards.dto.request;

import com.example.bankcards.entity.enums.CardStatus;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;

@Schema(description = "Данные для фильтрации карт пользователя")
public record FilterCardUserRequest(
        @Schema(description = "ID карты", example = "1", nullable = true)
        Long id,

        @Schema(description = "Статус карты", example = "ACTIVE", nullable = true)
        CardStatus cardStatus,

        @Schema(description = "Минимальный баланс карты", example = "0.00", nullable = true)
        BigDecimal balanceMin,

        @Schema(description = "Максимальный баланс карты", example = "10000.00", nullable = true)
        BigDecimal balanceMax
) {
}