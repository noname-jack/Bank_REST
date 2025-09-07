package com.example.bankcards.dto.response;

import com.example.bankcards.entity.enums.TransferStatus;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Schema(description = "Данные о переводе между картами")
public record TransferResponse(
        @Schema(description = "ID перевода", example = "1")
        Long id,

        @Schema(description = "ID карты отправителя", example = "1")
        Long fromCardId,

        @Schema(description = "Номер карты отправителя", example = "****-****-****-3456")
        String fromCardNumber,

        @Schema(description = "ID карты получателя", example = "2")
        Long toCardId,

        @Schema(description = "Номер карты получателя", example = "****-****-****-7654")
        String toCardNumber,

        @Schema(description = "Сумма перевода", example = "100.50")
        BigDecimal amount,

        @Schema(description = "Статус перевода", example = "COMPLETED")
        TransferStatus status,

        @Schema(description = "Дата и время создания перевода", example = "2025-09-07T10:59:00")
        LocalDateTime createdAt
) {
}