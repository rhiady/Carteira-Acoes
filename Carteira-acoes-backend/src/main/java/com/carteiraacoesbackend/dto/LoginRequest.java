package com.carteiraacoesbackend.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import jakarta.validation.constraints.NotBlank;

public record LoginRequest(
    @NotBlank(message = "O usuario deve ser informado") @JsonAlias("username") String login,
    @NotBlank(message = "A senha deve ser informada") String password) {}
