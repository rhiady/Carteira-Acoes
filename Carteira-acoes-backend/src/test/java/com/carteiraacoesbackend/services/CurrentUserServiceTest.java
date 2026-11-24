package com.carteiraacoesbackend.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.carteiraacoesbackend.domains.Carteira;
import com.carteiraacoesbackend.domains.Usuario;
import com.carteiraacoesbackend.exceptions.ApiException;
import com.carteiraacoesbackend.repositories.UsuarioRepository;
import jakarta.servlet.http.HttpServletRequest;
import java.util.Optional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

class CurrentUserServiceTest {

  private final HttpServletRequest request = mock(HttpServletRequest.class);
  private final UsuarioRepository usuarios = mock(UsuarioRepository.class);
  private final CurrentUserService service = new CurrentUserService(request, usuarios);

  @AfterEach
  void clearContext() {
    SecurityContextHolder.clearContext();
    RequestContextHolder.resetRequestAttributes();
  }

  @Test
  void rejectsMissingUnauthenticatedAndUnknownUsers() {
    assertThatThrownBy(service::required).isInstanceOf(ApiException.class);

    SecurityContextHolder.getContext()
        .setAuthentication(
            new AnonymousAuthenticationToken(
                "key",
                "anonymous",
                java.util.List.of(new SimpleGrantedAuthority("ROLE_ANONYMOUS"))));
    assertThatThrownBy(service::required).isInstanceOf(ApiException.class);

    var authentication = new UsernamePasswordAuthenticationToken("ana@test.com", "senha");
    authentication.setAuthenticated(false);
    SecurityContextHolder.getContext().setAuthentication(authentication);
    assertThatThrownBy(service::required).isInstanceOf(ApiException.class);

    SecurityContextHolder.getContext()
        .setAuthentication(
            UsernamePasswordAuthenticationToken.authenticated(
                "ana@test.com", "senha", java.util.List.of()));
    when(usuarios.findByEmail("ana@test.com")).thenReturn(Optional.empty());
    assertThatThrownBy(service::required).isInstanceOf(ApiException.class);
  }

  @Test
  void returnsAuthenticatedUserAndValidatesUserId() {
    Usuario user = new Usuario();
    user.setId(7L);
    user.setEmail("ana@test.com");
    SecurityContextHolder.getContext()
        .setAuthentication(
            UsernamePasswordAuthenticationToken.authenticated(
                "ana@test.com", "senha", java.util.List.of()));
    when(usuarios.findByEmail("ana@test.com")).thenReturn(Optional.of(user));

    assertThat(service.required()).isSameAs(user);
    assertThat(service.requireUserId(7L)).isSameAs(user);
    assertThatThrownBy(() -> service.requireUserId(8L)).isInstanceOf(ApiException.class);
  }

  @Test
  void identifiesWebRequestsAndOwnership() {
    assertThat(service.isWebRequest()).isFalse();
    when(request.getRequestURI()).thenReturn(" ");
    RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));
    assertThat(service.isWebRequest()).isFalse();
    when(request.getRequestURI()).thenReturn(null);
    assertThat(service.isWebRequest()).isFalse();
    when(request.getRequestURI()).thenReturn("/api/carteiras");
    assertThat(service.isWebRequest()).isTrue();

    Usuario owner = new Usuario();
    owner.setId(7L);
    owner.setEmail("ana@test.com");
    when(usuarios.findByEmail("ana@test.com")).thenReturn(Optional.of(owner));
    SecurityContextHolder.getContext()
        .setAuthentication(
            UsernamePasswordAuthenticationToken.authenticated(
                "ana@test.com", "senha", java.util.List.of()));
    Carteira carteira = new Carteira();
    carteira.setUsuario(owner);
    service.requireOwnership(carteira);

    Usuario other = new Usuario();
    other.setId(8L);
    carteira.setUsuario(other);
    assertThatThrownBy(() -> service.requireOwnership(carteira)).isInstanceOf(ApiException.class);
  }
}
