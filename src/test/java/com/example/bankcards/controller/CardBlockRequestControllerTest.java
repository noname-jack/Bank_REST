package com.example.bankcards.controller;

import com.example.bankcards.dto.request.FilterCardBlockRequest;
import com.example.bankcards.dto.response.CardBlockRequestDto;
import com.example.bankcards.dto.response.CardResponse;
import com.example.bankcards.dto.response.UserResponse;
import com.example.bankcards.entity.enums.BlockRequestStatus;
import com.example.bankcards.entity.enums.CardStatus;
import com.example.bankcards.entity.enums.Role;
import com.example.bankcards.security.JwtService;
import com.example.bankcards.service.CardBlockRequestService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
@WebMvcTest(CardBlockRequestController.class)
@AutoConfigureMockMvc(addFilters = false)
class CardBlockRequestControllerTest {


    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private UserDetailsService userDetailsService;

    @MockitoBean
    private CardBlockRequestService cardBlockRequestService;


    private CardBlockRequestDto cardBlockRequestDto;

    @BeforeEach
    void setup() {

        CardResponse cardResponse = new CardResponse(
                1L,
                "****-****-****-3456",
                LocalDate.of(2026, 12, 31),
                CardStatus.ACTIVE,
                BigDecimal.valueOf(1000.50)
        );

        UserResponse userResponse = new UserResponse(1L, "testUser", Role.USER);


        cardBlockRequestDto = new CardBlockRequestDto(
                1L,
                cardResponse,
                userResponse,
                BlockRequestStatus.PENDING,
                LocalDateTime.of(2025, 9, 7, 10, 59)
        );


    }

    @Test
    void getAllCardBlockRequest_Success() throws Exception {
        Page<CardBlockRequestDto> page = new PageImpl<>(List.of(cardBlockRequestDto));
        when(cardBlockRequestService.getAllCardBlockRequest(any(Pageable.class), any(FilterCardBlockRequest.class)))
                .thenReturn(page);

        mockMvc.perform(get("/api/cards/block-requests")
                        .param("page", "0")
                        .param("size", "20")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.data.content[0].id").value(cardBlockRequestDto.id()))
                .andExpect(jsonPath("$.data.content[0].status").value(cardBlockRequestDto.status().name()));

        verify(cardBlockRequestService, times(1))
                .getAllCardBlockRequest(any(Pageable.class), any());
    }

    @Test
    void approveCardBlock_WithResult() throws Exception {
        when(cardBlockRequestService.getRequestById(1L)).thenReturn(cardBlockRequestDto);

        mockMvc.perform(post("/api/cards/block-requests/1/approve")
                        .param("withResult", "true")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.data.id").value(cardBlockRequestDto.id()));

        verify(cardBlockRequestService, times(1)).approveCardBlock(1L);
        verify(cardBlockRequestService, times(1)).getRequestById(1L);
    }

    @Test
    void approveCardBlock_NoResult() throws Exception {
        mockMvc.perform(post("/api/cards/block-requests/1/approve")
                        .param("withResult", "false")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        verify(cardBlockRequestService, times(1)).approveCardBlock(1L);
        verify(cardBlockRequestService, never()).getRequestById(1L);
    }

    @Test
    void rejectCardBlock_WithResult() throws Exception {
        when(cardBlockRequestService.getRequestById(1L)).thenReturn(cardBlockRequestDto);

        mockMvc.perform(post("/api/cards/block-requests/1/reject")
                        .param("withResult", "true")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.data.id").value(cardBlockRequestDto.id()));

        verify(cardBlockRequestService, times(1)).rejectCardBlock(1L);
        verify(cardBlockRequestService, times(1)).getRequestById(1L);
    }

    @Test
    void rejectCardBlock_NoResult() throws Exception {
        mockMvc.perform(post("/api/cards/block-requests/1/reject")
                        .param("withResult", "false")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        verify(cardBlockRequestService, times(1)).rejectCardBlock(1L);
        verify(cardBlockRequestService, never()).getRequestById(1L);
    }
}

