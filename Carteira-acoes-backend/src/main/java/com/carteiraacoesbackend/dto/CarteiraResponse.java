package com.carteiraacoesbackend.dto;

import java.time.OffsetDateTime;

public record CarteiraResponse(
    Long id,
    String nome,
    Long usuarioId,
    Long corretoraId,
    OffsetDateTime createdAt,
    OffsetDateTime updatedAt) {}
