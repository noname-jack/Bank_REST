package com.example.bankcards.service.impl;

import com.example.bankcards.dto.request.CardRequest;
import com.example.bankcards.dto.request.FilterCardAdminRequest;
import com.example.bankcards.dto.request.FilterCardUserRequest;
import com.example.bankcards.dto.response.CardDetailedResponse;
import com.example.bankcards.dto.response.CardResponse;
import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.User;
import com.example.bankcards.entity.enums.CardStatus;
import com.example.bankcards.entity.spec.CardSpecification;
import com.example.bankcards.exception.custom.CardNotActiveException;
import com.example.bankcards.exception.custom.CardNumberGenerationException;
import com.example.bankcards.exception.custom.NotFoundException;
import com.example.bankcards.mapper.CardMapper;
import com.example.bankcards.repository.CardRepository;
import com.example.bankcards.service.CardService;
import com.example.bankcards.service.UserService;
import com.example.bankcards.util.CardNumberGenerator;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;


@Service
public class CardServiceImpl implements CardService {

    private final CardRepository cardRepository;
    private final CardMapper cardMapper;
    private final UserService userService;

    private final CardNumberGenerator cardNumberGenerator;
    public CardServiceImpl(CardRepository cardRepository, CardMapper cardMapper, UserService userService, CardNumberGenerator cardNumberGenerator) {
        this.cardRepository = cardRepository;
        this.cardMapper = cardMapper;
        this.userService = userService;
        this.cardNumberGenerator = cardNumberGenerator;
    }

    @Override
    @Transactional(readOnly = true)
    public CardDetailedResponse getCardDetailsById(Long id) {
        return cardMapper.toCardDetailedResponse(getCardEntityById(id));
    }

    @Override
    @Transactional(readOnly = true)
    public CardDetailedResponse getCardDetailsByIdAndUserId(Long id, Long userId) {
        return cardMapper.toCardDetailedResponse(getCardEntityByIdAndUserId(id, userId));
    }


    @Override
    public Page<CardResponse> getAllCards(Pageable pageable, FilterCardAdminRequest filter) {
        return cardRepository.findAll(CardSpecification.filter(filter), pageable).map(cardMapper::toCardResponse);
    }

    @Override
    public Page<CardResponse> getAllCardsByUserId(Pageable pageable, FilterCardUserRequest filter, Long userId) {
        return cardRepository.findAll(CardSpecification.filterByUserId(filter,userId), pageable).map(cardMapper::toCardResponse);
    }

    @Override
    @Transactional
    public CardDetailedResponse createCard(CardRequest request) {
        User user = userService.getUserEntityById(request.userId());
        String cardNumber = generateCardNumber();
        Card card = cardMapper.toCard(user, cardNumber, request);
        cardRepository.save(card);
        return cardMapper.toCardDetailedResponse(card);
    }

    public void validateCardStatus(Card card) {
        if (card.getStatus() != CardStatus.ACTIVE) {
            throw new CardNotActiveException("Карта заблокирована или неактивна");
        }
    }

    @Override
    public void deleteCardById(Long id) {
        Card card = getCardEntityById(id);
        cardRepository.delete(card);
    }

    @Override
    public void updateStatusCardById(Long id, CardStatus status) {
        Card card = getCardEntityById(id);
        card.setStatus(status);
        cardRepository.save(card);
    }

    @Override
    @Transactional
    public void transfer(Card fromCard, Card toCard, BigDecimal amount) {
        fromCard.setBalance(fromCard.getBalance().subtract(amount));
        toCard.setBalance(toCard.getBalance().add(amount));
        cardRepository.save(fromCard);
        cardRepository.save(toCard);
    }

    @Override
    public void saveCard(Card card) {
        cardRepository.save(card);
    }


    @Override
    public Card getCardEntityById(Long id) {
        return cardRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Карта не найдена"));
    }
    @Override
    public Card getCardEntityByIdAndUserId(Long id, Long userId) {
         return cardRepository.findByIdAndUserId(id,userId)
                .orElseThrow(() -> new NotFoundException("Карта не найдена"));
    }

    private String generateCardNumber() {
        int maxAttempts = 1000;
        for (int i = 0; i < maxAttempts; i++) {
            String cardNumber = cardNumberGenerator.generate();
            if (!cardRepository.existsByCardNumber(cardNumber)) {
                return cardNumber;
            }
        }
        throw new CardNumberGenerationException("Не удалось сгенерировать уникальный номер");
    }
}
