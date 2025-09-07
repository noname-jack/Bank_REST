package com.example.bankcards.service;

import com.example.bankcards.dto.request.CardBlockRequestDto;
import com.example.bankcards.dto.request.FilterCardBlockRequest;
import com.example.bankcards.entity.CardBlockRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public interface CardBlockRequestService {
    Page<CardBlockRequestDto> getAllCardBlockRequest(Pageable pageable, FilterCardBlockRequest filter);
    void approveCardBlock(Long requestId);
    void rejectCardBlock(Long requestId);
    void createRequestCardBlock(Long cardId, Long userId);
    CardBlockRequestDto getRequestById(Long id);

    CardBlockRequest getRequestEntityById(Long id);
}
