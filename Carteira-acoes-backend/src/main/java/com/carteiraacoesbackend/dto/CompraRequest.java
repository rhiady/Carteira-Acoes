package com.carteiraacoesbackend.dto;

import com.carteiraacoesbackend.domains.enums.Mercado;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;

public record CompraRequest(
    @NotNull @Positive Long carteiraId,
    @Positive Long acaoId,
    @Pattern(regexp = AcaoRequest.TICKER_PATTERN) String ticker,
    Mercado mercado,
    @NotNull @DecimalMin("0.0001") BigDecimal quantidade,
    @DecimalMin("0.0001") BigDecimal precoUnitario,
    @DecimalMin("0") BigDecimal corretagem,
    @DecimalMin("0") BigDecimal impostos,
    @DecimalMin("0") BigDecimal valorAdicional) {

  public CompraRequest(
      Long carteiraId,
      Long acaoId,
      BigDecimal quantidade,
      BigDecimal precoUnitario,
      BigDecimal corretagem,
      BigDecimal impostos,
      BigDecimal valorAdicional) {
    this(
        carteiraId,
        acaoId,
        null,
        null,
        quantidade,
        precoUnitario,
        corretagem,
        impostos,
        valorAdicional);
  }

  @AssertTrue(message = "Informe acaoId ou ticker e mercado.")
  public boolean isStockIdentifierValid() {
    boolean hasId = acaoId != null;
    boolean hasTickerAndMarket = ticker != null && !ticker.isBlank() && mercado != null;
    return hasId != hasTickerAndMarket;
  }
}
