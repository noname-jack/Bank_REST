package com.example.bankcards.dto.response;

import com.example.bankcards.entity.enums.TransferStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record TransferResponse(
        Long id,
        Long fromCardId,
        String fromCardNumber,
        Long toCardId,
        String toCardNumber,
        BigDecimal amount,
        TransferStatus status,
        LocalDateTime createdAt
) {}