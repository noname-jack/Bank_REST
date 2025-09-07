package com.example.bankcards.dto.request;

import com.example.bankcards.entity.enums.BlockRequestStatus;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Данные для фильтрации запросов на блокировку карт")
public record FilterCardBlockRequest(
        @Schema(description = "ID пользователя", example = "1", nullable = true)
        Long userId,

        @Schema(description = "ID карты", example = "1", nullable = true)
        Long cardId,

        @Schema(description = "Статус запроса на блокировку", example = "PENDING", nullable = true)
        BlockRequestStatus status
) {
}