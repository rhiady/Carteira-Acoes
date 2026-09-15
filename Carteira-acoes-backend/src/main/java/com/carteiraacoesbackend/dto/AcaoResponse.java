package com.carteiraacoesbackend.dto;

import com.carteiraacoesbackend.domains.enums.Mercado;
import com.carteiraacoesbackend.domains.enums.Moeda;
import java.math.BigDecimal;
import java.time.OffsetDateTime;

public record AcaoResponse(
    Long id,
    String ticker,
    String nomeEmpresa,
    Mercado mercado,
    Moeda moeda,
    BigDecimal cotacaoAtual,
    OffsetDateTime dataHoraCotacao,
    BigDecimal cotacaoEmBrl,
    BigDecimal taxaUsdBrl,
    OffsetDateTime dataHoraTaxaUsdBrl,
    OffsetDateTime createdAt,
    OffsetDateTime updatedAt) {}
