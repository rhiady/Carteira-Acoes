package com.carteiraacoesbackend.services;

import com.carteiraacoesbackend.domains.Acao;
import com.carteiraacoesbackend.domains.enums.Moeda;
import com.carteiraacoesbackend.dto.AcaoRequest;
import com.carteiraacoesbackend.dto.AcaoResponse;
import com.carteiraacoesbackend.dto.AtualizacaoCotacoesResponse;
import com.carteiraacoesbackend.dto.HistoricoCotacaoResponse;
import com.carteiraacoesbackend.exceptions.ApiException;
import com.carteiraacoesbackend.integrations.Cotacao;
import com.carteiraacoesbackend.integrations.CotacaoAdapter;
import com.carteiraacoesbackend.mappers.AcaoMapper;
import com.carteiraacoesbackend.repositories.AcaoRepository;
import com.carteiraacoesbackend.repositories.CarteiraAcaoRepository;
import com.carteiraacoesbackend.repositories.HistoricoCotacaoRepository;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class AcaoService {
  private static final Logger LOG = LoggerFactory.getLogger(AcaoService.class);
  private static final Map<String, String> ACAO_SORT_FIELDS =
      Map.ofEntries(
          Map.entry("id", "id"),
          Map.entry("ticker", "ticker"),
          Map.entry("nomeEmpresa", "nomeEmpresa"),
          Map.entry("mercado", "mercado"),
          Map.entry("moeda", "moeda"),
          Map.entry("cotacaoAtual", "cotacaoAtual"),
          Map.entry("dataHoraCotacao", "dataHoraCotacao"),
          Map.entry("createdAt", "createdAt"),
          Map.entry("updatedAt", "updatedAt"));
  private static final Map<String, String> HISTORICO_SORT_FIELDS =
      Map.of("id", "id", "cotacao", "cotacao", "dataHora", "dataHora");

  private final AcaoRepository repository;
  private final HistoricoCotacaoRepository historicos;
  private final CarteiraAcaoRepository posicoes;
  private final CotacaoAdapter cotacaoAdapter;
  private final CotacaoAtualizacaoService atualizacaoService;
  private final AcaoMapper mapper;
  private final CurrencyConversionService currencyConversionService;
  private final com.carteiraacoesbackend.mappers.HistoricoCotacaoMapper historicoMapper;

  @Autowired
  public AcaoService(
      AcaoRepository repository,
      HistoricoCotacaoRepository historicos,
      CarteiraAcaoRepository posicoes,
      CotacaoAdapter cotacaoAdapter,
      CotacaoAtualizacaoService atualizacaoService,
      AcaoMapper mapper,
      CurrencyConversionService currencyConversionService,
      com.carteiraacoesbackend.mappers.HistoricoCotacaoMapper historicoMapper) {
    this.repository = repository;
    this.historicos = historicos;
    this.posicoes = posicoes;
    this.cotacaoAdapter = cotacaoAdapter;
    this.atualizacaoService = atualizacaoService;
    this.mapper = mapper;
    this.currencyConversionService = currencyConversionService;
    this.historicoMapper = historicoMapper;
  }

  public AcaoService(
      AcaoRepository repository,
      HistoricoCotacaoRepository historicos,
      CotacaoAdapter cotacaoAdapter,
      CotacaoAtualizacaoService atualizacaoService,
      AcaoMapper mapper,
      CurrencyConversionService currencyConversionService,
      com.carteiraacoesbackend.mappers.HistoricoCotacaoMapper historicoMapper) {
    this(
        repository,
        historicos,
        null,
        cotacaoAdapter,
        atualizacaoService,
        mapper,
        currencyConversionService,
        historicoMapper);
  }

  @Transactional
  public Acao resolverOuCriar(
      String ticker, com.carteiraacoesbackend.domains.enums.Mercado mercado) {
    String tickerNormalizado = normalizarTicker(ticker, mercado);
    return repository
        .findByTickerIgnoreCase(tickerNormalizado)
        .orElseGet(() -> criarEntidade(tickerNormalizado, mercado));
  }

  @Transactional
  public AcaoResponse criar(AcaoRequest request) {
    String ticker = normalizarTicker(request.ticker(), request.mercado());
    return paraResposta(criarEntidade(ticker, request.mercado()));
  }

  private Acao criarEntidade(
      String tickerInformado, com.carteiraacoesbackend.domains.enums.Mercado mercado) {
    String ticker = tickerInformado.trim().toUpperCase(Locale.ROOT);
    if (repository.existsByTickerIgnoreCase(ticker)) {
      throw ApiException.conflict("TICKER_DUPLICADO", "Já existe uma ação com este ticker.");
    }
    Cotacao cotacao = cotacaoAdapter.consultar(ticker, mercado);
    validarDadosCadastro(cotacao);
    Acao acao = new Acao();
    acao.setTicker(ticker);
    acao.setNomeEmpresa(cotacao.nomeEmpresa().trim());
    acao.setMercado(mercado);
    acao.setMoeda(
        mercado == com.carteiraacoesbackend.domains.enums.Mercado.BRASIL ? Moeda.BRL : Moeda.USD);
    acao.setCotacaoAtual(cotacao.preco());
    acao.setDataHoraCotacao(cotacao.dataHora().withOffsetSameInstant(ZoneOffset.UTC));
    // Força a verificação da unicidade do ticker antes de a compra criar posição ou operação.
    Acao salva = repository.saveAndFlush(acao);
    atualizacaoService.registrarHistorico(salva, cotacao);
    return salva;
  }

  private String normalizarTicker(
      String ticker, com.carteiraacoesbackend.domains.enums.Mercado mercado) {
    if (ticker == null || ticker.isBlank() || mercado == null) {
      throw ApiException.badRequest("IDENTIFICACAO_ACAO_INVALIDA", "Informe ticker e mercado.");
    }
    String normalizado = ticker.trim().toUpperCase(Locale.ROOT);
    if (!normalizado.matches(AcaoRequest.TICKER_PATTERN)) {
      throw ApiException.badRequest(
          "TICKER_INVALIDO", "Ticker deve conter de 1 a 15 letras, números, pontos ou hífens.");
    }
    return normalizado;
  }

  public AcaoResponse buscarPorId(Long id) {
    return paraResposta(obterEntidade(id));
  }

  @Transactional
  public void excluir(Long id) {
    Acao acao = obterEntidade(id);
    if ((posicoes != null && posicoes.existsByAcaoId(id))
        || historicos.existsByAcaoId(id)
        || repository.existsOperacoesById(id)) {
      throw ApiException.conflict(
          "ACAO_VINCULADA",
          "Não é possível excluir a ação porque existem operações, posições ou histórico de cotações vinculados.");
    }
    repository.delete(acao);
  }

  public AcaoResponse buscarPorTicker(String ticker) {
    String normalizado =
        normalizarTicker(ticker, com.carteiraacoesbackend.domains.enums.Mercado.BRASIL);
    return paraResposta(
        repository
            .findByTickerIgnoreCase(normalizado)
            .orElseThrow(
                () -> ApiException.notFound("ACAO_NAO_ENCONTRADA", "Ação não encontrada.")));
  }

  public Page<AcaoResponse> listar(Pageable pageable) {
    pageable = PageableSupport.translate(pageable, ACAO_SORT_FIELDS, Sort.by("id").ascending());
    Page<Acao> acoes = repository.findAll(pageable);
    java.util.Optional<TaxaUsdBrl> taxa =
        acoes.stream()
                .anyMatch(
                    acao -> acao.getMercado() == com.carteiraacoesbackend.domains.enums.Mercado.EUA)
            ? currencyConversionService.consultarTaxaAtual()
            : java.util.Optional.empty();
    return acoes.map(
        acao ->
            mapper.toResponse(
                acao,
                acao.getMercado() == com.carteiraacoesbackend.domains.enums.Mercado.EUA
                    ? taxa
                    : java.util.Optional.empty()));
  }

  @Transactional
  public AcaoResponse atualizarCotacao(Long id) {
    return paraResposta(atualizacaoService.atualizar(id));
  }

  private AcaoResponse paraResposta(Acao acao) {
    return mapper.toResponse(
        acao,
        acao.getMercado() == com.carteiraacoesbackend.domains.enums.Mercado.EUA
            ? currencyConversionService.consultarTaxaAtual()
            : java.util.Optional.empty());
  }

  public Page<HistoricoCotacaoResponse> historico(
      Long acaoId, OffsetDateTime inicio, OffsetDateTime fim, Pageable pageable) {
    pageable =
        PageableSupport.translate(
            pageable,
            HISTORICO_SORT_FIELDS,
            Sort.by(Sort.Order.asc("dataHora"), Sort.Order.asc("id")));
    obterEntidade(acaoId);
    if (inicio != null && fim != null && inicio.isAfter(fim)) {
      throw ApiException.badRequest(
          "PERIODO_INVALIDO", "A data inicial deve ser anterior à final.");
    }
    if (inicio != null && fim != null) {
      return historicos
          .findByAcaoIdAndDataHoraGreaterThanEqualAndDataHoraLessThanEqual(
              acaoId, inicio, fim, pageable)
          .map(historicoMapper::toResponse);
    }
    if (inicio != null) {
      return historicos
          .findByAcaoIdAndDataHoraGreaterThanEqual(acaoId, inicio, pageable)
          .map(historicoMapper::toResponse);
    }
    if (fim != null) {
      return historicos
          .findByAcaoIdAndDataHoraLessThanEqual(acaoId, fim, pageable)
          .map(historicoMapper::toResponse);
    }
    return historicos.findByAcaoId(acaoId, pageable).map(historicoMapper::toResponse);
  }

  public AtualizacaoCotacoesResponse atualizarTodasCotacoes() {
    List<Long> ids = repository.findAll().stream().map(Acao::getId).toList();
    int atualizadas = 0;
    for (Long id : ids) {
      try {
        atualizacaoService.atualizar(id);
        atualizadas++;
      } catch (RuntimeException exception) {
        // Cada ação é processada isoladamente; a falha não interrompe o lote.
        LOG.warn(
            "quote_batch_item_failed acaoId={} error={}",
            id,
            exception instanceof ApiException api
                ? api.getCode()
                : exception.getClass().getSimpleName());
      }
    }
    return new AtualizacaoCotacoesResponse(
        ids.size(), atualizadas, ids.size() - atualizadas, OffsetDateTime.now(ZoneOffset.UTC));
  }

  public static void validarCotacao(Cotacao cotacao) {
    if (cotacao == null
        || cotacao.preco() == null
        || cotacao.preco().signum() <= 0
        || cotacao.dataHora() == null) {
      throw ApiException.external(
          org.springframework.http.HttpStatus.BAD_GATEWAY,
          "EXTERNAL_API_INVALID_RESPONSE",
          "O provedor externo retornou uma cotação inválida.");
    }
  }

  private void validarDadosCadastro(Cotacao cotacao) {
    validarCotacao(cotacao);
    if (cotacao.nomeEmpresa() == null || cotacao.nomeEmpresa().isBlank()) {
      throw ApiException.external(
          org.springframework.http.HttpStatus.BAD_GATEWAY,
          "EXTERNAL_API_INVALID_RESPONSE",
          "O provedor externo não retornou o nome da empresa.");
    }
  }

  public Acao obterEntidade(Long id) {
    return repository
        .findById(id)
        .orElseThrow(() -> ApiException.notFound("ACAO_NAO_ENCONTRADA", "Ação não encontrada."));
  }
}
