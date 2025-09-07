package com.example.bankcards.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.http.HttpStatus;

@Schema(description = "Общий формат ответа API")
public record ApiResponseDto<T>(
        @Schema(description = "Сообщение о результате запроса", example = "success")
        String message,

        @Schema(description = "Данные, возвращаемые в ответе", nullable = true)
        T data,

        @Schema(description = "Сообщение об ошибке, если запрос неуспешен", example = "Invalid request", nullable = true)
        String error,

        @Schema(description = "HTTP-статус ответа", example = "OK")
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