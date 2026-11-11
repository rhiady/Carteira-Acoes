package com.carteiraacoesbackend.resources;

import com.carteiraacoesbackend.domains.Usuario;
import com.carteiraacoesbackend.dto.ErroResponse;
import com.carteiraacoesbackend.dto.LoginRequest;
import com.carteiraacoesbackend.dto.LoginResponse;
import com.carteiraacoesbackend.services.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {

  private final AuthService service;
  private final SecurityContextRepository securityContextRepository;

  public AuthController(AuthService service, SecurityContextRepository securityContextRepository) {
    this.service = service;
    this.securityContextRepository = securityContextRepository;
  }

  @Operation(summary = "Autentica um usuário por email e senha")
  @ApiResponses({
    @ApiResponse(responseCode = "200", description = "Login realizado com sucesso"),
    @ApiResponse(
        responseCode = "400",
        description = "Request inválido",
        content = @Content(schema = @Schema(implementation = ErroResponse.class))),
    @ApiResponse(
        responseCode = "401",
        description = "Email ou senha inválidos",
        content = @Content(schema = @Schema(implementation = ErroResponse.class)))
  })
  @PostMapping("/login")
  public ResponseEntity<LoginResponse> login(
      @Valid @RequestBody LoginRequest request,
      HttpServletRequest httpRequest,
      HttpServletResponse httpResponse) {
    Usuario usuario = service.authenticate(request);
    Authentication authentication =
        UsernamePasswordAuthenticationToken.authenticated(
            usuario.getEmail(),
            null,
            List.of(new SimpleGrantedAuthority("ROLE_" + usuario.getPapel().name())));
    SecurityContext context = SecurityContextHolder.createEmptyContext();
    context.setAuthentication(authentication);
    SecurityContextHolder.setContext(context);
    securityContextRepository.saveContext(context, httpRequest, httpResponse);
    LoginResponse response =
        new LoginResponse(usuario.getId(), usuario.getNome(), usuario.getEmail());
    return ResponseEntity.ok(response);
  }
}
