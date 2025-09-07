package com.example.bankcards.dto.request;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record TransferRequest(@NotNull Long fromCardId,
                              @NotNull Long toCardId,
                              @NotNull @DecimalMin(value = "1")  @DecimalMax(value = "100000.00") BigDecimal amount) {
}
