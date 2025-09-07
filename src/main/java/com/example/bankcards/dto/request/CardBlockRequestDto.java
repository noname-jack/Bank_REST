package com.example.bankcards.dto.request;


import com.example.bankcards.dto.response.CardResponse;
import com.example.bankcards.dto.response.UserResponse;
import com.example.bankcards.entity.enums.BlockRequestStatus;

import java.time.LocalDateTime;

public record CardBlockRequestDto(Long id,
                                  CardResponse card,
                                  UserResponse user,
                                  BlockRequestStatus status,
                                  LocalDateTime createdAt) {

}
