package com.example.bankcards.controller;

import com.example.bankcards.dto.ApiResponseDto;
import com.example.bankcards.dto.response.CardBlockRequestDto;
import com.example.bankcards.dto.request.FilterCardBlockRequest;
import com.example.bankcards.service.CardBlockRequestService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.PagedModel;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/cards/block-requests")
@Tag(name = "Запросы на блокировку карт", description = "API для управления запросами на блокировку карт")
public class CardBlockRequestController {
    private final CardBlockRequestService cardBlockRequestService;

    public CardBlockRequestController(CardBlockRequestService cardBlockRequestService) {
        this.cardBlockRequestService = cardBlockRequestService;
    }


    @Operation(
            summary = "Получение списка запросов на блокировку карт (для администратора)",
            description = "Возвращает постраничный список запросов на блокировку карт с возможностью фильтрации. Доступно только администраторам."
    )
    @ApiResponse(
            responseCode = "200",
            description = "Список запросов успешно получен"
    )
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponseDto<PagedModel<CardBlockRequestDto>>> getAllCardBlockRequest(@PageableDefault(size = 20, sort = "createdAt") Pageable pageable,
                                                                                                  FilterCardBlockRequest filter) {
        Page<CardBlockRequestDto> page = cardBlockRequestService.getAllCardBlockRequest(pageable, filter);
        return ResponseEntity.ok(ApiResponseDto.success(new PagedModel<>(page)));
    }


    @Operation(
            summary = "Одобрение запроса на блокировку карты (для администратора)",
            description = "Одобряет запрос на блокировку карты по его ID. Доступно только администраторам."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Запрос успешно одобрен (с возвратом данных)"

            ),
            @ApiResponse(
                    responseCode = "204",
                    description = "Запрос успешно одобрен (без возврата данных)",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Запрос на блокировку не найден",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponseDto.class)
                    )
            )
    })
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



    @Operation(
            summary = "Отклонение запроса на блокировку карты (для администратора)",
            description = "Отклоняет запрос на блокировку карты по его ID. Доступно только администраторам."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Запрос успешно отклонён (с возвратом данных)"
            ),
            @ApiResponse(
                    responseCode = "204",
                    description = "Запрос успешно отклонён (без возврата данных)",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Запрос на блокировку не найден",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponseDto.class)
                    )
            )
    })
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
