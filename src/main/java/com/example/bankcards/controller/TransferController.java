package com.example.bankcards.controller;

import com.example.bankcards.dto.ApiResponseDto;
import com.example.bankcards.dto.request.TransferRequest;
import com.example.bankcards.dto.response.TransferResponse;
import com.example.bankcards.entity.User;
import com.example.bankcards.service.TransferService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/transfer")
@Validated
@Tag(name = "Переводы", description = "API для управления переводами между картами")
public class TransferController {

    private final TransferService transferService;

    public TransferController(TransferService transferService) {
        this.transferService = transferService;
    }

    @PostMapping("/my")
    @PreAuthorize("isAuthenticated()")
    @Operation(
            summary = "Перевод между своими картами (для пользователя)",
            description = "Выполняет перевод средств между картами, принадлежащими аутентифицированному пользователю."
    )
    @ApiResponse(
            responseCode = "200",
            description = "Перевод успешно выполнен"
    )

    public ResponseEntity<ApiResponseDto<TransferResponse>> transferBetweenMyCards(
            @RequestBody @Valid TransferRequest request,
            @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(ApiResponseDto.success(transferService.transferBetweenMyCards(request, user.getId())));
    }
}