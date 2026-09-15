package com.carteiraacoesbackend.resources;

import com.carteiraacoesbackend.dto.CompraRequest;
import com.carteiraacoesbackend.dto.ErroResponse;
import com.carteiraacoesbackend.dto.OperacaoResponse;
import com.carteiraacoesbackend.dto.VendaRequest;
import com.carteiraacoesbackend.services.OperacaoService;
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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@RestController
@RequestMapping("/operacoes")
@Tag(name = "Operações")
public class OperacaoResource {
  private final OperacaoService service;

  public OperacaoResource(OperacaoService service) {
    this.service = service;
  }

  @PostMapping("/compras")
  @Operation(summary = "Executa compra na carteira própria")
  @ApiResponses({
    @ApiResponse(responseCode = "201", description = "Compra realizada"),
    @ApiResponse(
        responseCode = "400",
        description = "Request ou identificador da ação inválido",
        content = @Content(schema = @Schema(implementation = ErroResponse.class))),
    @ApiResponse(
        responseCode = "401",
        description = "Usuário não autenticado",
        content = @Content(schema = @Schema(implementation = ErroResponse.class))),
    @ApiResponse(
        responseCode = "404",
        description = "Carteira, ação ou ticker não encontrado",
        content = @Content(schema = @Schema(implementation = ErroResponse.class))),
    @ApiResponse(
        responseCode = "409",
        description = "Conflito de persistência ou concorrência",
        content = @Content(schema = @Schema(implementation = ErroResponse.class))),
    @ApiResponse(
        responseCode = "502",
        description = "Cotação externa inválida",
        content = @Content(schema = @Schema(implementation = ErroResponse.class))),
    @ApiResponse(
        responseCode = "503",
        description = "Provedor externo indisponível",
        content = @Content(schema = @Schema(implementation = ErroResponse.class)))
  })
  public ResponseEntity<OperacaoResponse> comprar(@Valid @RequestBody CompraRequest request) {
    return criada(service.comprar(request));
  }

  @PostMapping("/vendas")
  @Operation(summary = "Executa venda na carteira própria")
  @ApiResponses({
    @ApiResponse(responseCode = "201", description = "Venda realizada"),
    @ApiResponse(
        responseCode = "400",
        description = "Request inválido",
        content = @Content(schema = @Schema(implementation = ErroResponse.class))),
    @ApiResponse(
        responseCode = "401",
        description = "Usuário não autenticado",
        content = @Content(schema = @Schema(implementation = ErroResponse.class))),
    @ApiResponse(
        responseCode = "404",
        description = "Carteira ou ação não encontrada",
        content = @Content(schema = @Schema(implementation = ErroResponse.class))),
    @ApiResponse(
        responseCode = "409",
        description = "Conflito de concorrência",
        content = @Content(schema = @Schema(implementation = ErroResponse.class))),
    @ApiResponse(
        responseCode = "422",
        description = "Posição ou quantidade insuficiente",
        content = @Content(schema = @Schema(implementation = ErroResponse.class))),
    @ApiResponse(
        responseCode = "502",
        description = "Cotação externa inválida",
        content = @Content(schema = @Schema(implementation = ErroResponse.class))),
    @ApiResponse(
        responseCode = "503",
        description = "Provedor externo indisponível",
        content = @Content(schema = @Schema(implementation = ErroResponse.class)))
  })
  public ResponseEntity<OperacaoResponse> vender(@Valid @RequestBody VendaRequest request) {
    return criada(service.vender(request));
  }

  @GetMapping("/{id}")
  @Operation(summary = "Consulta operação da carteira própria")
  @ApiResponses({
    @ApiResponse(responseCode = "200", description = "Operação encontrada"),
    @ApiResponse(
        responseCode = "401",
        description = "Usuário não autenticado",
        content = @Content(schema = @Schema(implementation = ErroResponse.class))),
    @ApiResponse(
        responseCode = "404",
        description = "Operação não encontrada",
        content = @Content(schema = @Schema(implementation = ErroResponse.class)))
  })
  public OperacaoResponse buscar(@PathVariable Long id) {
    return service.buscarPorId(id);
  }

  @GetMapping("/carteiras/{carteiraId}")
  @Operation(summary = "Lista operações da carteira própria")
  @ApiResponses({
    @ApiResponse(
        responseCode = "200",
        description = "Página de operações em ordem cronológica decrescente"),
    @ApiResponse(
        responseCode = "400",
        description = "Paginação ou ordenação inválida",
        content = @Content(schema = @Schema(implementation = ErroResponse.class))),
    @ApiResponse(
        responseCode = "401",
        description = "Usuário não autenticado",
        content = @Content(schema = @Schema(implementation = ErroResponse.class))),
    @ApiResponse(
        responseCode = "404",
        description = "Carteira não encontrada",
        content = @Content(schema = @Schema(implementation = ErroResponse.class)))
  })
  public Page<OperacaoResponse> historico(@PathVariable Long carteiraId, Pageable pageable) {
    return service.historico(carteiraId, pageable);
  }

  private ResponseEntity<OperacaoResponse> criada(OperacaoResponse response) {
    URI uri =
        ServletUriComponentsBuilder.fromCurrentContextPath()
            .path("/operacoes/{id}")
            .buildAndExpand(response.id())
            .toUri();
    return ResponseEntity.created(uri).body(response);
  }
}
