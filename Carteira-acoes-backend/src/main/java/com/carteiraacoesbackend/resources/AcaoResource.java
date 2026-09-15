package com.carteiraacoesbackend.resources;

import com.carteiraacoesbackend.dto.AcaoRequest;
import com.carteiraacoesbackend.dto.AcaoResponse;
import com.carteiraacoesbackend.dto.AtualizacaoCotacoesResponse;
import com.carteiraacoesbackend.dto.ErroResponse;
import com.carteiraacoesbackend.dto.HistoricoCotacaoResponse;
import com.carteiraacoesbackend.services.AcaoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Pattern;
import java.net.URI;
import java.time.OffsetDateTime;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@RestController
@RequestMapping("/acoes")
@Validated
@Tag(name = "Ações e cotações")
public class AcaoResource {
  private final AcaoService service;

  public AcaoResource(AcaoService service) {
    this.service = service;
  }

  @PostMapping
  @Operation(summary = "Cadastra uma ação usando dados do provedor do mercado")
  @ApiResponses({
    @ApiResponse(responseCode = "201", description = "Ação criada"),
    @ApiResponse(
        responseCode = "400",
        description = "Ticker ou mercado inválido",
        content = @Content(schema = @Schema(implementation = ErroResponse.class))),
    @ApiResponse(
        responseCode = "404",
        description = "Ticker não encontrado no provedor",
        content = @Content(schema = @Schema(implementation = ErroResponse.class))),
    @ApiResponse(
        responseCode = "409",
        description = "Ticker já cadastrado",
        content = @Content(schema = @Schema(implementation = ErroResponse.class))),
    @ApiResponse(
        responseCode = "502",
        description = "Resposta externa inválida",
        content = @Content(schema = @Schema(implementation = ErroResponse.class))),
    @ApiResponse(
        responseCode = "503",
        description = "Provedor indisponível",
        content = @Content(schema = @Schema(implementation = ErroResponse.class)))
  })
  public ResponseEntity<AcaoResponse> criar(@Valid @RequestBody AcaoRequest request) {
    AcaoResponse response = service.criar(request);
    URI location =
        ServletUriComponentsBuilder.fromCurrentRequest()
            .path("/{id}")
            .buildAndExpand(response.id())
            .toUri();
    return ResponseEntity.created(location).body(response);
  }

  @GetMapping
  @Operation(summary = "Lista ações cadastradas")
  @ApiResponses({
    @ApiResponse(responseCode = "200", description = "Página de ações"),
    @ApiResponse(
        responseCode = "400",
        description = "Paginação ou ordenação inválida",
        content = @Content(schema = @Schema(implementation = ErroResponse.class)))
  })
  public Page<AcaoResponse> listar(Pageable pageable) {
    return service.listar(pageable);
  }

  @GetMapping("/{id}")
  @Operation(summary = "Consulta ação por ID")
  @ApiResponses({
    @ApiResponse(responseCode = "200", description = "Ação encontrada"),
    @ApiResponse(
        responseCode = "404",
        description = "Ação não encontrada",
        content = @Content(schema = @Schema(implementation = ErroResponse.class)))
  })
  public AcaoResponse buscarPorId(@PathVariable Long id) {
    return service.buscarPorId(id);
  }

  @DeleteMapping("/{id}")
  @Operation(summary = "Exclui ação sem vínculos")
  @ApiResponses({
    @ApiResponse(responseCode = "204", description = "Ação excluída"),
    @ApiResponse(responseCode = "404", description = "Ação não encontrada", content = @Content(schema = @Schema(implementation = ErroResponse.class))),
    @ApiResponse(responseCode = "409", description = "Ação possui vínculos", content = @Content(schema = @Schema(implementation = ErroResponse.class)))
  })
  public ResponseEntity<Void> excluir(@PathVariable Long id) {
    service.excluir(id);
    return ResponseEntity.noContent().build();
  }

  @GetMapping("/ticker/{ticker}")
  @Operation(summary = "Consulta ação por ticker")
  @ApiResponses({
    @ApiResponse(responseCode = "200", description = "Ação encontrada"),
    @ApiResponse(
        responseCode = "400",
        description = "Ticker inválido",
        content = @Content(schema = @Schema(implementation = ErroResponse.class))),
    @ApiResponse(
        responseCode = "404",
        description = "Ação não encontrada",
        content = @Content(schema = @Schema(implementation = ErroResponse.class)))
  })
  public AcaoResponse buscarPorTicker(
      @PathVariable @Pattern(regexp = AcaoRequest.TICKER_PATTERN) String ticker) {
    return service.buscarPorTicker(ticker);
  }

  @org.springframework.web.bind.annotation.PostMapping("/{id}/cotacao")
  @Operation(summary = "Atualiza a cotação de uma ação")
  @ApiResponses({
    @ApiResponse(responseCode = "200", description = "Cotação e histórico atualizados"),
    @ApiResponse(
        responseCode = "404",
        description = "Ação ou ticker não encontrado",
        content = @Content(schema = @Schema(implementation = ErroResponse.class))),
    @ApiResponse(
        responseCode = "502",
        description = "Resposta externa inválida",
        content = @Content(schema = @Schema(implementation = ErroResponse.class))),
    @ApiResponse(
        responseCode = "503",
        description = "Provedor indisponível",
        content = @Content(schema = @Schema(implementation = ErroResponse.class)))
  })
  public AcaoResponse atualizarCotacao(@PathVariable Long id) {
    return service.atualizarCotacao(id);
  }

  @org.springframework.web.bind.annotation.PostMapping("/atualizar-cotacoes")
  @Operation(summary = "Atualiza todas as cotações, isolando falhas por ação")
  @ApiResponse(responseCode = "200", description = "Contagem de sucessos e falhas do lote")
  public AtualizacaoCotacoesResponse atualizarTodasCotacoes() {
    return service.atualizarTodasCotacoes();
  }

  @GetMapping("/{id}/historico")
  @Operation(summary = "Lista o histórico inclusivo de cotações em ordem cronológica")
  @ApiResponses({
    @ApiResponse(responseCode = "200", description = "Página do histórico"),
    @ApiResponse(
        responseCode = "400",
        description = "Período, paginação ou ordenação inválida",
        content = @Content(schema = @Schema(implementation = ErroResponse.class))),
    @ApiResponse(
        responseCode = "404",
        description = "Ação não encontrada",
        content = @Content(schema = @Schema(implementation = ErroResponse.class)))
  })
  public Page<HistoricoCotacaoResponse> historico(
      @PathVariable Long id,
      @RequestParam(required = false) OffsetDateTime inicio,
      @RequestParam(required = false) OffsetDateTime fim,
      @PageableDefault(size = 100, sort = "dataHora", direction = Direction.ASC)
          Pageable pageable) {
    return service.historico(id, inicio, fim, pageable);
  }
}
