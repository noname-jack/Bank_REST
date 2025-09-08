package com.example.bankcards.service.impl;

import com.example.bankcards.dto.request.AuthRequest;
import com.example.bankcards.dto.response.AuthResponse;
import com.example.bankcards.entity.User;
import com.example.bankcards.security.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;


import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AuthServiceImplTest {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtService jwtService;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private AuthServiceImpl authService;

    private AuthRequest  validAuthRequest;
    private AuthRequest invalidAuthRequest;
    private UserDetails userDetails;
    private String token;

    @BeforeEach
    void setUp() {
        validAuthRequest = new AuthRequest("testUser", "password");
        invalidAuthRequest = new AuthRequest("wrongUser", "wrongPassword");
        User user = new User();
        user.setUsername("testUser");
        user.setPasswordHash("password");
        userDetails = user;
        token = "jwt-token";
    }

    @Test
    void login_SuccessfulAuthentication_ReturnsAuthResponseWithToken() {

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(jwtService.generateToken(userDetails)).thenReturn(token);

        AuthResponse response = authService.login(validAuthRequest);

        assertNotNull(response);
        assertEquals(token, response.accessToken());
        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(jwtService).generateToken(userDetails);
    }

    @Test
    void login_WithInvalidCredentials_ShouldThrowBadCredentialsException() {
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadCredentialsException("Неверные учетные данные пользователя"));

        BadCredentialsException exception = assertThrows(
                BadCredentialsException.class,
                () -> authService.login(invalidAuthRequest)
        );

        assertEquals("Неверные учетные данные пользователя", exception.getMessage());

        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(jwtService, never()).generateToken(any());
    }

}
