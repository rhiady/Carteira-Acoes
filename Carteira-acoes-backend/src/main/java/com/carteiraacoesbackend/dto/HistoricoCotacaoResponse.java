package com.carteiraacoesbackend.dto;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

public record HistoricoCotacaoResponse(
    Long id, Long acaoId, BigDecimal cotacao, OffsetDateTime dataHora) {}
