package com.example.bankcards.service;

import com.example.bankcards.dto.request.TransferRequest;
import com.example.bankcards.dto.response.TransferResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public interface TransferService {

    TransferResponse transferBetweenMyCards(TransferRequest request, Long userId);

    Page<TransferResponse> getUserTransfers(Long userId, Pageable pageable);
}
