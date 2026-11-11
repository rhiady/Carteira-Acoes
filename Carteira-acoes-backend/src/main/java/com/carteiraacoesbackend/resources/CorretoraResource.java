package com.carteiraacoesbackend.resources;

import com.carteiraacoesbackend.dto.CorretoraRequest;
import com.carteiraacoesbackend.dto.CorretoraResponse;
import com.carteiraacoesbackend.dto.ErroResponse;
import com.carteiraacoesbackend.services.CorretoraService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Pattern;
import java.net.URI;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@RestController
@RequestMapping("/corretoras")
@Validated
@Tag(name = "Corretoras")
public class CorretoraResource {
  private final CorretoraService service;

  public CorretoraResource(CorretoraService service) {
    this.service = service;
  }

  @PostMapping
  @Operation(summary = "Cadastra uma corretora validada por CNPJ e registro CVM")
  @ApiResponses({
    @ApiResponse(responseCode = "201", description = "Corretora criada"),
    @ApiResponse(
        responseCode = "401",
        description = "Usuário não autenticado",
        content = @Content(schema = @Schema(implementation = ErroResponse.class))),
    @ApiResponse(
        responseCode = "403",
        description = "Apenas administradores podem criar corretoras",
        content = @Content(schema = @Schema(implementation = ErroResponse.class))),
    @ApiResponse(
        responseCode = "400",
        description = "CNPJ inválido",
        content = @Content(schema = @Schema(implementation = ErroResponse.class))),
    @ApiResponse(
        responseCode = "409",
        description = "CNPJ já cadastrado",
        content = @Content(schema = @Schema(implementation = ErroResponse.class))),
    @ApiResponse(
        responseCode = "422",
        description = "Dados externos incompletos ou registro CVM inativo",
        content = @Content(schema = @Schema(implementation = ErroResponse.class))),
    @ApiResponse(
        responseCode = "503",
        description = "Provedor externo indisponível",
        content = @Content(schema = @Schema(implementation = ErroResponse.class)))
  })
  public ResponseEntity<CorretoraResponse> criar(@Valid @RequestBody CorretoraRequest request) {
    CorretoraResponse response = service.criar(request);
    URI location =
        ServletUriComponentsBuilder.fromCurrentRequest()
            .path("/{id}")
            .buildAndExpand(response.id())
            .toUri();
    return ResponseEntity.created(location).body(response);
  }

  @GetMapping
  @Operation(summary = "Lista corretoras cadastradas")
  @ApiResponses({
    @ApiResponse(responseCode = "200", description = "Página de corretoras"),
    @ApiResponse(
        responseCode = "400",
        description = "Paginação ou ordenação inválida",
        content = @Content(schema = @Schema(implementation = ErroResponse.class)))
  })
  public Page<CorretoraResponse> listar(Pageable pageable) {
    return service.listar(pageable);
  }

  @GetMapping("/{id}")
  @Operation(summary = "Consulta corretora por ID")
  @ApiResponses({
    @ApiResponse(responseCode = "200", description = "Corretora encontrada"),
    @ApiResponse(
        responseCode = "404",
        description = "Corretora não encontrada",
        content = @Content(schema = @Schema(implementation = ErroResponse.class)))
  })
  public CorretoraResponse buscarPorId(@PathVariable Long id) {
    return service.buscarPorId(id);
  }

  @DeleteMapping("/{id}")
  @Operation(summary = "Exclui corretora sem vínculos")
  @ApiResponses({
    @ApiResponse(responseCode = "204", description = "Corretora excluída"),
    @ApiResponse(
        responseCode = "401",
        description = "Usuário não autenticado",
        content = @Content(schema = @Schema(implementation = ErroResponse.class))),
    @ApiResponse(
        responseCode = "403",
        description = "Apenas administradores podem excluir corretoras",
        content = @Content(schema = @Schema(implementation = ErroResponse.class))),
    @ApiResponse(
        responseCode = "404",
        description = "Corretora não encontrada",
        content = @Content(schema = @Schema(implementation = ErroResponse.class))),
    @ApiResponse(
        responseCode = "409",
        description = "Corretora possui vínculos",
        content = @Content(schema = @Schema(implementation = ErroResponse.class)))
  })
  public ResponseEntity<Void> excluir(@PathVariable Long id) {
    service.excluir(id);
    return ResponseEntity.noContent().build();
  }

  @GetMapping("/cnpj/{cnpj}")
  @Operation(summary = "Consulta corretora por CNPJ")
  @ApiResponses({
    @ApiResponse(responseCode = "200", description = "Corretora encontrada"),
    @ApiResponse(
        responseCode = "400",
        description = "CNPJ inválido",
        content = @Content(schema = @Schema(implementation = ErroResponse.class))),
    @ApiResponse(
        responseCode = "404",
        description = "Corretora não encontrada",
        content = @Content(schema = @Schema(implementation = ErroResponse.class)))
  })
  public CorretoraResponse buscarPorCnpj(
      @PathVariable
          @Pattern(
              regexp = CorretoraRequest.CNPJ_PATTERN,
              message = "CNPJ deve conter 14 dígitos, com ou sem máscara.")
          String cnpj) {
    return service.buscarPorCnpj(cnpj);
  }
}
