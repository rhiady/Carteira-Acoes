package com.carteiraacoesbackend.dto;

import com.carteiraacoesbackend.domains.enums.Mercado;
import com.carteiraacoesbackend.domains.enums.Moeda;
import java.math.BigDecimal;
import java.time.OffsetDateTime;

public record PosicaoAcaoResponse(
    Long carteiraId,
    Long acaoId,
    String ticker,
    String nomeEmpresa,
    Mercado mercado,
    Moeda moeda,
    BigDecimal quantidade,
    BigDecimal cotacaoAtual,
    OffsetDateTime dataHoraCotacao,
    BigDecimal valorAtual,
    BigDecimal precoMedio,
    BigDecimal cotacaoEmBrl,
    BigDecimal valorAtualEmBrl,
    BigDecimal taxaUsdBrl,
    OffsetDateTime dataHoraTaxaUsdBrl,
    BigDecimal precoMedioVenda,
    BigDecimal lucroPrejuizoRealizado,
    BigDecimal lucroPrejuizoNaoRealizado,
    BigDecimal lucroPrejuizo,
    BigDecimal valorInvestidoAtual,
    BigDecimal valorTotalComprado,
    BigDecimal valorTotalVendido) {}
