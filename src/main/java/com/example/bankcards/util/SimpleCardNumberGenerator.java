package com.example.bankcards.util;

import org.springframework.stereotype.Component;

import java.util.Random;

@Component
public class SimpleCardNumberGenerator implements CardNumberGenerator {
    private final Random random = new Random();
    private static final int CARD_LENGTH = 16;
    @Override
    public String generate() {
        StringBuilder sb = new StringBuilder(CARD_LENGTH);
        for (int i = 0; i < CARD_LENGTH; i++) {
            sb.append(random.nextInt(10));
        }
        return sb.toString();
    }
}
