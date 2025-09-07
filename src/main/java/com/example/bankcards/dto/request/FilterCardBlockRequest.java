package com.example.bankcards.dto.request;

import com.example.bankcards.entity.enums.BlockRequestStatus;

public record FilterCardBlockRequest(Long userId,
                                    Long cardId,
                                    BlockRequestStatus status) {
}
