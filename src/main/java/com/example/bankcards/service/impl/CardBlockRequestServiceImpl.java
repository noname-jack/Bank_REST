package com.example.bankcards.service.impl;

import com.example.bankcards.dto.request.CardBlockRequestDto;
import com.example.bankcards.dto.request.FilterCardBlockRequest;
import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.CardBlockRequest;
import com.example.bankcards.entity.User;
import com.example.bankcards.entity.enums.BlockRequestStatus;
import com.example.bankcards.entity.enums.CardStatus;
import com.example.bankcards.entity.spec.CardBlockSpecification;
import com.example.bankcards.exception.custom.InvalidBlockRequestStateException;
import com.example.bankcards.exception.custom.NotFoundException;
import com.example.bankcards.mapper.CardBlockRequestMapper;
import com.example.bankcards.repository.CardBlockRequestRepository;
import com.example.bankcards.service.CardBlockRequestService;
import com.example.bankcards.service.CardService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
public class CardBlockRequestServiceImpl implements CardBlockRequestService {
    private final CardService cardService;
    private final CardBlockRequestRepository cardBlockRequestRepository;
    private final CardBlockRequestMapper cardBlockRequestMapper;
    public CardBlockRequestServiceImpl(CardService cardService, CardBlockRequestRepository cardBlockRequestRepository, CardBlockRequestMapper cardBlockRequestMapper) {
        this.cardService = cardService;
        this.cardBlockRequestRepository = cardBlockRequestRepository;
        this.cardBlockRequestMapper = cardBlockRequestMapper;
    }

    @Override
    public Page<CardBlockRequestDto> getAllCardBlockRequest(Pageable pageable, FilterCardBlockRequest filter) {
        return cardBlockRequestRepository.findAll(CardBlockSpecification.filter(filter),pageable).map(cardBlockRequestMapper::toDto);
    }

    @Override
    @Transactional
    public void approveCardBlock(Long requestId) {
        CardBlockRequest request = getRequestEntityById(requestId);
        if (request.getStatus() == BlockRequestStatus.PENDING) {
            Card card = request.getCard();
            card.setStatus(CardStatus.BLOCKED);
            request.setStatus(BlockRequestStatus.APPROVED);
            cardBlockRequestRepository.save(request);
            cardService.saveCard(card);
        }
        else{
            throw  new InvalidBlockRequestStateException("Запрос на блокировку карты уже был рассмотрен");
        }
    }

    @Override
    public void rejectCardBlock(Long requestId) {
        CardBlockRequest request = getRequestEntityById(requestId);
        if (request.getStatus() == BlockRequestStatus.PENDING) {
            request.setStatus(BlockRequestStatus.REJECTED);
            cardBlockRequestRepository.save(request);
        }
        else{
            throw  new InvalidBlockRequestStateException("Запрос на блокировку карты уже был рассмотрен");
        }
    }

    @Override
    public void createRequestCardBlock(Long cardId, Long userId) {
        Card card = cardService.getCardEntityByIdAndUserId(cardId, userId);
        User user = card.getUser();

        CardBlockRequest cardBlockRequest = cardBlockRequestMapper.toEntity(user, card, BlockRequestStatus.PENDING);

        cardBlockRequestRepository.save(cardBlockRequest);
    }

    @Override
    public CardBlockRequestDto getRequestById(Long id) {
        return cardBlockRequestMapper.toDto(getRequestEntityById(id));
    }

    @Override
    public CardBlockRequest getRequestEntityById(Long id) {
        return cardBlockRequestRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Запрос на блокировку карты не найден"));
    }
}
