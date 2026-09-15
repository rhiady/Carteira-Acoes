package com.carteiraacoesbackend.services;

import com.carteiraacoesbackend.domains.Carteira;
import com.carteiraacoesbackend.domains.Usuario;
import com.carteiraacoesbackend.exceptions.ApiException;
import com.carteiraacoesbackend.repositories.UsuarioRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Service
public class CurrentUserService {
  public static final String SESSION_USER_ID = CurrentUserService.class.getName() + ".userId";

  private final HttpServletRequest request;
  private final UsuarioRepository usuarios;

  public CurrentUserService(HttpServletRequest request, UsuarioRepository usuarios) {
    this.request = request;
    this.usuarios = usuarios;
  }

  public Usuario required() {
    Object value =
        request.getSession(false) == null
            ? null
            : request.getSession(false).getAttribute(SESSION_USER_ID);
    if (!(value instanceof Long id)) {
      throw ApiException.unauthorized("USUARIO_NAO_AUTENTICADO", "Usuário não autenticado.");
    }
    return usuarios
        .findById(id)
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
