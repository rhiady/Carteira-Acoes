package com.carteiraacoesbackend.services;

import com.carteiraacoesbackend.domains.Usuario;
import com.carteiraacoesbackend.dto.LoginRequest;
import com.carteiraacoesbackend.dto.LoginResponse;
import com.carteiraacoesbackend.exceptions.ApiException;
import com.carteiraacoesbackend.repositories.UsuarioRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class AuthService {

  private static final String INVALID_CREDENTIALS_MESSAGE = "Email ou senha inválidos.";

  private final UsuarioRepository repository;
  private final PasswordEncoder passwordEncoder;

  public AuthService(UsuarioRepository repository, PasswordEncoder passwordEncoder) {
    this.repository = repository;
    this.passwordEncoder = passwordEncoder;
  }

  public LoginResponse login(LoginRequest request) {
    String email = request.email().trim().toLowerCase();
    Usuario usuario = repository.findByEmail(email).orElseThrow(this::invalidCredentials);

    if (!passwordEncoder.matches(request.senha(), usuario.getSenha())) {
      throw invalidCredentials();
    }

    return new LoginResponse(usuario.getId(), usuario.getNome(), usuario.getEmail());
  }

  private ApiException invalidCredentials() {
    return ApiException.unauthorized("CREDENCIAIS_INVALIDAS", INVALID_CREDENTIALS_MESSAGE);
  }
}
