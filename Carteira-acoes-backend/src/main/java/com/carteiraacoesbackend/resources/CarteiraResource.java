package com.carteiraacoesbackend.resources;

import com.carteiraacoesbackend.dto.CarteiraRequest;
import com.carteiraacoesbackend.dto.AtualizarCarteiraRequest;
import com.carteiraacoesbackend.dto.CarteiraResponse;
import com.carteiraacoesbackend.dto.CarteiraResumoResponse;
import com.carteiraacoesbackend.dto.ErroResponse;
import com.carteiraacoesbackend.dto.PosicaoAcaoResponse;
import com.carteiraacoesbackend.services.CarteiraService;
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
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@RestController
@RequestMapping
@Tag(name = "Carteiras")
public class CarteiraResource {

  private final CarteiraService service;

  public CarteiraResource(CarteiraService service) {
    this.service = service;
  }

  @PostMapping("/carteiras")
  @Operation(summary = "Cria uma carteira para o usuário autenticado")
  @ApiResponses({
    @ApiResponse(responseCode = "201", description = "Carteira criada"),
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
        description = "Corretora não encontrada",
        content = @Content(schema = @Schema(implementation = ErroResponse.class)))
  })
  public ResponseEntity<CarteiraResponse> criar(@Valid @RequestBody CarteiraRequest request) {
    CarteiraResponse response = service.criar(request);
    URI location =
        ServletUriComponentsBuilder.fromCurrentRequest()
            .path("/{id}")
            .buildAndExpand(response.id())
            .toUri();
    return ResponseEntity.created(location).body(response);
  }

  @GetMapping("/carteiras")
  @Operation(summary = "Lista somente as carteiras do usuário autenticado")
  @ApiResponses({
    @ApiResponse(responseCode = "200", description = "Página de carteiras próprias"),
    @ApiResponse(
        responseCode = "400",
        description = "Paginação ou ordenação inválida",
        content = @Content(schema = @Schema(implementation = ErroResponse.class))),
    @ApiResponse(
        responseCode = "401",
        description = "Usuário não autenticado",
        content = @Content(schema = @Schema(implementation = ErroResponse.class)))
  })
  public Page<CarteiraResponse> listar(Pageable pageable) {
    return service.listar(pageable);
  }

  @GetMapping("/carteiras/{id}")
  @Operation(summary = "Consulta uma carteira própria")
  @ApiResponses({
    @ApiResponse(responseCode = "200", description = "Carteira encontrada"),
    @ApiResponse(
        responseCode = "401",
        description = "Usuário não autenticado",
        content = @Content(schema = @Schema(implementation = ErroResponse.class))),
    @ApiResponse(
        responseCode = "404",
        description = "Carteira não encontrada",
        content = @Content(schema = @Schema(implementation = ErroResponse.class)))
  })
  public CarteiraResponse buscarPorId(@PathVariable Long id) {
    return service.buscarPorId(id);
  }

  @PutMapping("/carteiras/{id}")
  @Operation(summary = "Atualiza somente o nome da carteira")
  @ApiResponses({
    @ApiResponse(responseCode = "200", description = "Carteira atualizada"),
    @ApiResponse(responseCode = "400", description = "Dados inválidos", content = @Content(schema = @Schema(implementation = ErroResponse.class))),
    @ApiResponse(responseCode = "404", description = "Carteira não encontrada", content = @Content(schema = @Schema(implementation = ErroResponse.class)))
  })
  public CarteiraResponse atualizar(@PathVariable Long id, @Valid @RequestBody AtualizarCarteiraRequest request) {
    return service.atualizar(id, request);
  }

  @DeleteMapping("/carteiras/{id}")
  @Operation(summary = "Exclui carteira sem vínculos")
  @ApiResponses({
    @ApiResponse(responseCode = "204", description = "Carteira excluída"),
    @ApiResponse(responseCode = "404", description = "Carteira não encontrada", content = @Content(schema = @Schema(implementation = ErroResponse.class))),
    @ApiResponse(responseCode = "409", description = "Carteira possui vínculos", content = @Content(schema = @Schema(implementation = ErroResponse.class)))
  })
  public ResponseEntity<Void> excluir(@PathVariable Long id) {
    service.excluir(id);
    return ResponseEntity.noContent().build();
  }

  @GetMapping("/usuarios/{usuarioId}/carteiras")
  @Operation(summary = "Lista carteiras do usuário autenticado")
  @ApiResponses({
    @ApiResponse(responseCode = "200", description = "Página de carteiras próprias"),
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
        description = "Usuário não encontrado",
        content = @Content(schema = @Schema(implementation = ErroResponse.class)))
  })
  public Page<CarteiraResponse> listarPorUsuario(@PathVariable Long usuarioId, Pageable pageable) {
    return service.listarPorUsuario(usuarioId, pageable);
  }

  @GetMapping("/carteiras/{id}/acoes")
  @Operation(summary = "Lista posições da carteira própria")
  @ApiResponses({
    @ApiResponse(responseCode = "200", description = "Página de posições ativas"),
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
        content = @Content(schema = @Schema(implementation = ErroResponse.class))),
    @ApiResponse(
        responseCode = "503",
        description = "Câmbio externo indisponível",
        content = @Content(schema = @Schema(implementation = ErroResponse.class)))
  })
  public Page<PosicaoAcaoResponse> listarPosicoesAtivas(@PathVariable Long id, Pageable pageable) {
    return service.listarPosicoesAtivas(id, pageable);
  }

  @GetMapping("/carteiras/{id}/resumo")
  @Operation(summary = "Consulta o resumo da carteira própria")
  @ApiResponses({
    @ApiResponse(responseCode = "200", description = "Resumo por moeda"),
    @ApiResponse(
        responseCode = "401",
        description = "Usuário não autenticado",
        content = @Content(schema = @Schema(implementation = ErroResponse.class))),
    @ApiResponse(
        responseCode = "404",
        description = "Carteira não encontrada",
        content = @Content(schema = @Schema(implementation = ErroResponse.class))),
    @ApiResponse(
        responseCode = "503",
        description = "Câmbio externo indisponível",
        content = @Content(schema = @Schema(implementation = ErroResponse.class)))
  })
  public CarteiraResumoResponse resumo(@PathVariable Long id) {
    return service.resumo(id);
  }
}
