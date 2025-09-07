package com.example.bankcards.util;

import jakarta.persistence.AttributeConverter;
import org.springframework.stereotype.Component;

@Component
public class CardNumberConverter implements AttributeConverter<String, String> {

    private final Encryptor encryptor;

    public CardNumberConverter(Encryptor encryptor) {
        this.encryptor = encryptor;
    }

    @Override
    public String convertToDatabaseColumn(String cardNumberDecrypt) {
        if (cardNumberDecrypt == null) {
            return null;
        }
        return encryptor.encrypt(cardNumberDecrypt);
    }

    @Override
    public String convertToEntityAttribute(String cardNumberEncrypt) {
        if (cardNumberEncrypt == null) {
            return null;
        }
        return encryptor.decrypt(cardNumberEncrypt);
    }
}
