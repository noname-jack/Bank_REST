package com.example.bankcards.exception.custom;

public class NotFoundException extends RuntimeException {
    public NotFoundException(String message){
        super(message);
    }
}
