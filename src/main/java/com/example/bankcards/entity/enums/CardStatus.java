package com.example.bankcards.entity.enums;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Статус банковской карты")
public enum CardStatus {
    @Schema(description = "Карта активна и доступна для использования")
    ACTIVE,

    @Schema(description = "Карта заблокирована и не может использоваться")
    BLOCKED,

    @Schema(description = "Срок действия карты истек")
    EXPIRED
}