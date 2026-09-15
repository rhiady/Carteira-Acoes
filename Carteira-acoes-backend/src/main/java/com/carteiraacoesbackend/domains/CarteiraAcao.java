package com.carteiraacoesbackend.domains;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.persistence.Version;
import java.math.BigDecimal;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(
    name = "carteira_acao",
    uniqueConstraints =
        @UniqueConstraint(
            name = "uk_carteira_acao",
            columnNames = {"carteira_id", "acao_id"}))
public class CarteiraAcao extends AuditableEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "carteira_id", nullable = false)
  private Carteira carteira;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "acao_id", nullable = false)
  private Acao acao;

  @Column(nullable = false, precision = 19, scale = 4)
  private BigDecimal quantidade = BigDecimal.ZERO;

  @Version
  @Column(nullable = false)
  private Long version;
}
