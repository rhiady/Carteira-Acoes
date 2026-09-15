package com.carteiraacoesbackend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CarteiraRequest(
    @NotBlank String nome, @NotNull Long usuarioId, @NotNull Long corretoraId) {}
