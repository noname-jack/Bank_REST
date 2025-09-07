package com.example.bankcards.controller;

import com.example.bankcards.dto.ApiResponseDto;
import com.example.bankcards.dto.request.CardRequest;
import com.example.bankcards.dto.request.FilterCardAdminRequest;
import com.example.bankcards.dto.request.FilterCardUserRequest;
import com.example.bankcards.dto.response.CardDetailedResponse;
import com.example.bankcards.dto.response.CardResponse;
import com.example.bankcards.entity.User;
import com.example.bankcards.entity.enums.CardStatus;
import com.example.bankcards.service.CardBlockRequestService;
import com.example.bankcards.service.CardService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.PagedModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/cards")
@Validated
public class CardController {


    private final CardService cardService;
    private final CardBlockRequestService cardBlockRequestService;

    public CardController(CardService cardService, CardBlockRequestService cardBlockRequestService) {
        this.cardService = cardService;
        this.cardBlockRequestService = cardBlockRequestService;
    }

    @GetMapping("/{cardId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponseDto<CardDetailedResponse>> getCardByIdForAdmin(
            @PathVariable @NotNull @Positive Long cardId) {
        CardDetailedResponse card = cardService.getCardDetailsById(cardId);
        return ResponseEntity.ok(ApiResponseDto.success(card));
    }

    @GetMapping()
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponseDto<PagedModel<CardResponse>>> getAllCardsForAdmin(
            @PageableDefault(size = 20) Pageable pageable,
            FilterCardAdminRequest filter) {
        Page<CardResponse> cards = cardService.getAllCards(pageable, filter);
        return ResponseEntity.ok(ApiResponseDto.success(new PagedModel<>(cards)));
    }

    @PostMapping()
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponseDto<CardDetailedResponse>> createCard(@RequestBody @Valid CardRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponseDto.success(cardService.createCard(request), HttpStatus.CREATED));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponseDto<Void>> deleteCard(@PathVariable Long id){
        cardService.deleteCardById(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/activate")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponseDto<CardDetailedResponse>> activateCard(@PathVariable Long id,
                                                                             @RequestParam(required = false, defaultValue = "true") boolean withResult ){
        cardService.updateStatusCardById(id, CardStatus.ACTIVE);
        if (withResult) {
            return ResponseEntity.ok(ApiResponseDto.success(cardService.getCardDetailsById(id)));
        } else {
            return ResponseEntity.noContent().build();
        }
    }

    @PatchMapping("/{id}/block")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponseDto<CardDetailedResponse>> blockCard(@PathVariable Long id,
                                                                          @RequestParam(required = false, defaultValue = "true") boolean withResult ){
        cardService.updateStatusCardById(id, CardStatus.BLOCKED);
        if (withResult) {
            return ResponseEntity.ok(ApiResponseDto.success(cardService.getCardDetailsById(id)));
        } else {
            return ResponseEntity.noContent().build();
        }
    }

    @GetMapping("/my/{cardId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponseDto<CardDetailedResponse>> getMyCardById(
            @PathVariable @NotNull @Positive Long cardId,
            @AuthenticationPrincipal User user) {
        Long userId = user.getId();
        CardDetailedResponse card = cardService.getCardDetailsByIdAndUserId(cardId, userId);
        return ResponseEntity.ok(ApiResponseDto.success(card));
    }
    @GetMapping("/my")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponseDto<PagedModel<CardResponse>>> getMyCards(
            @PageableDefault(size = 20) Pageable pageable,
            FilterCardUserRequest filter,
            @AuthenticationPrincipal User user) {
        Long userId = user.getId();
        Page<CardResponse> cards = cardService.getAllCardsByUserId(pageable, filter, userId);
        return ResponseEntity.ok(ApiResponseDto.success(new PagedModel<>(cards)));
    }

    @PostMapping("/my/{cartId}/block-request")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponseDto<Void>> createBlockRequest(@PathVariable Long cartId, @AuthenticationPrincipal User user) {
        cardBlockRequestService.createRequestCardBlock(cartId, user.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponseDto.success(null, HttpStatus.CREATED));
    }
}