package com.carteiraacoesbackend.dto;

import java.util.List;

public record CarteiraResumoResponse(
    Long carteiraId,
    String nome,
    int quantidadeAtivos,
    List<ResumoPorMoedaResponse> resumosPorMoeda) {}
