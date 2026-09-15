package com.carteiraacoesbackend.domains;

import com.carteiraacoesbackend.domains.enums.TipoOperacao;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "operacao")
public class Operacao {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "carteira_id", nullable = false)
  private Carteira carteira;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "acao_id", nullable = false)
  private Acao acao;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private TipoOperacao tipo;

  @Column(nullable = false, precision = 19, scale = 4)
  private BigDecimal quantidade;

  @Column(nullable = false, precision = 19, scale = 4)
  private BigDecimal precoUnitario;

  @Column(nullable = false, precision = 19, scale = 4)
  private BigDecimal valorBruto;

  @Column(nullable = false, precision = 19, scale = 4)
  private BigDecimal corretagem = BigDecimal.ZERO;

  @Column(nullable = false, precision = 19, scale = 4)
  private BigDecimal impostos = BigDecimal.ZERO;

  @Column(nullable = false, precision = 19, scale = 4)
  private BigDecimal valorAdicional = BigDecimal.ZERO;

  @Column(nullable = false, precision = 19, scale = 4)
  private BigDecimal valorLiquido;

  @Column(nullable = false)
  private OffsetDateTime dataHora;

  @Column(name = "created_at", nullable = false, updatable = false)
  private OffsetDateTime createdAt;

  @PrePersist
  protected void onCreate() {
    OffsetDateTime now = OffsetDateTime.now(ZoneOffset.UTC);
    if (dataHora == null) {
      dataHora = now;
    }
    createdAt = now;
    recalcularValores();
  }

  @PreUpdate
  protected void onUpdate() {
    recalcularValores();
  }

  public void recalcularValores() {
    corretagem = valorOuZero(corretagem);
    impostos = valorOuZero(impostos);
    valorAdicional = valorOuZero(valorAdicional);
    if (quantidade != null && precoUnitario != null) {
      valorBruto = quantidade.multiply(precoUnitario);
      BigDecimal custos = corretagem.add(impostos).add(valorAdicional);
      valorLiquido =
          tipo == TipoOperacao.COMPRA ? valorBruto.add(custos) : valorBruto.subtract(custos);
    }
  }

  private BigDecimal valorOuZero(BigDecimal valor) {
    return valor == null ? BigDecimal.ZERO : valor;
  }
}
