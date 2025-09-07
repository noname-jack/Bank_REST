package com.example.bankcards.mapper;

import com.example.bankcards.dto.request.CardBlockRequestDto;
import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.CardBlockRequest;
import com.example.bankcards.entity.User;
import com.example.bankcards.entity.enums.BlockRequestStatus;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {CardMapper.class, UserMapper.class})
public interface CardBlockRequestMapper {

    @Mapping(target = "user", source = "user")
    @Mapping(target = "card", source = "card")
    @Mapping(target = "status", source = "status")
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "id", ignore = true)
    CardBlockRequest toEntity(User user, Card card, BlockRequestStatus status);


    @Mapping(target = "user", source = "user")
    @Mapping(target = "card", source = "card")
    @Mapping(target = "status", source = "status")
    @Mapping(target = "createdAt", source = "createdAt")
    @Mapping(target = "id", source = "id")
    CardBlockRequestDto toDto(CardBlockRequest cardBlockRequest);
}
