package com.example.bankcards.dto.response;

import com.example.bankcards.entity.enums.CardStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public record CardDetailedResponse(Long id,
                                   String cardNumber,
                                   LocalDate expirationDate,
                                   CardStatus status,
                                   BigDecimal balance,
                                   Long ownerId,
                                   String ownerUsername,
                                   List<TransferResponse> transfersFrom,
                                   List<TransferResponse> transfersTo) {
}
