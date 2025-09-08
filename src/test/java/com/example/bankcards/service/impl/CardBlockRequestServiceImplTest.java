package com.example.bankcards.service.impl;

import com.example.bankcards.dto.request.FilterCardBlockRequest;
import com.example.bankcards.dto.response.CardBlockRequestDto;
import com.example.bankcards.dto.response.CardResponse;
import com.example.bankcards.dto.response.UserResponse;
import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.CardBlockRequest;
import com.example.bankcards.entity.User;
import com.example.bankcards.entity.enums.BlockRequestStatus;
import com.example.bankcards.entity.enums.CardStatus;
import com.example.bankcards.entity.enums.Role;
import com.example.bankcards.exception.custom.InvalidBlockRequestStateException;
import com.example.bankcards.exception.custom.NotFoundException;
import com.example.bankcards.mapper.CardBlockRequestMapper;
import com.example.bankcards.repository.CardBlockRequestRepository;
import com.example.bankcards.service.CardService;
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
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CardBlockRequestServiceImplTest {

    @Mock
    private CardService cardService;

    @Mock
    private CardBlockRequestRepository cardBlockRequestRepository;

    @Mock
    private CardBlockRequestMapper cardBlockRequestMapper;

    @InjectMocks
    private CardBlockRequestServiceImpl cardBlockRequestService;

    private Card card;
    private User user;
    private CardBlockRequest cardBlockRequest;
    private CardBlockRequestDto cardBlockRequestDto;
    private CardResponse cardResponse;
    private UserResponse userResponse;
    private FilterCardBlockRequest filterCardBlockRequest;
    private Pageable pageable;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setUsername("testUser");
        user.setRole(Role.USER);

        card = new Card();
        card.setId(1L);
        card.setUser(user);
        card.setCardNumber("1234567890123456");
        card.setStatus(CardStatus.ACTIVE);
        card.setBalance(BigDecimal.valueOf(1000.50));
        card.setExpirationDate(LocalDate.of(2026, 12, 31));

        cardResponse = new CardResponse(
                1L,
                "****-****-****-3456",
                LocalDate.of(2026, 12, 31),
                CardStatus.ACTIVE,
                BigDecimal.valueOf(1000.50)
        );

        userResponse = new UserResponse(1L, "testUser", Role.USER);

        cardBlockRequest = new CardBlockRequest();
        cardBlockRequest.setId(1L);
        cardBlockRequest.setCard(card);
        cardBlockRequest.setUser(user);
        cardBlockRequest.setStatus(BlockRequestStatus.PENDING);
        cardBlockRequest.setCreatedAt(LocalDateTime.of(2025, 9, 7, 10, 59));

        cardBlockRequestDto = new CardBlockRequestDto(
                1L,
                cardResponse,
                userResponse,
                BlockRequestStatus.PENDING,
                LocalDateTime.of(2025, 9, 7, 10, 59)
        );

        filterCardBlockRequest = mock(FilterCardBlockRequest.class);
        pageable = mock(Pageable.class);
    }

    @Test
    void getAllCardBlockRequest_Successful_ReturnsPageOfCardBlockRequestDto() {
        Page<CardBlockRequest> requestPage = new PageImpl<>(List.of(cardBlockRequest));
        when(cardBlockRequestRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(requestPage);
        when(cardBlockRequestMapper.toDto(cardBlockRequest)).thenReturn(cardBlockRequestDto);

        Page<CardBlockRequestDto> result = cardBlockRequestService.getAllCardBlockRequest(pageable, filterCardBlockRequest);

        assertNotNull(result);
        assertEquals(1, result.getContent().size());

        assertEquals(cardBlockRequestDto, result.getContent().get(0));
        assertEquals(cardResponse, result.getContent().get(0).card());
        assertEquals(userResponse, result.getContent().get(0).user());

        verify(cardBlockRequestRepository).findAll(any(Specification.class), eq(pageable));
        verify(cardBlockRequestMapper).toDto(cardBlockRequest);
    }

    @Test
    void approveCardBlock_Successful_ApprovesRequestAndBlocksCard() {
        when(cardBlockRequestRepository.findById(1L)).thenReturn(Optional.of(cardBlockRequest));
        when(cardBlockRequestRepository.save(cardBlockRequest)).thenReturn(cardBlockRequest);

        cardBlockRequestService.approveCardBlock(1L);

        assertEquals(BlockRequestStatus.APPROVED, cardBlockRequest.getStatus());
        assertEquals(CardStatus.BLOCKED, card.getStatus());
        verify(cardBlockRequestRepository).findById(1L);
        verify(cardBlockRequestRepository).save(cardBlockRequest);
        verify(cardService).saveCard(card);
    }

    @Test
    void approveCardBlock_NonPendingRequest_ThrowsInvalidBlockRequestStateException() {
        cardBlockRequest.setStatus(BlockRequestStatus.APPROVED);
        when(cardBlockRequestRepository.findById(1L)).thenReturn(Optional.of(cardBlockRequest));

        assertThrows(InvalidBlockRequestStateException.class, () -> cardBlockRequestService.approveCardBlock(1L));
        verify(cardBlockRequestRepository).findById(1L);
        verifyNoMoreInteractions(cardBlockRequestRepository, cardService);
    }

    @Test
    void rejectCardBlock_Successful_RejectsRequest() {
        when(cardBlockRequestRepository.findById(1L)).thenReturn(Optional.of(cardBlockRequest));
        when(cardBlockRequestRepository.save(cardBlockRequest)).thenReturn(cardBlockRequest);

        cardBlockRequestService.rejectCardBlock(1L);

        assertEquals(BlockRequestStatus.REJECTED, cardBlockRequest.getStatus());
        verify(cardBlockRequestRepository).findById(1L);
        verify(cardBlockRequestRepository).save(cardBlockRequest);
        verifyNoInteractions(cardService);
    }

    @Test
    void rejectCardBlock_NonPendingRequest_ThrowsInvalidBlockRequestStateException() {
        cardBlockRequest.setStatus(BlockRequestStatus.REJECTED);
        when(cardBlockRequestRepository.findById(1L)).thenReturn(Optional.of(cardBlockRequest));

        assertThrows(InvalidBlockRequestStateException.class, () -> cardBlockRequestService.rejectCardBlock(1L));
        verify(cardBlockRequestRepository).findById(1L);
        verifyNoMoreInteractions(cardBlockRequestRepository, cardService);
    }

    @Test
    void createRequestCardBlock_Successful_CreatesRequest() {
        when(cardService.getCardEntityByIdAndUserId(1L, 1L)).thenReturn(card);
        when(cardBlockRequestMapper.toEntity(user, card, BlockRequestStatus.PENDING)).thenReturn(cardBlockRequest);
        when(cardBlockRequestRepository.save(cardBlockRequest)).thenReturn(cardBlockRequest);

        cardBlockRequestService.createRequestCardBlock(1L, 1L);

        verify(cardService).getCardEntityByIdAndUserId(1L, 1L);
        verify(cardBlockRequestMapper).toEntity(user, card, BlockRequestStatus.PENDING);
        verify(cardBlockRequestRepository).save(cardBlockRequest);
    }

    @Test
    void getRequestById_Successful_ReturnsCardBlockRequestDto() {
        when(cardBlockRequestRepository.findById(1L)).thenReturn(Optional.of(cardBlockRequest));
        when(cardBlockRequestMapper.toDto(cardBlockRequest)).thenReturn(cardBlockRequestDto);

        CardBlockRequestDto result = cardBlockRequestService.getRequestById(1L);

        assertNotNull(result);
        assertEquals(cardBlockRequestDto, result);

        verify(cardBlockRequestRepository).findById(1L);
        verify(cardBlockRequestMapper).toDto(cardBlockRequest);
    }

    @Test
    void getRequestById_RequestNotFound_ThrowsNotFoundException() {
        when(cardBlockRequestRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> cardBlockRequestService.getRequestById(1L));
        verify(cardBlockRequestRepository).findById(1L);
        verifyNoInteractions(cardBlockRequestMapper);
    }

    @Test
    void getRequestEntityById_Successful_ReturnsCardBlockRequest() {
        when(cardBlockRequestRepository.findById(1L)).thenReturn(Optional.of(cardBlockRequest));

        CardBlockRequest result = cardBlockRequestService.getRequestEntityById(1L);

        assertNotNull(result);

        assertEquals(cardBlockRequest, result);
        verify(cardBlockRequestRepository).findById(1L);
    }

    @Test
    void getRequestEntityById_RequestNotFound_ThrowsNotFoundException() {
        when(cardBlockRequestRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> cardBlockRequestService.getRequestEntityById(1L));
        verify(cardBlockRequestRepository).findById(1L);
    }
}