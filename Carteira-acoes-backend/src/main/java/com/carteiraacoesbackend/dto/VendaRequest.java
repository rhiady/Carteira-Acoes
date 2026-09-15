package com.carteiraacoesbackend.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;

public record VendaRequest(
    @NotNull @Positive Long carteiraId,
    @NotNull @Positive Long acaoId,
    @NotNull @DecimalMin("0.0001") BigDecimal quantidade,
    @DecimalMin("0.0001") BigDecimal precoUnitario,
    @DecimalMin("0") BigDecimal corretagem,
    @DecimalMin("0") BigDecimal impostos,
    @DecimalMin("0") BigDecimal valorAdicional) {

  public VendaRequest(
      Long carteiraId,
      Long acaoId,
      BigDecimal quantidade,
      BigDecimal corretagem,
      BigDecimal impostos,
      BigDecimal valorAdicional) {
    this(carteiraId, acaoId, quantidade, null, corretagem, impostos, valorAdicional);
  }
}
