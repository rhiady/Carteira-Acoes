package com.carteiraacoesbackend.domains;

import com.carteiraacoesbackend.domains.enums.Mercado;
import com.carteiraacoesbackend.domains.enums.Moeda;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(
    name = "acao",
    uniqueConstraints = @UniqueConstraint(name = "uk_acao_ticker", columnNames = "ticker"))
public class Acao extends AuditableEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false, length = 20)
  private String ticker;

  @Column(nullable = false, length = 255)
  private String nomeEmpresa;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 10)
  private Mercado mercado;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 3)
  private Moeda moeda;

  @Column(precision = 19, scale = 4)
  private BigDecimal cotacaoAtual;

  private OffsetDateTime dataHoraCotacao;

  @Setter(AccessLevel.NONE)
  @OneToMany(mappedBy = "acao")
  private List<CarteiraAcao> posicoes = new ArrayList<>();

  @Setter(AccessLevel.NONE)
  @OneToMany(mappedBy = "acao")
  private List<Operacao> operacoes = new ArrayList<>();
}
