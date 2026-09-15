package com.carteiraacoesbackend.dto;

import com.carteiraacoesbackend.domains.enums.Mercado;
import com.carteiraacoesbackend.domains.enums.Moeda;
import com.carteiraacoesbackend.domains.enums.TipoOperacao;
import java.math.BigDecimal;
import java.time.OffsetDateTime;

public record OperacaoResponse(
    Long id,
    Long carteiraId,
    Long acaoId,
    String ticker,
    String nomeEmpresa,
    Mercado mercado,
    Moeda moeda,
    TipoOperacao tipo,
    BigDecimal quantidade,
    BigDecimal precoUnitario,
    BigDecimal valorBruto,
    BigDecimal corretagem,
    BigDecimal impostos,
    BigDecimal valorAdicional,
    BigDecimal valorLiquido,
    OffsetDateTime dataHora,
    OffsetDateTime createdAt) {}
