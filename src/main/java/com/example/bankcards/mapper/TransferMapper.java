package com.example.bankcards.mapper;

import com.example.bankcards.dto.response.TransferResponse;
import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.Transfer;
import com.example.bankcards.entity.enums.TransferStatus;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.math.BigDecimal;

@Mapper(componentModel = "spring")
public interface TransferMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "fromCard", source = "fromCard")
    @Mapping(target = "toCard", source = "toCard")
    @Mapping(target = "status", source = "status")
    @Mapping(target = "amount", source = "amount")
    Transfer toTransfer(Card fromCard, Card toCard, TransferStatus status, BigDecimal amount);

    @Mapping(target = "fromCardId", source = "fromCard.id")
    @Mapping(target = "fromCardNumber", expression = "java(com.example.bankcards.util.CardMaskUtil.maskCardNumber(transfer.getFromCard().getCardNumber()))")
    @Mapping(target = "toCardId", source = "toCard.id")
    @Mapping(target = "toCardNumber", expression = "java(com.example.bankcards.util.CardMaskUtil.maskCardNumber(transfer.getToCard().getCardNumber()))")
    @Mapping(target = "status", source = "status")
    @Mapping(target = "amount", source = "amount")
    @Mapping(target = "createdAt", source = "createdAt")
    TransferResponse toTransferResponse(Transfer transfer);
}
