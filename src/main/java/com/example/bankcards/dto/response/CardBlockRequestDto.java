package com.example.bankcards.dto.response;

import com.example.bankcards.entity.enums.BlockRequestStatus;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

@Schema(description = "Данные о запросе на блокировку карты")
public record CardBlockRequestDto(
        @Schema(description = "ID запроса на блокировку", example = "1")
        Long id,

        @Schema(description = "Данные о карте")
        CardResponse card,

        @Schema(description = "Данные о пользователе")
        UserResponse user,

        @Schema(description = "Статус запроса на блокировку", example = "PENDING")
        BlockRequestStatus status,

        @Schema(description = "Дата и время создания запроса", example = "2025-09-07T10:59:00")
        LocalDateTime createdAt
) {
}