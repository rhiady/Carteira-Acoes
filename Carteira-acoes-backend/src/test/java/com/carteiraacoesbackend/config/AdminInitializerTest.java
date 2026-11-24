package com.carteiraacoesbackend.config;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import com.carteiraacoesbackend.domains.Usuario;
import com.carteiraacoesbackend.repositories.UsuarioRepository;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.password.PasswordEncoder;

class AdminInitializerTest {

  private final UsuarioRepository usuarios = mock(UsuarioRepository.class);
  private final PasswordEncoder encoder = mock(PasswordEncoder.class);

  @Test
  void createsNormalizedAdminWithEncodedPassword() {
    when(usuarios.findByEmail("admin@test.com")).thenReturn(Optional.empty());
    when(encoder.encode("senha")).thenReturn("hash");

    new AdminInitializer(usuarios, encoder, " Admin@Test.com ", "senha")
        .criarAdministradorInicial();

    var captor = org.mockito.ArgumentCaptor.forClass(Usuario.class);
    verify(usuarios).save(captor.capture());
    assertThat(captor.getValue().getEmail()).isEqualTo("admin@test.com");
    assertThat(captor.getValue().getSenha()).isEqualTo("hash");
    assertThat(captor.getValue().getPapel())
        .isEqualTo(com.carteiraacoesbackend.domains.enums.Papel.ADMIN);
  }

  @Test
  void doesNotCreateAdminWhenEmailAlreadyExists() {
    when(usuarios.findByEmail("admin@test.com")).thenReturn(Optional.of(new Usuario()));

    new AdminInitializer(usuarios, encoder, "admin@test.com", "senha").criarAdministradorInicial();

    verify(usuarios).findByEmail("admin@test.com");
    verifyNoInteractions(encoder);
  }

  @Test
  void rejectsBlankBootstrapCredentials() {
    assertThatThrownBy(
            () -> new AdminInitializer(usuarios, encoder, " ", "senha").criarAdministradorInicial())
        .isInstanceOf(IllegalArgumentException.class);
    assertThatThrownBy(
            () ->
                new AdminInitializer(usuarios, encoder, "admin@test.com", " ")
                    .criarAdministradorInicial())
        .isInstanceOf(IllegalArgumentException.class);
    verifyNoInteractions(usuarios, encoder);
  }
}
