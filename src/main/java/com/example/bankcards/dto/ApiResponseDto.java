package com.example.bankcards.dto;

import org.springframework.http.HttpStatus;

public record ApiResponseDto<T>(
        String message,
        T data,
        String error,
        HttpStatus status
) {

    public static <T> ApiResponseDto<T> success(T data) {
        return new ApiResponseDto<>("success", data, null, HttpStatus.OK);
    }

    public static <T> ApiResponseDto<T> success(T data, HttpStatus status) {
        return new ApiResponseDto<>("success", data, null, status);
    }

    public static <T> ApiResponseDto<T> error(String errorMessage, HttpStatus status) {
        return new ApiResponseDto<>("error", null, errorMessage, status);
    }
}