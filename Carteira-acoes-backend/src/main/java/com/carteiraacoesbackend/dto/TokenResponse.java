package com.carteiraacoesbackend.dto;

public record TokenResponse(
        String token,
        String tipo,
        long expiresIn
) { }
