package com.carteiraacoesbackend.integrations;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

public record Cotacao(BigDecimal preco, OffsetDateTime dataHora, String nomeEmpresa) {
  public Cotacao(BigDecimal preco, OffsetDateTime dataHora) {
    this(preco, dataHora, null);
  }
}
