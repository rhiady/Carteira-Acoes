package com.carteiraacoesbackend.domains;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
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
@Table(name = "carteira")
public class Carteira extends AuditableEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false, length = 255)
  private String nome;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "usuario_id", nullable = false)
  private Usuario usuario;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "corretora_id", nullable = false)
  private Corretora corretora;

  @Setter(AccessLevel.NONE)
  @OneToMany(mappedBy = "carteira", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<CarteiraAcao> posicoes = new ArrayList<>();

  @Setter(AccessLevel.NONE)
  @OneToMany(mappedBy = "carteira")
  private List<Operacao> operacoes = new ArrayList<>();
}
