package com.example.bankcards.service;

import com.example.bankcards.dto.request.AuthRequest;
import com.example.bankcards.dto.response.AuthResponse;


public interface AuthService {
    AuthResponse login(AuthRequest authRequest);
}
