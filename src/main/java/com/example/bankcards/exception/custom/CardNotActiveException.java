package com.example.bankcards.exception.custom;


public class CardNotActiveException extends RuntimeException {
    public CardNotActiveException(String message) {
        super(message);
    }
}
