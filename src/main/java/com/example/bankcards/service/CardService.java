package com.example.bankcards.service;

import com.example.bankcards.dto.request.CardRequest;
import com.example.bankcards.dto.request.FilterCardAdminRequest;
import com.example.bankcards.dto.request.FilterCardUserRequest;
import com.example.bankcards.dto.response.CardDetailedResponse;
import com.example.bankcards.dto.response.CardResponse;
import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.enums.CardStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public interface CardService {


    Card getCardEntityById(Long id);

    Card getCardEntityByIdAndUserId(Long id, Long userId);
    CardDetailedResponse getCardDetailsById(Long id);

    CardDetailedResponse getCardDetailsByIdAndUserId(Long id, Long ownerId);

    Page<CardResponse> getAllCards(Pageable pageable, FilterCardAdminRequest filter);

    Page<CardResponse> getAllCardsByUserId(Pageable pageable, FilterCardUserRequest filter, Long userId);

    CardDetailedResponse createCard(CardRequest request);

    void validateCardStatus(Card card);

    void deleteCardById(Long id);

    void updateStatusCardById(Long id, CardStatus status);
    void transfer(Card fromCard, Card toCard, BigDecimal amount);

    void saveCard(Card card);

}
