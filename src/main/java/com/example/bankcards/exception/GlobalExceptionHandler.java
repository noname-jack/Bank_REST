package com.example.bankcards.exception;


import com.example.bankcards.dto.ApiResponse;
import com.example.bankcards.exception.custom.InvalidJwtException;
import com.example.bankcards.exception.custom.NotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ApiResponse<Object>> handleNotFound(NotFoundException ex) {
        return new ResponseEntity<>(
                ApiResponse.error(ex.getMessage(), HttpStatus.NOT_FOUND),
                HttpStatus.NOT_FOUND
        );
    }
    @ExceptionHandler(InvalidJwtException.class)
    public ResponseEntity<ApiResponse<Object>> handleInvalidJwt(InvalidJwtException ex) {
        return new ResponseEntity<>(
                ApiResponse.error(ex.getMessage(), HttpStatus.UNAUTHORIZED),
                HttpStatus.UNAUTHORIZED
        );
    }
}
