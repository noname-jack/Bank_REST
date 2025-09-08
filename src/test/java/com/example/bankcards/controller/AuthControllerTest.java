package com.example.bankcards.controller;

import com.example.bankcards.dto.request.AuthRequest;
import com.example.bankcards.dto.response.AuthResponse;
import com.example.bankcards.security.JwtService;
import com.example.bankcards.service.AuthService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AuthService authService;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private UserDetailsService userDetailsService;

    @Autowired
    private ObjectMapper objectMapper;

    private AuthRequest validAuthRequest;
    private AuthRequest invalidAuthRequest;
    private AuthResponse authResponse;

    @BeforeEach
    void setUp() {
        validAuthRequest = new AuthRequest("testUser", "password123");
        invalidAuthRequest = new AuthRequest("wrongUser", "wrongPassword");
        authResponse = new AuthResponse("jwt-token");
    }

    @Test
    void login_WithValidCredentials_ShouldReturnSuccessResponse() throws Exception {

        when(authService.login(any(AuthRequest.class))).thenReturn(authResponse);

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validAuthRequest)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value("success"))
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.data.accessToken").value("jwt-token"));


        verify(authService).login(argThat(request ->
                request.username().equals("testUser") &&
                        request.password().equals("password123")
        ));
    }

    @Test
    void login_WithInvalidCredentials_ShouldReturn401() throws Exception {
        when(authService.login(any(AuthRequest.class)))
                .thenThrow(new BadCredentialsException("Неверные учетные данные пользователя"));

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidAuthRequest)))
                .andDo(print())
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("error"))
                .andExpect(jsonPath("$.data").doesNotExist())
                .andExpect(jsonPath("$.error").value("Ошибка аутентификации: Неверные учетные данные пользователя"))
                .andExpect(jsonPath("$.status").value("UNAUTHORIZED"));

        verify(authService).login(any(AuthRequest.class));
    }


    @Test
    void login_WithEmptyBody_ShouldReturn400() throws Exception {
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(""))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("error"))
                .andExpect(jsonPath("$.data").doesNotExist())
                .andExpect(jsonPath("$.error").value("Некорректный формат JSON запроса"))
                .andExpect(jsonPath("$.status").value("BAD_REQUEST"));
        verify(authService, never()).login(any());
    }

}