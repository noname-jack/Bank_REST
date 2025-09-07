package com.example.bankcards.exception.custom;

public class TransferException extends RuntimeException {
    public TransferException(String message) {
        super(message);
    }
}
