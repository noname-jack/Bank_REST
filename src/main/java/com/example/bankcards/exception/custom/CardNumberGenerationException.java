package com.example.bankcards.exception.custom;

public class CardNumberGenerationException extends RuntimeException {
    public CardNumberGenerationException(String message) {
        super(message);
    }
}
