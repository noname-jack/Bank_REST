package com.example.bankcards.service.impl;

import com.example.bankcards.dto.request.CardRequest;
import com.example.bankcards.dto.request.FilterCardAdminRequest;
import com.example.bankcards.dto.request.FilterCardUserRequest;
import com.example.bankcards.dto.response.CardDetailedResponse;
import com.example.bankcards.dto.response.CardResponse;
import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.User;
import com.example.bankcards.entity.enums.CardStatus;
import com.example.bankcards.exception.custom.CardNotActiveException;
import com.example.bankcards.exception.custom.CardNumberGenerationException;
import com.example.bankcards.exception.custom.NotFoundException;
import com.example.bankcards.mapper.CardMapper;
import com.example.bankcards.repository.CardRepository;
import com.example.bankcards.service.UserService;
import com.example.bankcards.util.CardNumberGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CardServiceImplTest {

    @Mock
    private CardRepository cardRepository;

    @Mock
    private CardMapper cardMapper;

    @Mock
    private UserService userService;

    @Mock
    private CardNumberGenerator cardNumberGenerator;

    @InjectMocks
    private CardServiceImpl cardService;

    private Card card;
    private User user;
    private CardRequest cardRequest;
    private CardDetailedResponse cardDetailedResponse;
    private CardResponse cardResponse;
    private Pageable pageable;
    private FilterCardAdminRequest filterAdminRequest;
    private FilterCardUserRequest filterUserRequest;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setUsername("testUser");

        card = new Card();
        card.setId(1L);
        card.setUser(user);
        card.setCardNumber("1234567890123456");
        card.setBalance(BigDecimal.valueOf(1000.50));
        card.setStatus(CardStatus.ACTIVE);
        card.setExpirationDate(LocalDate.of(2026, 12, 31));

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

        filterAdminRequest = mock(FilterCardAdminRequest.class);
        filterUserRequest = mock(FilterCardUserRequest.class);
        pageable = mock(Pageable.class);
    }

    @Test
    void getCardDetailsById_Successful_ReturnsCardDetailedResponse() {
        when(cardRepository.findById(1L)).thenReturn(Optional.of(card));
        when(cardMapper.toCardDetailedResponse(card)).thenReturn(cardDetailedResponse);

        CardDetailedResponse result = cardService.getCardDetailsById(1L);

        assertNotNull(result);
        assertEquals(cardDetailedResponse, result);

        verify(cardRepository).findById(1L);
        verify(cardMapper).toCardDetailedResponse(card);
    }

    @Test
    void getCardDetailsById_CardNotFound_ThrowsNotFoundException() {
        when(cardRepository.findById(1L)).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> cardService.getCardDetailsById(1L)
        );

        assertEquals("Карта не найдена", exception.getMessage());

        verify(cardRepository).findById(1L);
        verifyNoInteractions(cardMapper);
    }

    @Test
    void getCardDetailsByIdAndUserId_Successful_ReturnsCardDetailedResponse() {
        when(cardRepository.findByIdAndUserId(1L, 1L)).thenReturn(Optional.of(card));
        when(cardMapper.toCardDetailedResponse(card)).thenReturn(cardDetailedResponse);

        CardDetailedResponse result = cardService.getCardDetailsByIdAndUserId(1L, 1L);

        assertNotNull(result);

        assertEquals(cardDetailedResponse, result);

        verify(cardRepository).findByIdAndUserId(1L, 1L);
        verify(cardMapper).toCardDetailedResponse(card);
    }

    @Test
    void getAllCards_AdminFilter_ReturnsPageOfCardResponse() {
        Page<Card> cardPage = new PageImpl<>(List.of(card));

        when(cardRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(cardPage);
        when(cardMapper.toCardResponse(card)).thenReturn(cardResponse);

        Page<CardResponse> result = cardService.getAllCards(pageable, filterAdminRequest);

        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        assertEquals(cardResponse, result.getContent().get(0));

        verify(cardRepository).findAll(any(Specification.class), eq(pageable));
        verify(cardMapper).toCardResponse(card);
    }

    @Test
    void getAllCardsByUserId_UserFilter_ReturnsPageOfCardResponse() {
        Page<Card> cardPage = new PageImpl<>(List.of(card));
        when(cardRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(cardPage);
        when(cardMapper.toCardResponse(card)).thenReturn(cardResponse);

        Page<CardResponse> result = cardService.getAllCardsByUserId(pageable, filterUserRequest, 1L);

        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        assertEquals(cardResponse, result.getContent().get(0));

        verify(cardRepository).findAll(any(Specification.class), eq(pageable));
        verify(cardMapper).toCardResponse(card);
    }

    @Test
    void createCard_Successful_ReturnsCardDetailedResponse() {
        when(userService.getUserEntityById(1L)).thenReturn(user);
        when(cardNumberGenerator.generate()).thenReturn("1234567890123456");
        when(cardRepository.existsByCardNumber("1234567890123456")).thenReturn(false);
        when(cardMapper.toCard(user, "1234567890123456", cardRequest)).thenReturn(card);
        when(cardRepository.save(card)).thenReturn(card);
        when(cardMapper.toCardDetailedResponse(card)).thenReturn(cardDetailedResponse);

        CardDetailedResponse result = cardService.createCard(cardRequest);

        assertNotNull(result);
        assertEquals(cardDetailedResponse, result);

        verify(userService).getUserEntityById(1L);
        verify(cardNumberGenerator).generate();
        verify(cardRepository).existsByCardNumber("1234567890123456");
        verify(cardRepository).save(card);
        verify(cardMapper).toCardDetailedResponse(card);
    }

    @Test
    void createCard_CardNumberGenerationFails_ThrowsCardNumberGenerationException() {
        when(userService.getUserEntityById(1L)).thenReturn(user);
        when(cardNumberGenerator.generate()).thenReturn("1234567890123456");
        when(cardRepository.existsByCardNumber("1234567890123456")).thenReturn(true);

        CardNumberGenerationException exception = assertThrows(CardNumberGenerationException.class, () -> cardService.createCard(cardRequest));

        assertEquals("Не удалось сгенерировать уникальный номер", exception.getMessage());

        verify(userService).getUserEntityById(1L);
        verify(cardNumberGenerator, times(1000)).generate();
        verify(cardRepository, times(1000)).existsByCardNumber("1234567890123456");
        verifyNoMoreInteractions(cardRepository, cardMapper);
    }

    @Test
    void validateCardStatus_InactiveCard_ThrowsCardNotActiveException() {
        card.setStatus(CardStatus.BLOCKED);

        assertThrows(CardNotActiveException.class, () -> cardService.validateCardStatus(card));
    }

    @Test
    void transfer_Successful_UpdatesBalances() {
        Card toCard = new Card();
        toCard.setId(2L);
        toCard.setBalance(BigDecimal.valueOf(500));
        toCard.setStatus(CardStatus.ACTIVE);
        toCard.setExpirationDate(LocalDate.of(2026, 12, 31));

        BigDecimal amount = BigDecimal.valueOf(200);

        cardService.transfer(card, toCard, amount);

        assertEquals(BigDecimal.valueOf(800.50), card.getBalance());
        assertEquals(BigDecimal.valueOf(700), toCard.getBalance());

        verify(cardRepository).save(card);
        verify(cardRepository).save(toCard);
    }

    @Test
    void deleteCardById_Successful_DeletesCard() {
        when(cardRepository.findById(1L)).thenReturn(Optional.of(card));

        cardService.deleteCardById(1L);

        verify(cardRepository).findById(1L);
        verify(cardRepository).delete(card);
    }

    @Test
    void deleteCardById_WithNonExistingId_ShouldThrowNotFoundException() {
        when(cardRepository.findById(999L)).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> cardService.deleteCardById(999L)
        );

        assertEquals("Карта не найдена", exception.getMessage());

        verify(cardRepository).findById(999L);
        verify(cardRepository, never()).delete((Card) any());

    }

    @Test
    void updateStatusCardById_Successful_UpdatesStatus() {
        when(cardRepository.findById(1L)).thenReturn(Optional.of(card));
        when(cardRepository.save(card)).thenReturn(card);

        cardService.updateStatusCardById(1L, CardStatus.BLOCKED);

        assertEquals(CardStatus.BLOCKED, card.getStatus());

        verify(cardRepository).findById(1L);
        verify(cardRepository).save(card);
    }


        @Test
        void updateStatusCardById_WithNonExistingId_ShouldThrowNotFoundException() {
            when(cardRepository.findById(999L)).thenReturn(Optional.empty());

            NotFoundException exception = assertThrows(
                    NotFoundException.class,
                    () -> cardService.updateStatusCardById(999L, CardStatus.BLOCKED)
            );

            assertEquals("Карта не найдена", exception.getMessage());

            verify(cardRepository).findById(999L);
            verify(cardRepository, never()).save((any()));

        }
    }