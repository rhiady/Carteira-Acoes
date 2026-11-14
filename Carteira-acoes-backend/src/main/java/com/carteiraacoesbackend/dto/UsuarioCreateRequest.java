package com.carteiraacoesbackend.dto;

import com.carteiraacoesbackend.domains.enums.Papel;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record UsuarioCreateRequest(
    @NotBlank @Size(max = 100) String login,
    @NotBlank @Size(min = 8, max = 72) String password,
    @NotNull Papel papel) {}
