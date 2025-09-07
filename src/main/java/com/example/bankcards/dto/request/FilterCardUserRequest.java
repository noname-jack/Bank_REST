package com.example.bankcards.dto.request;

import com.example.bankcards.entity.enums.CardStatus;

import java.math.BigDecimal;

public record FilterCardUserRequest(Long id,
                                    String cardNumber,
                                    CardStatus cardStatus,
                                    BigDecimal balanceMin,
                                    BigDecimal balanceMax) {
}
