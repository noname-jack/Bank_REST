
package com.example.bankcards.util;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.util.Base64;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class EncryptorTest {

    private Encryptor encryptor;

    @BeforeEach
    void setUp() throws Exception {
        KeyGenerator keyGen = KeyGenerator.getInstance("AES");
        keyGen.init(256);
        SecretKey secretKey = keyGen.generateKey();
        String validBase64Key = Base64.getEncoder().encodeToString(secretKey.getEncoded());

        encryptor = new Encryptor(validBase64Key);
    }

    @Test
    void encryptDecrypt_WithValidText_ShouldReturnOriginalText() {
        String originalText = "Hello World!";

        String encrypted = encryptor.encrypt(originalText);
        String decrypted = encryptor.decrypt(encrypted);

        assertNotNull(encrypted);
        assertNotEquals(originalText, encrypted);
        assertEquals(originalText, decrypted);
    }

    @Test
    void encryptDecrypt_WithEmptyString_ShouldWork() {
        String emptyText = "";

        String encrypted = encryptor.encrypt(emptyText);
        String decrypted = encryptor.decrypt(encrypted);

        assertNotNull(encrypted);
        assertFalse(encrypted.isEmpty());
        assertEquals(emptyText, decrypted);
    }


    @Test
    void encryptDecrypt_WithCardNumber_ShouldWork() {
        String cardNumber = "4532015112830366";

        String encrypted = encryptor.encrypt(cardNumber);
        String decrypted = encryptor.decrypt(encrypted);

        assertEquals(cardNumber, decrypted);
        assertNotEquals(cardNumber, encrypted);
    }

    @Test
    void encrypt_WithNullPlaintext_ShouldThrowException() {
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> encryptor.encrypt(null));
        assertEquals("Encryption error", exception.getMessage());
    }

    @Test
    void decrypt_WithInvalidData_ShouldThrowException() {
        String invalidData = "invalid-encrypted";

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> encryptor.decrypt(invalidData));

        assertEquals("Decryption error", exception.getMessage());
    }

    @Test
    void decrypt_WithNullData_ShouldThrowException() {
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> encryptor.decrypt(null));

        assertEquals("Decryption error", exception.getMessage());
    }

    @Test
    void decrypt_WithEmptyData_ShouldThrowException() {
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> encryptor.decrypt(""));

        assertEquals("Decryption error", exception.getMessage());
    }
}