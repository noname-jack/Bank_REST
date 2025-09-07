package com.example.bankcards.mapper;

import com.example.bankcards.dto.request.CardRequest;
import com.example.bankcards.dto.response.CardDetailedResponse;
import com.example.bankcards.dto.response.CardResponse;
import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring",  uses = {TransferMapper.class})
public interface CardMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", source = "user")
    @Mapping(target = "cardNumber", source = "cardNumber")
    @Mapping(target = "expirationDate", source = "request.expirationDate")
    @Mapping(target = "balance", source = "request.balance")
    @Mapping(target = "status", expression = "java(CardStatus.ACTIVE)")
    @Mapping(target = "transfersTo", ignore = true)
    @Mapping(target = "transfersFrom", ignore = true)
    Card toCard(User user, String cardNumber, CardRequest request);

    @Mapping(target = "cardNumber", expression = "java(com.example.bankcards.util.CardMaskUtil.maskCardNumber(card.getCardNumber()))")
    CardResponse toCardResponse(Card card);

    @Mapping(target = "cardNumber", expression = "java(com.example.bankcards.util.CardMaskUtil.maskCardNumber(card.getCardNumber()))")
    @Mapping(target = "ownerUsername", source = "user.username")
    @Mapping(target = "ownerId", source = "user.id")
    CardDetailedResponse toCardDetailedResponse(Card card);

}
