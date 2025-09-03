package com.example.bankcards.service.impl;

import com.example.bankcards.dto.request.AuthRequest;
import com.example.bankcards.dto.response.AuthResponse;
import com.example.bankcards.security.JwtService;
import com.example.bankcards.service.AuthService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
public class AuthServiceImpl implements AuthService {
    private final AuthenticationManager authenticationManager;

    private final JwtService jwtService;

    public AuthServiceImpl(AuthenticationManager authenticationManager, JwtService jwtService) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }

    @Override
    public AuthResponse login(AuthRequest authRequest) {

            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            authRequest.username(),
                            authRequest.password()
                    )
            );
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();

            return new AuthResponse(jwtService.generateToken(userDetails));

    }
}
