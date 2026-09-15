package com.carteiraacoesbackend.resources;

import com.carteiraacoesbackend.dto.ErroResponse;
import com.carteiraacoesbackend.dto.UsuarioRequest;
import com.carteiraacoesbackend.dto.UsuarioResponse;
import com.carteiraacoesbackend.dto.UsuarioUpdateRequest;
import com.carteiraacoesbackend.services.UsuarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.net.URI;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@RestController
@RequestMapping("/usuarios")
@Tag(name = "Usuários")
public class UsuarioResource {

  private final UsuarioService service;

  public UsuarioResource(UsuarioService service) {
    this.service = service;
  }

  @PostMapping
  @Operation(summary = "Cadastra um novo usuário")
  @ApiResponses({
    @ApiResponse(responseCode = "201", description = "Usuário criado"),
    @ApiResponse(
        responseCode = "400",
        description = "Dados inválidos",
        content = @Content(schema = @Schema(implementation = ErroResponse.class))),
    @ApiResponse(
        responseCode = "409",
        description = "Email já cadastrado",
        content = @Content(schema = @Schema(implementation = ErroResponse.class)))
  })
  public ResponseEntity<UsuarioResponse> criar(@Valid @RequestBody UsuarioRequest request) {
    UsuarioResponse response = service.criar(request);
    URI location =
        ServletUriComponentsBuilder.fromCurrentRequest()
            .path("/{id}")
            .buildAndExpand(response.id())
            .toUri();
    return ResponseEntity.created(location).body(response);
  }

  @GetMapping
  @Operation(summary = "Lista somente o usuário autenticado")
  @ApiResponses({
    @ApiResponse(responseCode = "200", description = "Página contendo o usuário autenticado"),
    @ApiResponse(
        responseCode = "400",
        description = "Paginação ou ordenação inválida",
        content = @Content(schema = @Schema(implementation = ErroResponse.class))),
    @ApiResponse(
        responseCode = "401",
        description = "Usuário não autenticado",
        content = @Content(schema = @Schema(implementation = ErroResponse.class)))
  })
  public Page<UsuarioResponse> listar(Pageable pageable) {
    return service.listar(pageable);
  }

  @GetMapping("/{id}")
  @Operation(summary = "Consulta os dados do próprio usuário")
  @ApiResponses({
    @ApiResponse(responseCode = "200", description = "Usuário encontrado"),
    @ApiResponse(
        responseCode = "401",
        description = "Usuário não autenticado",
        content = @Content(schema = @Schema(implementation = ErroResponse.class))),
    @ApiResponse(
        responseCode = "404",
        description = "Usuário não encontrado ou pertencente a outra sessão",
        content = @Content(schema = @Schema(implementation = ErroResponse.class)))
  })
  public UsuarioResponse buscarPorId(@PathVariable Long id) {
    return service.buscarPorId(id);
  }

  @PutMapping("/{id}")
  @Operation(summary = "Atualiza os dados do próprio usuário; senha omitida é preservada")
  @ApiResponses({
    @ApiResponse(responseCode = "200", description = "Usuário atualizado"),
    @ApiResponse(
        responseCode = "400",
        description = "Dados inválidos",
        content = @Content(schema = @Schema(implementation = ErroResponse.class))),
    @ApiResponse(
        responseCode = "401",
        description = "Usuário não autenticado",
        content = @Content(schema = @Schema(implementation = ErroResponse.class))),
    @ApiResponse(
        responseCode = "404",
        description = "Usuário não encontrado ou pertencente a outra sessão",
        content = @Content(schema = @Schema(implementation = ErroResponse.class))),
    @ApiResponse(
        responseCode = "409",
        description = "Email já cadastrado",
        content = @Content(schema = @Schema(implementation = ErroResponse.class)))
  })
  public UsuarioResponse atualizar(
      @PathVariable Long id, @Valid @RequestBody UsuarioUpdateRequest request) {
    return service.atualizar(id, request);
  }
}
