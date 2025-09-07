package com.example.bankcards.exception.custom;

public class InvalidBlockRequestStateException extends RuntimeException {
    public InvalidBlockRequestStateException(String message) {
        super(message);
    }
}
