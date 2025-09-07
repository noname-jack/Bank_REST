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
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Карты", description = "API для управления банковскими картами")
public class CardController {


    private final CardService cardService;
    private final CardBlockRequestService cardBlockRequestService;

    public CardController(CardService cardService, CardBlockRequestService cardBlockRequestService) {
        this.cardService = cardService;
        this.cardBlockRequestService = cardBlockRequestService;
    }


    @Operation(
            summary = "Получение детальной информации о карте по ID (для администратора)",
            description = "Получение детальной информации о карте по её идентификатору. Доступно только администраторам."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Карта успешно найдена"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Карта не найдена",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponseDto.class)
                    )
            )
    })
    @GetMapping("/{cardId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponseDto<CardDetailedResponse>> getCardByIdForAdmin(
            @PathVariable @NotNull @Positive Long cardId) {
        CardDetailedResponse card = cardService.getCardDetailsById(cardId);
        return ResponseEntity.ok(ApiResponseDto.success(card));
    }


    @Operation(
            summary = "Получение списка всех карт (для администратора)",
            description = "Возвращает постраничный список всех карт с возможностью фильтрации. Доступно только администраторам."
    )
    @ApiResponse(
            responseCode = "200",
            description = "Список карт успешно получен"
    )

    @GetMapping()
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponseDto<PagedModel<CardResponse>>> getAllCardsForAdmin(
            @PageableDefault(size = 20) Pageable pageable,
            FilterCardAdminRequest filter) {
        Page<CardResponse> cards = cardService.getAllCards(pageable, filter);
        return ResponseEntity.ok(ApiResponseDto.success(new PagedModel<>(cards)));
    }


    @Operation(
            summary = "Создание новой карты (для администратора)",
            description = "Создаёт новую карту на основе переданных данных. Доступно только администраторам."
    )

    @ApiResponse(
            responseCode = "201",
            description = "Карта успешно создана"
    )

    @PostMapping()
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponseDto<CardDetailedResponse>> createCard(@RequestBody @Valid CardRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponseDto.success(cardService.createCard(request), HttpStatus.CREATED));
    }


    @Operation(
            summary = "Удаление карты по ID (для администратора)",
            description = "Удаляет карту по её ID. Доступно только администраторам."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "204",
                    description = "Карта успешно удалена"

            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Карта не найдена",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponseDto.class)
                    )
            )
    })
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteCard(@PathVariable Long id) {
        cardService.deleteCardById(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "Активация карты по ID (для администратора)",
            description = "Активирует карту, устанавливая статус ACTIVE. Доступно только администраторам."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Карта успешно активирована (с возвратом данных)"
            ),
            @ApiResponse(
                    responseCode = "204",
                    description = "Карта успешно активирована (без возврата данных)",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Карта не найдена",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponseDto.class)
                    )
            )
    })
    @PatchMapping("/{id}/activate")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponseDto<CardDetailedResponse>> activateCard(@PathVariable Long id,
                                                                             @RequestParam(required = false, defaultValue = "true") boolean withResult) {
        cardService.updateStatusCardById(id, CardStatus.ACTIVE);
        if (withResult) {
            return ResponseEntity.ok(ApiResponseDto.success(cardService.getCardDetailsById(id)));
        } else {
            return ResponseEntity.noContent().build();
        }
    }

    @Operation(
            summary = "Блокировка карты по ID (для администратора)",
            description = "Блокирует карту, устанавливая статус BLOCKED. Доступно только администраторам."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Карта успешно заблокирована (с возвратом данных)"
            ),
            @ApiResponse(
                    responseCode = "204",
                    description = "Карта успешно заблокирована (без возврата данных)",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Карта не найдена",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponseDto.class)
                    )
            )
    })
    @PatchMapping("/{id}/block")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponseDto<CardDetailedResponse>> blockCard(@PathVariable Long id,
                                                                          @RequestParam(required = false, defaultValue = "true") boolean withResult) {
        cardService.updateStatusCardById(id, CardStatus.BLOCKED);
        if (withResult) {
            return ResponseEntity.ok(ApiResponseDto.success(cardService.getCardDetailsById(id)));
        } else {
            return ResponseEntity.noContent().build();
        }
    }

    @Operation(
            summary = "Получение детальной информации о своей карте по ID",
            description = "Возвращает подробные данные о карте пользователя по её ID. Доступно только аутентифицированным пользователям."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Карта успешно найдена"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Карта не найдена или не принадлежит пользователю",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponseDto.class)
                    )
            )
    })
    @GetMapping("/my/{cardId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponseDto<CardDetailedResponse>> getMyCardById(
            @PathVariable @NotNull @Positive Long cardId,
            @AuthenticationPrincipal User user) {
        Long userId = user.getId();
        CardDetailedResponse card = cardService.getCardDetailsByIdAndUserId(cardId, userId);
        return ResponseEntity.ok(ApiResponseDto.success(card));
    }

    @Operation(
            summary = "Получение списка своих карт (для пользователя)",
            description = "Возвращает постраничный список карт, принадлежащих пользователю, с возможностью фильтрации. Доступно только аутентифицированным пользователям."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Список карт успешно получен"
            )
    })
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

    @Operation(
            summary = "Создание запроса на блокировку своей карты (для пользователя)",
            description = "Создаёт запрос на блокировку карты, принадлежащей пользователю. Доступно только аутентифицированным пользователям."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Запрос на блокировку успешно создан",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponseDto.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Карта не найдена или не принадлежит пользователю",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponseDto.class)
                    )
            )
    })
    @PostMapping("/my/{cartId}/block-request")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponseDto<Void>> createBlockRequest(@PathVariable Long cartId, @AuthenticationPrincipal User user) {
        cardBlockRequestService.createRequestCardBlock(cartId, user.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponseDto.success(null, HttpStatus.CREATED));
    }
}