package com.carteiraacoesbackend.services;

import com.carteiraacoesbackend.domains.Carteira;
import com.carteiraacoesbackend.domains.Usuario;
import com.carteiraacoesbackend.exceptions.ApiException;
import com.carteiraacoesbackend.repositories.UsuarioRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Service
public class CurrentUserService {
  private final HttpServletRequest request;
  private final UsuarioRepository usuarios;

  public CurrentUserService(HttpServletRequest request, UsuarioRepository usuarios) {
    this.request = request;
    this.usuarios = usuarios;
  }

  public Usuario required() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (authentication == null
        || !authentication.isAuthenticated()
        || authentication instanceof AnonymousAuthenticationToken) {
      throw ApiException.unauthorized("USUARIO_NAO_AUTENTICADO", "Usuário não autenticado.");
    }
    return usuarios
        .findByEmail(authentication.getName())
        .orElseThrow(
            () -> ApiException.unauthorized("USUARIO_NAO_AUTENTICADO", "Usuário não autenticado."));
  }

  public boolean isWebRequest() {
    if (!(RequestContextHolder.getRequestAttributes()
        instanceof ServletRequestAttributes attributes)) {
      return false;
    }
    String uri = attributes.getRequest().getRequestURI();
    return uri != null && !uri.isBlank();
  }

  public void requireOwnership(Carteira carteira) {
    if (!required().getId().equals(carteira.getUsuario().getId())) {
      throw ApiException.notFound("CARTEIRA_NAO_ENCONTRADA", "Carteira não encontrada.");
    }
  }

  public Usuario requireUserId(Long usuarioId) {
    Usuario usuario = required();
    if (!usuario.getId().equals(usuarioId)) {
      throw ApiException.notFound("USUARIO_NAO_ENCONTRADO", "Usuário não encontrado.");
    }
    return usuario;
  }
}
