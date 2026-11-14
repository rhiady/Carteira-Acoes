package com.carteiraacoesbackend.mappers;

import com.carteiraacoesbackend.domains.Usuario;
import com.carteiraacoesbackend.dto.UsuarioCreateRequest;
import com.carteiraacoesbackend.dto.UsuarioResponse;
import org.springframework.stereotype.Component;

@Component
public class UsuarioMapper {

  public Usuario toEntity(UsuarioCreateRequest request, String encodedPassword) {
    Usuario usuario = new Usuario();
    String login = request.login().trim().toLowerCase();
    usuario.setNome(login);
    usuario.setEmail(login);
    usuario.setSenha(encodedPassword);
    usuario.setPapel(com.carteiraacoesbackend.domains.enums.Papel.USER);
    usuario.setAtivo(true);
    return usuario;
  }

  public UsuarioResponse toResponse(Usuario usuario) {
    return new UsuarioResponse(
        usuario.getId(),
        usuario.getEmail(), usuario.getPapel(), usuario.getAtivo());
  }
}
