package com.carteiraacoesbackend.dto;

import jakarta.validation.constraints.NotBlank;

public record LoginRequest(
        @NotBlank(message = "O usuario deve ser informado")
        String login,
        @NotBlank(message = "A senha deve ser informada") String password
) { }
