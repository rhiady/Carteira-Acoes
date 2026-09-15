package com.carteiraacoesbackend.services;

import com.carteiraacoesbackend.domains.Carteira;
import com.carteiraacoesbackend.domains.CarteiraAcao;
import com.carteiraacoesbackend.domains.Corretora;
import com.carteiraacoesbackend.domains.Operacao;
import com.carteiraacoesbackend.domains.Usuario;
import com.carteiraacoesbackend.domains.enums.Mercado;
import com.carteiraacoesbackend.domains.enums.Moeda;
import com.carteiraacoesbackend.dto.CarteiraRequest;
import com.carteiraacoesbackend.dto.AtualizarCarteiraRequest;
import com.carteiraacoesbackend.dto.CarteiraResponse;
import com.carteiraacoesbackend.dto.CarteiraResumoResponse;
import com.carteiraacoesbackend.dto.PosicaoAcaoResponse;
import com.carteiraacoesbackend.dto.ResumoPorMoedaResponse;
import com.carteiraacoesbackend.exceptions.ApiException;
import com.carteiraacoesbackend.mappers.CarteiraMapper;
import com.carteiraacoesbackend.repositories.CarteiraAcaoRepository;
import com.carteiraacoesbackend.repositories.CarteiraRepository;
import com.carteiraacoesbackend.repositories.CorretoraRepository;
import com.carteiraacoesbackend.repositories.OperacaoRepository;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class CarteiraService {
  private static final Map<String, String> CARTEIRA_SORT_FIELDS =
      Map.of(
          "id", "id",
          "nome", "nome",
          "createdAt", "createdAt",
          "updatedAt", "updatedAt");
  private static final Sort CARTEIRA_SORT_PADRAO = Sort.by("id").ascending();
  private static final Map<String, String> POSICAO_SORT_FIELDS =
      Map.of("id", "id", "quantidade", "quantidade", "ticker", "acao.ticker");
  private static final Sort POSICAO_SORT_PADRAO = Sort.by(Sort.Direction.DESC, "id");

  private final CarteiraRepository repository;
  private final CarteiraAcaoRepository carteiraAcaoRepository;
  private final CorretoraRepository corretoraRepository;
  private final OperacaoRepository operacaoRepository;
  private final UsuarioService usuarioService;
  private final CarteiraMapper mapper;
  private final PosicaoFinanceiraCalculator calculator;
  private final CurrencyConversionService currencyConversionService;
  private final CurrentUserService currentUser;

  @Autowired
  public CarteiraService(
      CarteiraRepository repository,
      CarteiraAcaoRepository carteiraAcaoRepository,
      CorretoraRepository corretoraRepository,
      OperacaoRepository operacaoRepository,
      UsuarioService usuarioService,
      CarteiraMapper mapper,
      PosicaoFinanceiraCalculator calculator,
      CurrencyConversionService currencyConversionService,
      CurrentUserService currentUser) {
    this.repository = repository;
    this.carteiraAcaoRepository = carteiraAcaoRepository;
    this.corretoraRepository = corretoraRepository;
    this.operacaoRepository = operacaoRepository;
    this.usuarioService = usuarioService;
    this.mapper = mapper;
    this.calculator = calculator;
    this.currencyConversionService = currencyConversionService;
    this.currentUser = currentUser;
  }

  public CarteiraService(
      CarteiraRepository repository,
      CarteiraAcaoRepository carteiraAcaoRepository,
      CorretoraRepository corretoraRepository,
      OperacaoRepository operacaoRepository,
      UsuarioService usuarioService,
      CarteiraMapper mapper,
      PosicaoFinanceiraCalculator calculator,
      CurrencyConversionService currencyConversionService) {
    this(
        repository,
        carteiraAcaoRepository,
        corretoraRepository,
        operacaoRepository,
        usuarioService,
        mapper,
        calculator,
        currencyConversionService,
        null);
  }

  @Transactional
  public CarteiraResponse criar(CarteiraRequest request) {
    Usuario usuario =
        currentUser == null || !currentUser.isWebRequest()
            ? usuarioService.obterEntidade(request.usuarioId())
            : currentUser.required();
    Corretora corretora =
        corretoraRepository
            .findById(request.corretoraId())
            .orElseThrow(
                () ->
                    ApiException.notFound("CORRETORA_NAO_ENCONTRADA", "Corretora não encontrada."));
    Carteira carteira = new Carteira();
    carteira.setNome(request.nome().trim());
    carteira.setUsuario(usuario);
    carteira.setCorretora(corretora);
    return mapper.toResponse(repository.save(carteira));
  }

  public CarteiraResponse buscarPorId(Long id) {
    Carteira carteira = obterEntidade(id);
    validarOwnership(carteira);
    return mapper.toResponse(carteira);
  }

  @Transactional
  public CarteiraResponse atualizar(Long id, AtualizarCarteiraRequest request) {
    Carteira carteira = obterEntidade(id);
    validarOwnership(carteira);
    carteira.setNome(request.nome().trim());
    return mapper.toResponse(repository.save(carteira));
  }

  @Transactional
  public void excluir(Long id) {
    Carteira carteira = obterEntidade(id);
    validarOwnership(carteira);
    if (operacaoRepository.existsByCarteiraId(id) || carteiraAcaoRepository.existsByCarteiraId(id)) {
      throw ApiException.conflict(
          "CARTEIRA_VINCULADA",
          "Não é possível excluir a carteira porque ela possui operações ou posições vinculadas.");
    }
    repository.delete(carteira);
  }

  public Page<CarteiraResponse> listar(Pageable pageable) {
    pageable = PageableSupport.translate(pageable, CARTEIRA_SORT_FIELDS, CARTEIRA_SORT_PADRAO);
    if (currentUser == null || !currentUser.isWebRequest())
      return repository.findAll(pageable).map(mapper::toResponse);
    return repository
        .findByUsuarioId(currentUser.required().getId(), pageable)
        .map(mapper::toResponse);
  }

  public Page<CarteiraResponse> listarPorUsuario(Long usuarioId, Pageable pageable) {
    pageable = PageableSupport.translate(pageable, CARTEIRA_SORT_FIELDS, CARTEIRA_SORT_PADRAO);
    if (currentUser != null
        && currentUser.isWebRequest()
        && !currentUser.required().getId().equals(usuarioId)) {
      throw ApiException.notFound("USUARIO_NAO_ENCONTRADO", "Usuário não encontrado.");
    }
    usuarioService.obterEntidade(usuarioId);
    return repository.findByUsuarioId(usuarioId, pageable).map(mapper::toResponse);
  }

  public Page<PosicaoAcaoResponse> listarPosicoesAtivas(Long carteiraId, Pageable pageable) {
    validarOwnership(obterEntidade(carteiraId));
    Pageable pageableTraduzido = traduzirPageablePosicoes(pageable);
    Page<CarteiraAcao> pagina =
        carteiraAcaoRepository.findByCarteiraIdAndQuantidadeGreaterThan(
            carteiraId, BigDecimal.ZERO, pageableTraduzido);
    Map<Long, List<Operacao>> operacoesPorAcao = operacoesPorAcao(carteiraId, pagina.getContent());
    java.util.Optional<TaxaUsdBrl> taxa = consultarTaxaSeNecessario(pagina.getContent());
    List<PosicaoAcaoResponse> conteudo =
        pagina.getContent().stream()
            .map(
                posicao ->
                    paraResposta(
                        posicao,
                        operacoesPorAcao.getOrDefault(posicao.getAcao().getId(), List.of()),
                        taxa))
            .toList();
    return new PageImpl<>(conteudo, pageable, pagina.getTotalElements());
  }

  private Pageable traduzirPageablePosicoes(Pageable pageable) {
    return PageableSupport.translate(pageable, POSICAO_SORT_FIELDS, POSICAO_SORT_PADRAO);
  }

  public CarteiraResumoResponse resumo(Long carteiraId) {
    Carteira carteira = obterEntidade(carteiraId);
    validarOwnership(carteira);
    List<CarteiraAcao> posicoes =
        carteiraAcaoRepository.findByCarteiraIdAndQuantidadeGreaterThan(
            carteiraId, BigDecimal.ZERO);
    Map<Long, List<Operacao>> operacoesPorAcao = operacoesPorAcao(carteiraId, posicoes);
    java.util.Optional<TaxaUsdBrl> taxa = consultarTaxaSeNecessario(posicoes);
    Map<Moeda, List<PosicaoAcaoResponse>> porMoeda =
        posicoes.stream()
            .map(
                posicao ->
                    paraResposta(
                        posicao,
                        operacoesPorAcao.getOrDefault(posicao.getAcao().getId(), List.of()),
                        taxa))
            .collect(
                Collectors.groupingBy(
                    PosicaoAcaoResponse::moeda,
                    () -> new EnumMap<>(Moeda.class),
                    Collectors.toList()));
    List<ResumoPorMoedaResponse> resumos = new ArrayList<>();
    porMoeda.forEach(
        (moeda, respostas) ->
            resumos.add(
                new ResumoPorMoedaResponse(
                    moeda,
                    somarSomenteSeCompleto(respostas, PosicaoAcaoResponse::valorAtual),
                    somarSomenteSeCompleto(respostas, PosicaoAcaoResponse::lucroPrejuizo),
                    somarSomenteSeCompleto(respostas, PosicaoAcaoResponse::valorInvestidoAtual),
                    somarSomenteSeCompleto(respostas, PosicaoAcaoResponse::valorTotalComprado),
                    somarSomenteSeCompleto(respostas, PosicaoAcaoResponse::valorTotalVendido),
                    somarSomenteSeCompleto(respostas, PosicaoAcaoResponse::lucroPrejuizoRealizado),
                    somarSomenteSeCompleto(respostas, PosicaoAcaoResponse::lucroPrejuizoNaoRealizado))));
    resumos.sort(Comparator.comparing(resumo -> resumo.moeda().name()));
    return new CarteiraResumoResponse(
        carteira.getId(), carteira.getNome(), posicoes.size(), resumos);
  }

  private Map<Long, List<Operacao>> operacoesPorAcao(Long carteiraId, List<CarteiraAcao> posicoes) {
    if (posicoes.isEmpty()) {
      return Map.of();
    }
    List<Long> acoesIds = posicoes.stream().map(posicao -> posicao.getAcao().getId()).toList();
    return operacaoRepository
        .findByCarteiraIdAndAcaoIdInOrderByDataHoraAscIdAsc(carteiraId, acoesIds)
        .stream()
        .collect(Collectors.groupingBy(operacao -> operacao.getAcao().getId()));
  }

  private PosicaoAcaoResponse paraResposta(
      CarteiraAcao posicao, List<Operacao> operacoes, java.util.Optional<TaxaUsdBrl> taxa) {
    PosicaoFinanceiraCalculator.Resultado resultado =
        calculator.calcular(operacoes, posicao.getAcao().getCotacaoAtual());
    boolean converter = posicao.getAcao().getMercado() == Mercado.EUA && taxa.isPresent();
    boolean cotacaoDisponivel = posicao.getAcao().getCotacaoAtual() != null;
    return new PosicaoAcaoResponse(
        posicao.getCarteira().getId(),
        posicao.getAcao().getId(),
        posicao.getAcao().getTicker(),
        posicao.getAcao().getNomeEmpresa(),
        posicao.getAcao().getMercado(),
        posicao.getAcao().getMoeda(),
        posicao.getQuantidade(),
        posicao.getAcao().getCotacaoAtual(),
        posicao.getAcao().getDataHoraCotacao(),
        resultado.valorAtual(),
        resultado.precoMedio(),
        converter && cotacaoDisponivel
            ? posicao.getAcao().getCotacaoAtual().multiply(taxa.get().valor())
            : null,
        converter && resultado.valorAtual() != null
            ? resultado.valorAtual().multiply(taxa.get().valor())
            : null,
        converter && cotacaoDisponivel ? taxa.get().valor() : null,
        converter && cotacaoDisponivel ? taxa.get().dataHoraReferencia() : null,
        resultado.precoMedioVenda(),
        resultado.lucroPrejuizoRealizado(),
        resultado.lucroPrejuizoNaoRealizado(),
        resultado.lucroPrejuizo(),
        resultado.valorInvestidoAtual(),
        resultado.valorTotalComprado(),
        resultado.valorTotalVendido());
  }

  private java.util.Optional<TaxaUsdBrl> consultarTaxaSeNecessario(List<CarteiraAcao> posicoes) {
    return posicoes.stream()
            .anyMatch(
                posicao ->
                    posicao.getAcao().getMercado() == Mercado.EUA
                        && posicao.getAcao().getCotacaoAtual() != null)
        ? currencyConversionService.consultarTaxaAtual()
        : java.util.Optional.empty();
  }

  private BigDecimal somarSomenteSeCompleto(
      List<PosicaoAcaoResponse> respostas,
      java.util.function.Function<PosicaoAcaoResponse, BigDecimal> campo) {
    if (respostas.stream().map(campo).anyMatch(java.util.Objects::isNull)) {
      return null;
    }
    return respostas.stream().map(campo).reduce(BigDecimal.ZERO, BigDecimal::add);
  }

  public Carteira obterEntidade(Long id) {
    return repository
        .findById(id)
        .orElseThrow(
            () -> ApiException.notFound("CARTEIRA_NAO_ENCONTRADA", "Carteira não encontrada."));
  }

  private void validarOwnership(Carteira carteira) {
    if (currentUser != null && currentUser.isWebRequest()) currentUser.requireOwnership(carteira);
  }
}
