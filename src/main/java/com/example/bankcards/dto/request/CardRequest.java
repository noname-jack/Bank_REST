package com.example.bankcards.dto.request;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import org.springframework.boot.context.properties.bind.DefaultValue;

import java.math.BigDecimal;
import java.time.LocalDate;

public record CardRequest(@NotNull Long userId,
                          @NotNull @Future LocalDate expirationDate,
                          @NotNull BigDecimal balance) {
}
