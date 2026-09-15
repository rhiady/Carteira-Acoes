package com.carteiraacoesbackend.domains;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.time.OffsetDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(
    name = "corretora",
    uniqueConstraints = @UniqueConstraint(name = "uk_corretora_cnpj", columnNames = "cnpj"))
public class Corretora extends AuditableEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false, length = 14)
  private String cnpj;

  @Column(nullable = false, length = 255)
  private String razaoSocial;

  @Column(length = 255)
  private String nomeFantasia;

  @Column(length = 255)
  private String email;

  @Column(length = 30)
  private String telefone;

  @Column(length = 8)
  private String cep;

  @Column(length = 255)
  private String logradouro;

  @Column(length = 30)
  private String numero;

  @Column(length = 255)
  private String complemento;

  @Column(length = 255)
  private String bairro;

  @Column(length = 255)
  private String cidade;

  @Column(length = 2)
  private String uf;

  @Column(length = 100)
  private String situacaoCadastral;

  @Column(length = 100)
  private String registroCvm;

  private OffsetDateTime dataValidacaoCvm;
}
