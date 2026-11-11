package com.carteiraacoesbackend.mappers;

import com.carteiraacoesbackend.domains.Usuario;
import com.carteiraacoesbackend.domains.enums.Papel;
import com.carteiraacoesbackend.dto.UsuarioRequest;
import com.carteiraacoesbackend.dto.UsuarioResponse;
import org.springframework.stereotype.Component;

@Component
public class UsuarioMapper {

  public Usuario toEntity(UsuarioRequest request) {
    Usuario usuario = new Usuario();
    usuario.setNome(request.nome());
    usuario.setEmail(request.email());
    usuario.setSenha(request.senha());
    usuario.setPapel(Papel.USER);
    return usuario;
  }

  public UsuarioResponse toResponse(Usuario usuario) {
    return new UsuarioResponse(
        usuario.getId(),
        usuario.getNome(),
        usuario.getEmail(),
        usuario.getCreatedAt(),
        usuario.getUpdatedAt());
  }
}
