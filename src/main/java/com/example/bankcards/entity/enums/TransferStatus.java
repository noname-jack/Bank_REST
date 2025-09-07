package com.example.bankcards.entity.enums;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Статус операции перевода средств")
public enum TransferStatus {
    @Schema(description = "Перевод находится в обработке")
    PENDING,

    @Schema(description = "Перевод успешно завершен")
    COMPLETED,

    @Schema(description = "Перевод не выполнен из-за ошибки")
    FAILED
}