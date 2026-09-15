package com.carteiraacoesbackend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record AtualizarCarteiraRequest(@NotBlank @Size(max = 255) String nome) {}
