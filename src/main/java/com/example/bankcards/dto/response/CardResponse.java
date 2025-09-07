package com.example.bankcards.dto.response;

import com.example.bankcards.entity.enums.CardStatus;

import java.math.BigDecimal;
import java.time.LocalDate;

public record CardResponse(Long id,
                           String cardNumber,
                           LocalDate expirationDate,
                           CardStatus status,
                           BigDecimal balance) {
}
