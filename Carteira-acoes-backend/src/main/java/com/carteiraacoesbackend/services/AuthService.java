package com.carteiraacoesbackend.services;

import com.carteiraacoesbackend.config.JwtProperties;
import com.carteiraacoesbackend.dto.LoginRequest;
import com.carteiraacoesbackend.dto.AlterarSenhaRequest;
import com.carteiraacoesbackend.dto.TokenResponse;
import com.carteiraacoesbackend.exceptions.ApiException;
import com.carteiraacoesbackend.repositories.UsuarioRepository;
import java.time.Instant;
import java.util.List;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

  private static final String INVALID_CREDENTIALS_MESSAGE = "Email ou senha inválidos.";

  private final AuthenticationManager authenticationManager;
  private final JwtEncoder jwtEncoder;
  private final JwtProperties jwtProperties;
  private final CurrentUserService currentUserService;
  private final PasswordEncoder passwordEncoder;
  private final UsuarioRepository usuarioRepository;

  public AuthService(
      AuthenticationManager authenticationManager,
      JwtEncoder jwtEncoder,
      JwtProperties jwtProperties,
      CurrentUserService currentUserService,
      PasswordEncoder passwordEncoder,
      UsuarioRepository usuarioRepository) {
    this.authenticationManager = authenticationManager;
    this.jwtEncoder = jwtEncoder;
    this.jwtProperties = jwtProperties;
    this.currentUserService = currentUserService;
    this.passwordEncoder = passwordEncoder;
    this.usuarioRepository = usuarioRepository;
  }

  public TokenResponse login(LoginRequest request) {
    Authentication authentication;
    try {
      authentication =
          authenticationManager.authenticate(
              UsernamePasswordAuthenticationToken.unauthenticated(
                  request.login().trim().toLowerCase(), request.password()));
    } catch (org.springframework.security.core.AuthenticationException exception) {
      throw ApiException.unauthorized("CREDENCIAIS_INVALIDAS", INVALID_CREDENTIALS_MESSAGE);
    }

    Instant issuedAt = Instant.now();
    Instant expiresAt = issuedAt.plus(jwtProperties.expiration());
    List<String> authorities =
        authentication.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList();
    JwtClaimsSet claims =
        JwtClaimsSet.builder()
            .issuer(jwtProperties.issuer())
            .issuedAt(issuedAt)
            .expiresAt(expiresAt)
            .subject(authentication.getName())
            .claim("authorities", authorities)
            .build();
    JwsHeader header = JwsHeader.with(MacAlgorithm.HS256).type("JWT").build();
    String token = jwtEncoder.encode(JwtEncoderParameters.from(header, claims)).getTokenValue();
    return new TokenResponse(token, "Bearer", jwtProperties.expiration().toSeconds());
  }

  @Transactional
  public void redefinirSenha(AlterarSenhaRequest request) {
    var usuario = currentUserService.required();
    usuario.setSenha(passwordEncoder.encode(request.password()));
    usuarioRepository.save(usuario);
  }
}
