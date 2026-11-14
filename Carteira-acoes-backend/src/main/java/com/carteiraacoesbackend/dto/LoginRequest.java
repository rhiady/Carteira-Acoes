package com.carteiraacoesbackend.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Email;

public record LoginRequest(
        @NotBlank(message = "O usuario deve ser informado")
        @Email(message = "O usuario deve ser um email valido") @JsonAlias("email") String username,
        @NotBlank(message = "A senha deve ser informada") @JsonAlias("senha") String password
) { }
