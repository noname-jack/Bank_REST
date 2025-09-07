package com.example.bankcards.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
@Schema(description = "Данные для перевода между картами")
public record TransferRequest(
        @NotNull
        @Schema(description = "ID карты отправителя", example = "1")
        Long fromCardId,
        @NotNull
        @Schema(description = "ID карты получателя", example = "2")
        Long toCardId,
        @NotNull
        @DecimalMin(value = "1")
        @DecimalMax(value = "100000.00")
        @Schema(description = "Сумма перевода", example = "100.50")
        BigDecimal amount) {
}
