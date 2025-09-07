package com.example.bankcards.exception;


import com.example.bankcards.dto.ApiResponseDto;
import com.example.bankcards.exception.custom.*;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;


import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<String> handleAccessDenied(AccessDeniedException ex) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body("Доступ запрещен: " + ex.getMessage());
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ApiResponseDto<Object>> handleAuthException(AuthenticationException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(ApiResponseDto.error("Ошибка аутентификации: " + ex.getMessage(), HttpStatus.UNAUTHORIZED));
    }


    @ExceptionHandler({NotFoundException.class, UsernameNotFoundException.class})
    public ResponseEntity<ApiResponseDto<Object>> handleNotFound(NotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponseDto.error(ex.getMessage(), HttpStatus.NOT_FOUND)
        );
    }

    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<ApiResponseDto<Object>> handleUserExists(UserAlreadyExistsException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponseDto.error(ex.getMessage(), HttpStatus.BAD_REQUEST));
    }

    @ExceptionHandler(InvalidJwtException.class)
    public ResponseEntity<ApiResponseDto<Object>> handleInvalidJwt(InvalidJwtException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(ApiResponseDto.error(ex.getMessage(), HttpStatus.UNAUTHORIZED));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponseDto<Object>> handleValidation(MethodArgumentNotValidException ex) {
        String errors = ex.getBindingResult().getFieldErrors().stream()
                .map(e -> e.getField() + ": " + e.getDefaultMessage())
                .collect(Collectors.joining("; "));
        return ResponseEntity.badRequest()
                .body( ApiResponseDto.error(errors, HttpStatus.BAD_REQUEST));

    }

    @ExceptionHandler(InsufficientFundsException.class)
    public ResponseEntity<ApiResponseDto<Object>> handleInsufficientFunds(InsufficientFundsException ex) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ApiResponseDto.error(ex.getMessage(), HttpStatus.BAD_REQUEST));
    }

    @ExceptionHandler(CardNotActiveException.class)
    public ResponseEntity<ApiResponseDto<Object>> handleCardNotActive(CardNotActiveException ex) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ApiResponseDto.error(ex.getMessage(), HttpStatus.BAD_REQUEST));
    }

    @ExceptionHandler(TransferException.class)
    public ResponseEntity<ApiResponseDto<Object>> handleTransferException(TransferException ex) {
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponseDto.error(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR));
    }

    @ExceptionHandler(CardNumberGenerationException.class)
    public ResponseEntity<ApiResponseDto<Void>> handleCardNumberGeneration(CardNumberGenerationException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(ApiResponseDto.error(ex.getMessage(), HttpStatus.CONFLICT));
    }

    @ExceptionHandler(InvalidBlockRequestStateException.class)
    public ResponseEntity<ApiResponseDto<Object>> handleInvalidBlockRequestState(InvalidBlockRequestStateException ex) {
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(ApiResponseDto.error(ex.getMessage(), HttpStatus.CONFLICT));
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiResponseDto<Object>> handleConstraintViolation(ConstraintViolationException ex) {
        String errors = ex.getConstraintViolations().stream()
                .map(ConstraintViolation::getMessage)
                .collect(Collectors.joining("; "));
        return ResponseEntity.badRequest()
                .body( ApiResponseDto.error(errors, HttpStatus.BAD_REQUEST));
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiResponseDto<Object>> handleJsonParseError(HttpMessageNotReadableException ex) {
        return ResponseEntity
                .badRequest()
                .body(ApiResponseDto.error("Некорректный формат JSON запроса" + ex, HttpStatus.BAD_REQUEST));
    }


}
