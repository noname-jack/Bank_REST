package com.example.bankcards.service.impl;

import com.example.bankcards.dto.request.TransferRequest;
import com.example.bankcards.dto.response.TransferResponse;
import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.Transfer;
import com.example.bankcards.entity.enums.CardStatus;
import com.example.bankcards.entity.enums.TransferStatus;
import com.example.bankcards.exception.custom.CardNotActiveException;
import com.example.bankcards.exception.custom.InsufficientFundsException;
import com.example.bankcards.exception.custom.TransferException;
import com.example.bankcards.mapper.TransferMapper;
import com.example.bankcards.repository.TransferRepository;
import com.example.bankcards.service.CardService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransferServiceImplTest {

    @Mock
    private CardService cardService;

    @Mock
    private TransferRepository transferRepository;

    @Mock
    private TransferMapper transferMapper;

    @InjectMocks
    private TransferServiceImpl transferService;

    private Card fromCard;
    private Card toCard;
    private TransferRequest transferRequest;
    private Transfer transfer;
    private TransferResponse transferResponse;
    private Long userId;

    @BeforeEach
    void setUp() {
        userId = 1L;

        fromCard = new Card();
        fromCard.setId(1L);
        fromCard.setCardNumber("1234567890123456");
        fromCard.setBalance(BigDecimal.valueOf(1000.50));
        fromCard.setStatus(CardStatus.ACTIVE);

        toCard = new Card();
        toCard.setId(2L);
        toCard.setCardNumber("9876543210987654");
        toCard.setBalance(BigDecimal.valueOf(500.00));
        toCard.setStatus(CardStatus.ACTIVE);

        transferRequest = new TransferRequest(1L, 2L, BigDecimal.valueOf(200.50));

        transfer = new Transfer();
        transfer.setId(1L);
        transfer.setFromCard(fromCard);
        transfer.setToCard(toCard);
        transfer.setAmount(BigDecimal.valueOf(200.50));
        transfer.setStatus(TransferStatus.COMPLETED);
        transfer.setCreatedAt(LocalDateTime.of(2025, 9, 7, 10, 59));

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

    @Test
    void transferBetweenMyCards_Successful_ReturnsTransferResponse() {
        when(cardService.getCardEntityByIdAndUserId(1L, userId)).thenReturn(fromCard);
        when(cardService.getCardEntityByIdAndUserId(2L, userId)).thenReturn(toCard);
        when(transferMapper.toTransfer(fromCard, toCard, TransferStatus.PENDING, transferRequest.amount())).thenReturn(transfer);
        when(transferRepository.save(transfer)).thenReturn(transfer);
        when(transferMapper.toTransferResponse(transfer)).thenReturn(transferResponse);

        TransferResponse result = transferService.transferBetweenMyCards(transferRequest, userId);

        assertNotNull(result);
        assertEquals(transferResponse, result);

        verify(cardService).getCardEntityByIdAndUserId(1L, userId);
        verify(cardService).getCardEntityByIdAndUserId(2L, userId);
        verify(cardService).validateCardStatus(fromCard);
        verify(cardService).validateCardStatus(toCard);
        verify(cardService).transfer(fromCard, toCard, transferRequest.amount());
        verify(transferRepository).save(transfer);
        verify(transferMapper).toTransferResponse(transfer);
    }

    @Test
    void transferBetweenMyCards_SameCard_ThrowsTransferException() {
        TransferRequest invalidRequest = new TransferRequest(1L, 1L, BigDecimal.valueOf(200.50));

        TransferException exception = assertThrows(TransferException.class, () -> transferService.transferBetweenMyCards(invalidRequest, userId));

        assertEquals("Нельзя переводить на ту же самую карту", exception.getMessage());
        verifyNoInteractions(cardService, transferRepository, transferMapper);
    }

    @Test
    void transferBetweenMyCards_FromCardInactive_ThrowsCardNotActiveException() {
        fromCard.setStatus(CardStatus.BLOCKED);
        when(cardService.getCardEntityByIdAndUserId(1L, userId)).thenReturn(fromCard);
        when(cardService.getCardEntityByIdAndUserId(2L, userId)).thenReturn(toCard);
        doThrow(new CardNotActiveException("Карта заблокирована или неактивна")).when(cardService).validateCardStatus(fromCard);

        assertThrows(CardNotActiveException.class, () -> transferService.transferBetweenMyCards(transferRequest, userId));
        verify(cardService).getCardEntityByIdAndUserId(1L, userId);
        verify(cardService).getCardEntityByIdAndUserId(2L, userId);
        verify(cardService).validateCardStatus(fromCard);
        verifyNoMoreInteractions(cardService, transferRepository, transferMapper);
    }

    @Test
    void transferBetweenMyCards_InsufficientFunds_ThrowsInsufficientFundsException() {
        TransferRequest highAmountRequest = new TransferRequest(1L, 2L, BigDecimal.valueOf(2000.00));

        when(cardService.getCardEntityByIdAndUserId(1L, userId)).thenReturn(fromCard);
        when(cardService.getCardEntityByIdAndUserId(2L, userId)).thenReturn(toCard);

        assertThrows(InsufficientFundsException.class, () -> transferService.transferBetweenMyCards(highAmountRequest, userId));
        verify(cardService).getCardEntityByIdAndUserId(1L, userId);
        verify(cardService).getCardEntityByIdAndUserId(2L, userId);
        verify(cardService).validateCardStatus(fromCard);
        verify(cardService).validateCardStatus(toCard);
        verifyNoInteractions(transferRepository, transferMapper);
    }
}