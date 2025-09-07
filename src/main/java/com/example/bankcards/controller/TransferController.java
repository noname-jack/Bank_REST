package com.example.bankcards.controller;

import com.example.bankcards.dto.ApiResponseDto;
import com.example.bankcards.dto.request.TransferRequest;
import com.example.bankcards.dto.response.TransferResponse;
import com.example.bankcards.entity.User;
import com.example.bankcards.service.TransferService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/transfer")
@Validated
public class TransferController {

    private final TransferService transferService;

    public TransferController(TransferService transferService) {
        this.transferService = transferService;
    }

    @PostMapping("/my")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponseDto<TransferResponse>> transferBetweenMyCards(@RequestBody @Valid TransferRequest request,
                                                                                   @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(ApiResponseDto.success(transferService.transferBetweenMyCards(request, user.getId())));
    }

}
