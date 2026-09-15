package com.carteiraacoesbackend.dto.integrations;

import java.util.List;

public record PtaxQuoteResponse(List<PtaxQuote> value) {
  public record PtaxQuote(String cotacaoVenda, String dataHoraCotacao) {}
}
