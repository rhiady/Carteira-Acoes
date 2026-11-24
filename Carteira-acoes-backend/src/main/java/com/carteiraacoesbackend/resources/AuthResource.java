package com.carteiraacoesbackend.resources;

import com.carteiraacoesbackend.dto.AlterarSenhaRequest;
import com.carteiraacoesbackend.dto.AuthMeResponse;
import com.carteiraacoesbackend.dto.CadastroRequest;
import com.carteiraacoesbackend.dto.LoginRequest;
import com.carteiraacoesbackend.dto.TokenResponse;
import com.carteiraacoesbackend.dto.UsuarioResponse;
import com.carteiraacoesbackend.services.AuthService;
import com.carteiraacoesbackend.services.CurrentUserService;
import com.carteiraacoesbackend.services.UsuarioService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthResource {

  private final AuthService authService;
  private final CurrentUserService currentUserService;
  private final UsuarioService usuarioService;

  public AuthResource(
      AuthService authService,
      CurrentUserService currentUserService,
      UsuarioService usuarioService) {
    this.authService = authService;
    this.currentUserService = currentUserService;
    this.usuarioService = usuarioService;
  }

  @PostMapping("/login")
  public ResponseEntity<TokenResponse> login(@Valid @RequestBody LoginRequest request) {
    return ResponseEntity.ok(authService.login(request));
  }

  @PostMapping("/cadastro")
  public ResponseEntity<UsuarioResponse> cadastrar(@Valid @RequestBody CadastroRequest request) {
    return ResponseEntity.status(HttpStatus.CREATED).body(usuarioService.cadastrar(request));
  }

  @PutMapping("/senha")
  public ResponseEntity<Void> redefinirSenha(@Valid @RequestBody AlterarSenhaRequest request) {
    authService.redefinirSenha(request);
    return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
  }

  @GetMapping("/me")
  public ResponseEntity<AuthMeResponse> me(Authentication authentication) {
    var usuario = currentUserService.required();
    List<String> authorities =
        authentication.getAuthorities().stream()
            .map(authority -> authority.getAuthority())
            .filter(authority -> authority.startsWith("ROLE_"))
            .toList();
    return ResponseEntity.ok(new AuthMeResponse(usuario.getId(), usuario.getEmail(), authorities));
  }
}
