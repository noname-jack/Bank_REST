package com.example.bankcards.util;

public class CardMaskUtil {

    public static String maskCardNumber(String cardNumber) {
        if (cardNumber == null) {
            return null;
        }

        if (cardNumber.length() < 4) {
            return "****";
        }
        String last4 = cardNumber.substring(cardNumber.length() - 4);
        return "**** **** **** " + last4;
    }

}
