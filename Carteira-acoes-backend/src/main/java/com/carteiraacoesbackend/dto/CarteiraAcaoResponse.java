package com.carteiraacoesbackend.dto;

import java.math.BigDecimal;

public record CarteiraAcaoResponse(
    Long id, Long carteiraId, Long acaoId, String ticker, BigDecimal quantidade, Long version) {}
