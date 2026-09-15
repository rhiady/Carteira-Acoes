package com.carteiraacoesbackend.resources;

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
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {

  private final AuthService service;

  public AuthController(AuthService service) {
    this.service = service;
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
      @Valid @RequestBody LoginRequest request, HttpServletRequest httpRequest) {
    LoginResponse response = service.login(request);
    HttpSession session = httpRequest.getSession(false);
    if (session == null) {
      session = httpRequest.getSession(true);
    } else {
      httpRequest.changeSessionId();
    }
    session.setAttribute(
        com.carteiraacoesbackend.services.CurrentUserService.SESSION_USER_ID, response.id());
    return ResponseEntity.ok(response);
  }
}
