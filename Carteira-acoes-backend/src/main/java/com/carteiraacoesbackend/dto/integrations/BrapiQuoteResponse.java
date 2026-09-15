package com.carteiraacoesbackend.dto.integrations;

import java.math.BigDecimal;
import java.util.List;

public record BrapiQuoteResponse(List<Result> results) {
  public record Result(
      BigDecimal regularMarketPrice, String longName, String shortName, String regularMarketTime) {}
}
