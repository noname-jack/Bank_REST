package com.example.bankcards.dto.response;

import com.example.bankcards.entity.enums.CardStatus;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Schema(description = "Подробные данные о карте, включая переводы")
public record CardDetailedResponse(
        @Schema(description = "ID карты", example = "1")
        Long id,

        @Schema(description = "Номер карты", example = "****-****-****-3456")
        String cardNumber,

        @Schema(description = "Дата окончания действия карты", example = "2026-12-31")
        LocalDate expirationDate,

        @Schema(description = "Статус карты", example = "ACTIVE")
        CardStatus status,

        @Schema(description = "Баланс карты", example = "1000.50")
        BigDecimal balance,

        @Schema(description = "ID владельца карты", example = "1")
        Long ownerId,

        @Schema(description = "Имя пользователя владельца карты", example = "admin")
        String ownerUsername,

        @Schema(description = "Список исходящих переводов")
        List<TransferResponse> transfersFrom,

        @Schema(description = "Список входящих переводов")
        List<TransferResponse> transfersTo
) {
}