package com.carteiraacoesbackend.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record LoginRequest(
        @NotBlank(message = "O usuario deve ser informado") String username,
        @NotBlank(message = "A senha deve ser informada") String password
) { }
