package com.carteiraacoesbackend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record UsuarioUpdateRequest(
    @NotBlank @Size(max = 100) String login, @NotNull Boolean ativo) {}
