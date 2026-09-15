package com.carteiraacoesbackend.dto;

import java.time.OffsetDateTime;

public record AtualizacaoCotacoesResponse(
    int total, int atualizadas, int falhas, OffsetDateTime dataHora) {}
