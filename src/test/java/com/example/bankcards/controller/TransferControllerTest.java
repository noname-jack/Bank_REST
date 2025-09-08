package com.example.bankcards.controller;

import com.example.bankcards.dto.request.TransferRequest;
import com.example.bankcards.dto.response.TransferResponse;
import com.example.bankcards.entity.User;
import com.example.bankcards.entity.enums.TransferStatus;
import com.example.bankcards.security.JwtService;
import com.example.bankcards.service.TransferService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
@WebMvcTest(controllers = TransferController.class)
@AutoConfigureMockMvc(addFilters = false)
class TransferControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private TransferService transferService;
    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private UserDetailsService userDetailsService;

    private User user;
    private TransferRequest transferRequest;
    private TransferResponse transferResponse;

    @BeforeEach
    void setup() {
        user = new User();
        user.setId(1L);
        transferRequest = new TransferRequest(1L, 2L, BigDecimal.valueOf(200.50));

        transferResponse = new TransferResponse(
                1L,
                1L,
                "****-****-****-3456",
                2L,
                "****-****-****-7654",
                BigDecimal.valueOf(200.50),
                TransferStatus.COMPLETED,
                LocalDateTime.of(2025, 9, 7, 10, 59)
        );
    }
    @BeforeEach
    void setupSecurityContext() {
        UserDetails userDetails = user;
        Authentication auth = new UsernamePasswordAuthenticationToken(userDetails, null, List.of());
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(auth);
        SecurityContextHolder.setContext(context);
    }
    @Test
    void transferBetweenMyCards_Success() throws Exception {
        when(transferService.transferBetweenMyCards(any(TransferRequest.class), eq(user.getId())))
                .thenReturn(transferResponse);

        mockMvc.perform(post("/api/transfer/my")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(transferRequest))
                        .requestAttr("user", user)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.data.id").value(transferResponse.id()))
                .andExpect(jsonPath("$.data.amount").value(transferResponse.amount()));

        verify(transferService, times(1))
                .transferBetweenMyCards(any(TransferRequest.class), eq(user.getId()));
    }
}
