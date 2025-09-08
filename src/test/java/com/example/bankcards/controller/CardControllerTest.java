package com.example.bankcards.controller;

import com.example.bankcards.dto.request.CardRequest;
import com.example.bankcards.dto.request.FilterCardAdminRequest;
import com.example.bankcards.dto.request.FilterCardUserRequest;
import com.example.bankcards.dto.response.CardDetailedResponse;
import com.example.bankcards.dto.response.CardResponse;
import com.example.bankcards.entity.User;
import com.example.bankcards.entity.enums.CardStatus;
import com.example.bankcards.security.JwtService;
import com.example.bankcards.service.CardBlockRequestService;
import com.example.bankcards.service.CardService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
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
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CardController.class)
@AutoConfigureMockMvc(addFilters = false)
class CardControllerTest {

    @Autowired
    private MockMvc mockMvc;


    @Autowired
    private ObjectMapper objectMapper;
    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private UserDetailsService userDetailsService;

    @MockitoBean
    private CardService cardService;

    @MockitoBean
    private CardBlockRequestService cardBlockRequestService;

    private CardRequest cardRequest;
    private CardDetailedResponse cardDetailedResponse;
    private CardResponse cardResponse;
    private User user;


    @BeforeEach
    void setUp() {
        cardRequest = new CardRequest(
                1L,
                LocalDate.of(2026, 12, 31),
                BigDecimal.valueOf(1000.50));
        cardResponse = new CardResponse(
                1L,
                "****-****-****-3456",
                LocalDate.of(2026, 12, 31),
                CardStatus.ACTIVE,
                BigDecimal.valueOf(1000.50));
        cardDetailedResponse = new CardDetailedResponse(
                1L,
                "****-****-****-3456",
                LocalDate.of(2026, 12, 31),
                CardStatus.ACTIVE,
                BigDecimal.valueOf(1000.50),
                1L,
                "testUser",
                Collections.emptyList(),
                Collections.emptyList());

        user = new User();
        user.setId(1L);

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
    void getCardByIdForAdmin_Success() throws Exception {
        when(cardService.getCardDetailsById(1L)).thenReturn(cardDetailedResponse);

        mockMvc.perform(get("/api/cards/{cardId}", 1L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value(cardDetailedResponse.id()))
                .andExpect(jsonPath("$.data.cardNumber").value(cardDetailedResponse.cardNumber()))
                .andExpect(jsonPath("$.data.expirationDate").value(cardDetailedResponse.expirationDate().toString()))
                .andExpect(jsonPath("$.data.status").value(cardDetailedResponse.status().name()))
                .andExpect(jsonPath("$.data.balance").value(cardDetailedResponse.balance().doubleValue()))
                .andExpect(jsonPath("$.data.ownerId").value(cardDetailedResponse.ownerId()))
                .andExpect(jsonPath("$.data.ownerUsername").value(cardDetailedResponse.ownerUsername()))
                .andExpect(jsonPath("$.data.transfersFrom").isArray())
                .andExpect(jsonPath("$.data.transfersTo").isArray());
        ;

        verify(cardService, times(1)).getCardDetailsById(1L);
    }

    @Test
    void getAllCardsForAdmin_EmptyFilter() throws Exception {
        Page<CardResponse> page = new PageImpl<>(List.of(cardResponse));
        when(cardService.getAllCards(any(Pageable.class), any(FilterCardAdminRequest.class))).thenReturn(page);

        mockMvc.perform(get("/api/cards")
                        .param("page", "0")
                        .param("size", "10")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.message").value("success"))
                .andExpect(jsonPath("$.error").isEmpty())
                .andExpect(jsonPath("$.data.page.number").value(0))
                .andExpect(jsonPath("$.data.page.totalElements").value(1))
                .andExpect(jsonPath("$.data.page.totalPages").value(1))
                .andExpect(jsonPath("$.data.content[0].id").value(1));
        verify(cardService, times(1)).getAllCards(any(Pageable.class), any());
    }

    @Test
    void createCard_Success() throws Exception {
        when(cardService.createCard(any(CardRequest.class))).thenReturn(cardDetailedResponse);

        mockMvc.perform(post("/api/cards")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(cardRequest)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value("CREATED"))
                .andExpect(jsonPath("$.message").value("success"))
                .andExpect(jsonPath("$.error").isEmpty())
                .andExpect(jsonPath("$.data.id").value(cardDetailedResponse.id()));

        verify(cardService, times(1)).createCard(any(CardRequest.class));
    }

    @Test
    void deleteCard_Success() throws Exception {
        Long cardId = 1L;
        doNothing().when(cardService).deleteCardById(cardId);

        mockMvc.perform(delete("/api/cards/{id}", cardId))
                .andDo(print())
                .andExpect(status().isNoContent());

        verify(cardService, times(1)).deleteCardById(cardId);
    }

    @Test
    void activateCard_WithResult() throws Exception {

        when(cardService.getCardDetailsById(1L)).thenReturn(cardDetailedResponse);
        doNothing().when(cardService).updateStatusCardById(1L, CardStatus.ACTIVE);

        mockMvc.perform(patch("/api/cards/{id}/activate", 1L)
                        .param("withResult", "true")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.data.id").value(cardDetailedResponse.id()));

        verify(cardService, times(1)).updateStatusCardById(1L, CardStatus.ACTIVE);
        verify(cardService, times(1)).getCardDetailsById(1L);
    }

    @Test
    void activateCard_WithoutResult() throws Exception {
        doNothing().when(cardService).updateStatusCardById(1L, CardStatus.ACTIVE);

        mockMvc.perform(patch("/api/cards/{id}/activate", 1L)
                        .param("withResult", "false")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNoContent());

        verify(cardService, times(1)).updateStatusCardById(1L, CardStatus.ACTIVE);
        verify(cardService, times(0)).getCardDetailsById(anyLong());
    }


    @Test
    void blockCard_WithResult() throws Exception {


        when(cardService.getCardDetailsById(1L)).thenReturn(cardDetailedResponse);
        doNothing().when(cardService).updateStatusCardById(1L, CardStatus.BLOCKED);


        mockMvc.perform(patch("/api/cards/{id}/block", 1L)
                        .param("withResult", "true")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.data.id").value(cardDetailedResponse.id()));

        verify(cardService, times(1)).updateStatusCardById(1L, CardStatus.BLOCKED);
        verify(cardService, times(1)).getCardDetailsById(1L);
    }

    @Test
    void blockCard_WithoutResult() throws Exception {
        doNothing().when(cardService).updateStatusCardById(1L, CardStatus.BLOCKED);

        mockMvc.perform(patch("/api/cards/{id}/block", 1L)
                        .param("withResult", "false")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNoContent());

        verify(cardService, times(1)).updateStatusCardById(1L, CardStatus.BLOCKED);
        verify(cardService, times(0)).getCardDetailsById(anyLong());
    }

    @Test
    void getMyCards_Success() throws Exception {
        Page<CardResponse> cardPage = new PageImpl<>(List.of(cardResponse));
        when(cardService.getAllCardsByUserId(any(Pageable.class), any(FilterCardUserRequest.class), eq(user.getId()))).thenReturn(cardPage);
        mockMvc
                .perform(get("/api/cards/my")
                        .param("page", "0")
                        .param("size", "20")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.data.content.length()").value(1))
                .andExpect(jsonPath("$.data.content[0].id").value(1))
                .andExpect(jsonPath("$.data.page.number").value(0))
                .andExpect(jsonPath("$.data.page.totalElements").value(1))
                .andExpect(jsonPath("$.data.page.totalPages").value(1));
        verify(cardService, times(1)).getAllCardsByUserId(any(Pageable.class), any(FilterCardUserRequest.class), eq(user.getId()));
    }


    @Test
    void getMyCardById_Success() throws Exception {
        Long cardId = 1L;

        when(cardService.getCardDetailsByIdAndUserId(cardId, user.getId()))
                .thenReturn(cardDetailedResponse);

        mockMvc.perform(get("/api/cards/my/{cardId}", cardId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.data.id").value(cardId))
                .andExpect(jsonPath("$.data.ownerId").value(user.getId()));

        verify(cardService, times(1)).getCardDetailsByIdAndUserId(cardId, user.getId());
    }


}
