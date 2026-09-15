package com.carteiraacoesbackend.dto;

import com.carteiraacoesbackend.domains.enums.Moeda;
import java.math.BigDecimal;

public record ResumoPorMoedaResponse(
    Moeda moeda, BigDecimal valorAtual, BigDecimal lucroPrejuizo,
    BigDecimal valorInvestidoAtual, BigDecimal valorTotalComprado, BigDecimal valorTotalVendido,
    BigDecimal lucroPrejuizoRealizado, BigDecimal lucroPrejuizoNaoRealizado) {}
