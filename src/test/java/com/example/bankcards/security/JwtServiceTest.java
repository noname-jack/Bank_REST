package com.example.bankcards.security;

import com.example.bankcards.entity.User;
import com.example.bankcards.entity.enums.Role;
import com.example.bankcards.exception.custom.InvalidJwtException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class JwtServiceTest {

    @InjectMocks
    private JwtService jwtService;


    private final String testSecret = "mySecretKeyMySecretKeyMySecretKeyMySecretKeyMySecretKeyMySe";
    private final long testExpiration = 3600000;

    private User testUser;


    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(jwtService, "jwtSecret", testSecret);
        ReflectionTestUtils.setField(jwtService, "jwtExpiration", testExpiration);

        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testUser");
        testUser.setRole(Role.USER);

    }

    @Test
    void generateToken_WithUserEntity_ShouldIncludeCustomClaims() {
        String token = jwtService.generateToken(testUser);

        assertNotNull(token);
        assertFalse(token.isEmpty());

        String[] tokenParts = token.split("\\.");
        assertEquals(3, tokenParts.length);

        Claims claims = parseTokenClaims(token);
        assertEquals("testUser", claims.getSubject());
        assertEquals(1L, claims.get("id", Long.class));
        assertEquals("testUser", claims.get("username"));
        assertEquals("USER", claims.get("role"));
        assertNotNull(claims.getIssuedAt());
        assertNotNull(claims.getExpiration());
    }


    @Test
    void generateToken_ShouldSetCorrectExpirationTime() {
        Instant beforeGeneration = Instant.now();

        String token = jwtService.generateToken(testUser);

        Claims claims = parseTokenClaims(token);
        Date expiration = claims.getExpiration();
        Date issuedAt = claims.getIssuedAt();

        long actualDuration = expiration.getTime() - issuedAt.getTime();
        assertEquals(testExpiration, actualDuration);

        assertTrue(expiration.after(Date.from(beforeGeneration)));
    }

    @Test
    void isTokenValid_WithCorrectUser_ShouldReturnTrue() {
        String token = jwtService.generateToken(testUser);

        boolean isValid = jwtService.isTokenValid(token, testUser);

        assertTrue(isValid);
    }

    @Test
    void isTokenValid_WithDifferentUser_ShouldReturnFalse() {
        String token = jwtService.generateToken(testUser);

        User testUser1 = new User();
        testUser1.setUsername("testUser1");

        boolean isValid = jwtService.isTokenValid(token, testUser1);

        assertFalse(isValid);
    }


    @Test
    void extractUserName_WithValidToken_ShouldReturnUsername() {
        String token = jwtService.generateToken(testUser);

        String extractedUsername = jwtService.extractUserName(token);

        assertEquals("testUser", extractedUsername);
    }

    @Test
    void extractUserName_WithMalformedToken_ShouldThrowException() {
        String malformedToken = "not-valid-jwt-token";

        InvalidJwtException exception = assertThrows(
                InvalidJwtException.class,
                () -> jwtService.extractUserName(malformedToken)
        );

        assertEquals("Invalid token", exception.getMessage());
    }



    private Claims parseTokenClaims(String token) {
        SecretKey key = Keys.hmacShaKeyFor(testSecret.getBytes(StandardCharsets.UTF_8));
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
