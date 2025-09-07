package com.example.bankcards.dto.request;

import com.example.bankcards.entity.enums.CardStatus;

import java.math.BigDecimal;

public record FilterCardAdminRequest(
        Long id,
        Long ownerId,
        String ownerUserName,
        CardStatus cardStatus,
        BigDecimal balanceMin,
        BigDecimal balanceMax
) {

}
