package com.example.bankcards.service.impl;

import com.example.bankcards.dto.request.TransferRequest;
import com.example.bankcards.dto.response.TransferResponse;
import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.Transfer;
import com.example.bankcards.entity.enums.TransferStatus;
import com.example.bankcards.exception.custom.InsufficientFundsException;
import com.example.bankcards.exception.custom.TransferException;
import com.example.bankcards.mapper.TransferMapper;
import com.example.bankcards.repository.TransferRepository;
import com.example.bankcards.service.CardService;
import com.example.bankcards.service.TransferService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TransferServiceImpl implements TransferService {

    private final CardService cardService;

    private final TransferRepository transferRepository;

    private final TransferMapper transferMapper;

    public TransferServiceImpl(CardService cardService, TransferRepository transferRepository, TransferMapper transferMapper) {
        this.cardService = cardService;
        this.transferRepository = transferRepository;
        this.transferMapper = transferMapper;
    }

    @Override
    @Transactional
    public TransferResponse transferBetweenMyCards(TransferRequest request, Long userId) {
        if (request.fromCardId().equals(request.toCardId())) {
            throw new TransferException("Нельзя переводить на ту же самую карту");
        }

        Card fromCard= cardService.getCardEntityByIdAndUserId(request.fromCardId(), userId);
        Card toCard = cardService.getCardEntityByIdAndUserId(request.toCardId(), userId);

        cardService.validateCardStatus(fromCard);
        cardService.validateCardStatus(toCard);

        if (fromCard.getBalance().compareTo(request.amount()) < 0) {
            throw new InsufficientFundsException("Недостаточно средств на карте отправителя");
        }

        Transfer transfer = transferMapper.toTransfer(fromCard, toCard, TransferStatus.PENDING, request.amount());
        try {
            cardService.transfer(fromCard,toCard,request.amount());
            transfer.setStatus(TransferStatus.COMPLETED);

        } catch (Exception e) {
            transfer.setStatus(TransferStatus.FAILED);
            throw new TransferException("Ошибка при выполнении перевода");
        }
        transfer = transferRepository.save(transfer);

        return transferMapper.toTransferResponse(transfer);
    }

    @Override
    public Page<TransferResponse> getUserTransfers(Long userId, Pageable pageable) {
        return null;
    }
}
