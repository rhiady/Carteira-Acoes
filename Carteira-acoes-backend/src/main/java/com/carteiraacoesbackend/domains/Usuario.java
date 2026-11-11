package com.carteiraacoesbackend.domains;

import com.carteiraacoesbackend.domains.enums.Papel;
import jakarta.persistence.*;
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
    name = "usuario",
    uniqueConstraints = @UniqueConstraint(name = "uk_usuario_email", columnNames = "email"))
public class Usuario extends AuditableEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false, length = 255)
  private String nome;

  @Column(nullable = false, length = 255)
  private String email;

  @Column(nullable = false, length = 255)
  private String senha;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 20)
  private Papel papel = Papel.USER;

  @Setter(AccessLevel.NONE)
  @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<Carteira> carteiras = new ArrayList<>();

  public void adicionarCarteira(Carteira carteira) {
    carteiras.add(carteira);
    carteira.setUsuario(this);
  }
}
