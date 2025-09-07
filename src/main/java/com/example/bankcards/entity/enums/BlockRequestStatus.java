package com.example.bankcards.entity.enums;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Статус запроса на блокировку карты")
public enum BlockRequestStatus {
    @Schema(description = "Запрос на блокировку ожидает рассмотрения")
    PENDING,

    @Schema(description = "Запрос на блокировку одобрен")
    APPROVED,

    @Schema(description = "Запрос на блокировку отклонен")
    REJECTED
}