package com.example.bankcards.controller;

import com.example.bankcards.dto.ApiResponseDto;
import com.example.bankcards.dto.request.CardBlockRequestDto;
import com.example.bankcards.dto.request.FilterCardBlockRequest;
import com.example.bankcards.service.CardBlockRequestService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.PagedModel;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/cards/block-requests")
public class CardBlockRequestController {
    private final CardBlockRequestService cardBlockRequestService;

    public CardBlockRequestController(CardBlockRequestService cardBlockRequestService) {
        this.cardBlockRequestService = cardBlockRequestService;
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponseDto<PagedModel<CardBlockRequestDto>>> getAllCardBlockRequest(@PageableDefault(size = 20, sort = "createdAt") Pageable pageable,
                                                                                                  FilterCardBlockRequest filter) {
        Page<CardBlockRequestDto> page = cardBlockRequestService.getAllCardBlockRequest(pageable, filter);
        return ResponseEntity.ok(ApiResponseDto.success(new PagedModel<>(page)));
    }

    @PostMapping("/{id}/approve")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponseDto<CardBlockRequestDto>> approve(@PathVariable Long id,
                                                                       @RequestParam(required = false, defaultValue = "true") boolean withResult) {
        cardBlockRequestService.approveCardBlock(id);
        if (withResult) {
            return ResponseEntity.ok().body(ApiResponseDto.success(cardBlockRequestService.getRequestById(id)));
        }
        else{
            return ResponseEntity.noContent().build();
        }
    }

    @PostMapping("/{id}/reject")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponseDto<CardBlockRequestDto>> reject(@PathVariable Long id,
                                                                      @RequestParam(required = false, defaultValue = "true") boolean withResult) {
        cardBlockRequestService.rejectCardBlock(id);
        if (withResult) {
            return ResponseEntity.ok().body(ApiResponseDto.success(cardBlockRequestService.getRequestById(id)));
        }
        else{
            return ResponseEntity.noContent().build();
        }
    }

}
