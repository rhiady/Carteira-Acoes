package com.carteiraacoesbackend.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.carteiraacoesbackend.domains.Usuario;
import com.carteiraacoesbackend.domains.enums.Papel;
import com.carteiraacoesbackend.dto.AlterarSenhaRequest;
import com.carteiraacoesbackend.dto.UsuarioCreateRequest;
import com.carteiraacoesbackend.dto.UsuarioResponse;
import com.carteiraacoesbackend.dto.UsuarioUpdateRequest;
import com.carteiraacoesbackend.exceptions.ApiException;
import com.carteiraacoesbackend.mappers.UsuarioMapper;
import com.carteiraacoesbackend.repositories.UsuarioRepository;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.security.crypto.password.PasswordEncoder;

class UsuarioServiceUnitTest {

  private UsuarioRepository repository;
  private UsuarioMapper mapper;
  private PasswordEncoder passwordEncoder;
  private UsuarioService service;

  @BeforeEach
  void setUp() {
    repository = mock(UsuarioRepository.class);
    mapper = mock(UsuarioMapper.class);
    passwordEncoder = mock(PasswordEncoder.class);
    service = new UsuarioService(repository, mapper, passwordEncoder);
  }

  @Test
  void criaUsuarioComEmailNormalizadoEsenhaCodificada() {
    Usuario usuario = usuario(1L, "ana@example.com");
    UsuarioResponse response = new UsuarioResponse(1L, "ana@example.com", Papel.USER, true);
    when(repository.existsByEmail("ana@example.com")).thenReturn(false);
    when(passwordEncoder.encode("senha-segura")).thenReturn("hash-bcrypt");
    when(mapper.toEntity(any(), eq("hash-bcrypt"))).thenReturn(usuario);
    when(repository.save(usuario)).thenReturn(usuario);
    when(mapper.toResponse(usuario)).thenReturn(response);

    UsuarioResponse actual =
        service.criar(new UsuarioCreateRequest(" ANA@EXAMPLE.COM ", "senha-segura", Papel.USER));

    assertThat(actual).isEqualTo(response);
    verify(repository).existsByEmail("ana@example.com");
    verify(passwordEncoder).encode("senha-segura");
    verify(repository).save(usuario);
  }

  @Test
  void rejeitaEmailDuplicado() {
    when(repository.existsByEmail("ana@example.com")).thenReturn(true);

    assertThatThrownBy(
            () ->
                service.criar(
                    new UsuarioCreateRequest("ana@example.com", "senha-segura", Papel.USER)))
        .isInstanceOf(ApiException.class)
        .extracting(exception -> ((ApiException) exception).getCode())
        .isEqualTo("EMAIL_DUPLICADO");

    verify(repository, never()).save(any());
  }

  @Test
  void atualizaMantendoOProprioEmail() {
    Usuario usuario = usuario(1L, "ana@example.com");
    UsuarioResponse response = new UsuarioResponse(1L, "ana@example.com", Papel.USER, false);
    when(repository.findById(1L)).thenReturn(Optional.of(usuario));
    when(repository.existsByEmailAndIdNot("ana@example.com", 1L)).thenReturn(false);
    when(repository.save(usuario)).thenReturn(usuario);
    when(mapper.toResponse(usuario)).thenReturn(response);

    UsuarioResponse actual =
        service.atualizar(1L, new UsuarioUpdateRequest(" ANA@EXAMPLE.COM ", false));

    assertThat(actual).isEqualTo(response);
    assertThat(usuario.getEmail()).isEqualTo("ana@example.com");
    assertThat(usuario.getNome()).isEqualTo("ana@example.com");
    assertThat(usuario.getAtivo()).isFalse();
    verify(repository).save(usuario);
  }

  @Test
  void rejeitaEmailPertencenteAOutroUsuario() {
    Usuario usuario = usuario(1L, "ana@example.com");
    when(repository.findById(1L)).thenReturn(Optional.of(usuario));
    when(repository.existsByEmailAndIdNot("joao@example.com", 1L)).thenReturn(true);

    assertThatThrownBy(
            () -> service.atualizar(1L, new UsuarioUpdateRequest("joao@example.com", true)))
        .isInstanceOf(ApiException.class)
        .extracting(exception -> ((ApiException) exception).getCode())
        .isEqualTo("EMAIL_DUPLICADO");

    verify(repository, never()).save(any());
  }

  @Test
  void rejeitaConsultaDeUsuarioInexistente() {
    when(repository.findById(99L)).thenReturn(Optional.empty());

    assertThatThrownBy(() -> service.buscarPorId(99L))
        .isInstanceOf(ApiException.class)
        .extracting(exception -> ((ApiException) exception).getCode())
        .isEqualTo("USUARIO_NAO_ENCONTRADO");
  }

  @Test
  void alteraSenhaSemPersistirTextoEmClaro() {
    Usuario usuario = usuario(1L, "ana@example.com");
    UsuarioResponse response = new UsuarioResponse(1L, "ana@example.com", Papel.USER, true);
    when(repository.findById(1L)).thenReturn(Optional.of(usuario));
    when(passwordEncoder.encode("nova-senha")).thenReturn("novo-hash");
    when(repository.save(usuario)).thenReturn(usuario);
    when(mapper.toResponse(usuario)).thenReturn(response);

    service.alterarSenha(1L, new AlterarSenhaRequest("nova-senha"));

    ArgumentCaptor<Usuario> captor = ArgumentCaptor.forClass(Usuario.class);
    verify(repository).save(captor.capture());
    assertThat(captor.getValue().getSenha()).isEqualTo("novo-hash");
    assertThat(captor.getValue().getSenha()).isNotEqualTo("nova-senha");
  }

  private Usuario usuario(Long id, String email) {
    Usuario usuario = new Usuario();
    usuario.setId(id);
    usuario.setNome(email);
    usuario.setEmail(email);
    usuario.setPapel(Papel.USER);
    usuario.setAtivo(true);
    return usuario;
  }
}
