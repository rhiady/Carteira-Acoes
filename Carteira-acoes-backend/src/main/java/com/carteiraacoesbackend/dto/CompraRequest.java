package com.carteiraacoesbackend.dto;

import com.fasterxml.jackson.annotation.JsonAnySetter;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;

public record CompraRequest(
    @NotNull @Positive Long carteiraId,
    @NotNull @Positive Long acaoId,
    @NotNull @DecimalMin("0.0001") BigDecimal quantidade,
    @DecimalMin("0.0001") BigDecimal precoUnitario,
    @DecimalMin("0") BigDecimal corretagem,
    @DecimalMin("0") BigDecimal impostos,
    @DecimalMin("0") BigDecimal valorAdicional) {

  @JsonAnySetter
  public void rejectUnsupportedField(String name, Object value) {
    throw new IllegalArgumentException("Campo não permitido na compra: " + name);
  }
}
