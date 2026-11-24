package com.carteiraacoesbackend.services;

import com.carteiraacoesbackend.domains.Usuario;
import com.carteiraacoesbackend.repositories.UsuarioRepository;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UsuarioDetailsService implements UserDetailsService {

  private final UsuarioRepository usuarioRepository;

  public UsuarioDetailsService(UsuarioRepository usuarioRepository) {
    this.usuarioRepository = usuarioRepository;
  }

  @Override
  public UserDetails loadUserByUsername(String login) {
    Usuario usuario =
        usuarioRepository
            .findByEmail(login.trim().toLowerCase())
            .orElseThrow(() -> new UsernameNotFoundException("Usuario nao encontrado"));

    return User.withUsername(usuario.getEmail())
        .password(usuario.getSenha())
        .disabled(!Boolean.TRUE.equals(usuario.getAtivo()))
        .roles(usuario.getPapel().name())
        .build();
  }
}
