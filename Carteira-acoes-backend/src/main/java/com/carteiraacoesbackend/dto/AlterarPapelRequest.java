package com.carteiraacoesbackend.dto;

import com.carteiraacoesbackend.domains.enums.Papel;
import jakarta.validation.constraints.NotNull;

public record AlterarPapelRequest(@NotNull Papel papel) {}
