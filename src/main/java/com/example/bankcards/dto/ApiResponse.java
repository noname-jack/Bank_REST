package com.example.bankcards.dto;

import org.springframework.http.HttpStatus;

public record ApiResponse<T>(
        String message,
        T data,
        String error,
        HttpStatus status
) {

    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>("success", data, null, HttpStatus.OK);
    }

    public static <T> ApiResponse<T> success(T data, HttpStatus status) {
        return new ApiResponse<>("success", data, null, status);
    }

    public static <T> ApiResponse<T> error(String errorMessage, HttpStatus status) {
        return new ApiResponse<>("error", null, errorMessage, status);
    }
}