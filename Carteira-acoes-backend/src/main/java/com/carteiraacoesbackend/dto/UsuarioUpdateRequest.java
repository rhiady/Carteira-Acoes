package com.carteiraacoesbackend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.NotNull;

public record UsuarioUpdateRequest(
    @NotBlank @Size(max = 100) String login, @NotNull Boolean ativo) {}
