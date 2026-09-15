package com.carteiraacoesbackend.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.carteiraacoesbackend.clients.PtaxClient;
import com.carteiraacoesbackend.domains.enums.Mercado;
import com.carteiraacoesbackend.dto.AcaoRequest;
import com.carteiraacoesbackend.dto.AcaoResponse;
import com.carteiraacoesbackend.dto.AtualizacaoCotacoesResponse;
import com.carteiraacoesbackend.dto.integrations.PtaxQuoteResponse;
import com.carteiraacoesbackend.exceptions.ApiException;
import com.carteiraacoesbackend.integrations.Cotacao;
import com.carteiraacoesbackend.integrations.CotacaoAdapter;
import com.carteiraacoesbackend.mappers.AcaoMapper;
import com.carteiraacoesbackend.mappers.HistoricoCotacaoMapper;
import com.carteiraacoesbackend.repositories.AcaoRepository;
import com.carteiraacoesbackend.repositories.HistoricoCotacaoRepository;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.test.context.ActiveProfiles;

@DataJpaTest(properties = "spring.jpa.hibernate.ddl-auto=validate")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
class AcaoServiceCotacaoTest {

  @Autowired private AcaoRepository repository;
  @Autowired private HistoricoCotacaoRepository historicos;

  private StubCotacaoAdapter cotacaoAdapter;
  private StubPtaxClient ptaxClient;
  private AcaoService service;

  @BeforeEach
  void setUp() {
    cotacaoAdapter = new StubCotacaoAdapter();
    ptaxClient = new StubPtaxClient();
    CotacaoAtualizacaoService atualizacaoService =
        new CotacaoAtualizacaoService(repository, historicos, cotacaoAdapter);
    service =
        new AcaoService(
            repository,
            historicos,
            cotacaoAdapter,
            atualizacaoService,
            new AcaoMapper(),
            new CurrencyConversionService(ptaxClient),
            new HistoricoCotacaoMapper());
  }

  @Test
  void updatesQuoteWithDecimalPriceAndUtcTimestamp() {
    cotacaoAdapter.cotacao =
        new Cotacao(
            new BigDecimal("10.0000"),
            OffsetDateTime.parse("2026-08-22T13:00:00Z"),
            "Petrobras S.A.");
    AcaoResponse acao = criarAcao("PETR4");
    cotacaoAdapter.cotacao =
        new Cotacao(new BigDecimal("15.1234"), OffsetDateTime.parse("2026-08-23T10:00:00-03:00"));

    AcaoResponse updated = service.atualizarCotacao(acao.id());

    assertEquals(new BigDecimal("15.1234"), updated.cotacaoAtual());
    assertEquals(OffsetDateTime.parse("2026-08-23T13:00:00Z"), updated.dataHoraCotacao());
    assertEquals("Petrobras S.A.", updated.nomeEmpresa());
    assertEquals(
        2,
        historicos
            .findByAcaoId(acao.id(), org.springframework.data.domain.Pageable.unpaged())
            .getTotalElements());
  }

  @Test
  void persistsInitialQuoteAndHistoryWithUtcTimestamp() {
    OffsetDateTime timestamp = OffsetDateTime.parse("2026-08-23T10:00:00-03:00");
    cotacaoAdapter.cotacao = new Cotacao(new BigDecimal("15.1234"), timestamp, "Petrobras S.A.");

    AcaoResponse acao = criarAcao("PETR4");

    assertEquals(new BigDecimal("15.1234"), acao.cotacaoAtual());
    assertEquals(OffsetDateTime.parse("2026-08-23T13:00:00Z"), acao.dataHoraCotacao());
    var historico =
        historicos.findByAcaoId(acao.id(), org.springframework.data.domain.Pageable.unpaged());
    assertEquals(1, historico.getTotalElements());
    assertEquals(
        OffsetDateTime.parse("2026-08-23T13:00:00Z"),
        historico.getContent().getFirst().getDataHora());
  }

  @Test
  void createsStockDirectlyAndRejectsDuplicateTicker() {
    cotacaoAdapter.cotacao = cotacao("15.1234", "Petrobras S.A.");

    AcaoResponse created = service.criar(new AcaoRequest(" petr4 ", Mercado.BRASIL));

    assertEquals("PETR4", created.ticker());
    assertEquals(Mercado.BRASIL, created.mercado());
    assertEquals(
        1,
        historicos
            .findByAcaoId(created.id(), org.springframework.data.domain.Pageable.unpaged())
            .getTotalElements());
    ApiException exception =
        assertThrows(
            ApiException.class, () -> service.criar(new AcaoRequest("PETR4", Mercado.BRASIL)));
    assertEquals("TICKER_DUPLICADO", exception.getCode());
  }

  @Test
  void enrichesOnlyUsStocksWhenPtaxRateIsAvailable() {
    cotacaoAdapter.cotacao = cotacao("10.00", "Empresa EUA");
    ptaxClient.resposta =
        new PtaxQuoteResponse(
            List.of(new PtaxQuoteResponse.PtaxQuote("5.20", "2026-09-04 13:00:00")));

    AcaoResponse americana = service.criar(new AcaoRequest("AAPL", Mercado.EUA));

    assertEquals(new BigDecimal("52.0000"), americana.cotacaoEmBrl());
    assertEquals(new BigDecimal("5.20"), americana.taxaUsdBrl());
    assertEquals(OffsetDateTime.parse("2026-09-04T16:00:00Z"), americana.dataHoraTaxaUsdBrl());

    cotacaoAdapter.cotacao = cotacao("20.00", "Empresa Brasil");
    AcaoResponse brasileira = service.criar(new AcaoRequest("PETR4", Mercado.BRASIL));
    assertEquals(null, brasileira.cotacaoEmBrl());
    assertEquals(null, brasileira.taxaUsdBrl());
  }

  @Test
  void keepsUsQuoteAndOmitsConversionFieldsWhenPtaxHasNoRate() {
    cotacaoAdapter.cotacao = cotacao("10.00", "Empresa EUA");

    AcaoResponse americana = service.criar(new AcaoRequest("MSFT", Mercado.EUA));

    assertEquals(new BigDecimal("10.00"), americana.cotacaoAtual());
    assertEquals(null, americana.cotacaoEmBrl());
    assertEquals(null, americana.taxaUsdBrl());
    assertEquals(null, americana.dataHoraTaxaUsdBrl());
  }

  @Test
  void doesNotPersistDirectRegistrationWhenMarketDataIsInvalid() {
    cotacaoAdapter.cotacao =
        new Cotacao(BigDecimal.ZERO, OffsetDateTime.now(ZoneOffset.UTC), "Empresa inválida");

    assertThrows(
        ApiException.class, () -> service.criar(new AcaoRequest("INVALID", Mercado.BRASIL)));

    assertEquals(0, repository.count());
    assertEquals(0, historicos.count());
  }

  @Test
  void preservesStoredQuoteForEveryExternalFailureCategory() {
    verificarFalha(
        "VALE3",
        ApiException.notFound("TICKER_NAO_ENCONTRADO", "não encontrado"),
        404,
        "TICKER_NAO_ENCONTRADO");
    verificarFalha(
        "ITUB4",
        ApiException.external(
            org.springframework.http.HttpStatus.BAD_GATEWAY,
            "EXTERNAL_API_AUTHENTICATION",
            "sem credencial"),
        502,
        "EXTERNAL_API_AUTHENTICATION");
    verificarFalha(
        "BBAS3",
        ApiException.external(
            org.springframework.http.HttpStatus.TOO_MANY_REQUESTS,
            "EXTERNAL_API_RATE_LIMIT",
            "limite"),
        429,
        "EXTERNAL_API_RATE_LIMIT");
    verificarFalha(
        "ABEV3",
        ApiException.external(
            org.springframework.http.HttpStatus.SERVICE_UNAVAILABLE,
            "EXTERNAL_API_UNAVAILABLE",
            "timeout"),
        503,
        "EXTERNAL_API_UNAVAILABLE");
  }

  @Test
  void rejectsUnknownStockBeforeCallingTheProvider() {
    ApiException exception =
        assertThrows(ApiException.class, () -> service.atualizarCotacao(99999L));
    assertEquals("ACAO_NAO_ENCONTRADA", exception.getCode());
  }

  @Test
  void returnsChronologicalHistoryAndContinuesGlobalRefreshAfterFailure() {
    cotacaoAdapter.cotacao = cotacao("10.0000", "Empresa A");
    AcaoResponse primeira = criarAcao("AAAA3");
    cotacaoAdapter.cotacao = cotacao("20.0000", "Empresa B");
    AcaoResponse segunda = criarAcao("BBBB3");
    cotacaoAdapter.cotacao = cotacao("21.0000", "Empresa B");
    service.atualizarCotacao(segunda.id());

    assertEquals(
        2,
        service
            .historico(
                segunda.id(),
                null,
                null,
                org.springframework.data.domain.PageRequest.of(
                    0, 100, org.springframework.data.domain.Sort.by("dataHora").ascending()))
            .getTotalElements());

    cotacaoAdapter.failures.put(
        primeira.ticker(),
        ApiException.external(
            org.springframework.http.HttpStatus.SERVICE_UNAVAILABLE,
            "EXTERNAL_API_UNAVAILABLE",
            "indisponível"));
    cotacaoAdapter.cotacao =
        new Cotacao(
            new BigDecimal("22.0000"),
            OffsetDateTime.now(ZoneOffset.UTC).plusMinutes(1),
            "Empresa B");
    long totalAntes = repository.count();
    AtualizacaoCotacoesResponse response = service.atualizarTodasCotacoes();

    assertEquals(totalAntes, response.total());
    assertEquals(totalAntes - 1, response.atualizadas());
    assertEquals(1, response.falhas());
    assertEquals(new BigDecimal("10.0000"), service.buscarPorId(primeira.id()).cotacaoAtual());
    assertEquals(new BigDecimal("22.0000"), service.buscarPorId(segunda.id()).cotacaoAtual());
    assertEquals(
        1,
        historicos
            .findByAcaoId(primeira.id(), org.springframework.data.domain.Pageable.unpaged())
            .getTotalElements());
    assertEquals(
        3,
        historicos
            .findByAcaoId(segunda.id(), org.springframework.data.domain.Pageable.unpaged())
            .getTotalElements());
  }

  @Test
  void historyUsesInclusiveUtcFiltersDeterministicPaginationAndEmptyResults() {
    OffsetDateTime firstAt = OffsetDateTime.parse("2026-09-01T10:00:00-03:00");
    OffsetDateTime secondAt = OffsetDateTime.parse("2026-09-02T13:00:00Z");
    OffsetDateTime thirdAt = OffsetDateTime.parse("2026-09-03T13:00:00Z");
    cotacaoAdapter.cotacao = new Cotacao(new BigDecimal("10.1111"), firstAt, "Empresa");
    AcaoResponse acao = criarAcao("HIST3");
    cotacaoAdapter.cotacao = new Cotacao(new BigDecimal("11.2222"), secondAt, "Empresa");
    service.atualizarCotacao(acao.id());
    cotacaoAdapter.cotacao = new Cotacao(new BigDecimal("12.3333"), thirdAt, "Empresa");
    service.atualizarCotacao(acao.id());

    var firstPage =
        service.historico(
            acao.id(), secondAt, thirdAt, org.springframework.data.domain.PageRequest.of(0, 1));
    var secondPage =
        service.historico(
            acao.id(), secondAt, thirdAt, org.springframework.data.domain.PageRequest.of(1, 1));

    assertEquals(2, firstPage.getTotalElements());
    assertEquals(new BigDecimal("11.2222"), firstPage.getContent().getFirst().cotacao());
    assertEquals(new BigDecimal("12.3333"), secondPage.getContent().getFirst().cotacao());
    assertEquals(secondAt, firstPage.getContent().getFirst().dataHora());
    assertEquals(thirdAt, secondPage.getContent().getFirst().dataHora());
    assertEquals(
        0,
        service
            .historico(
                acao.id(),
                thirdAt.plusSeconds(1),
                null,
                org.springframework.data.domain.Pageable.unpaged())
            .getTotalElements());
  }

  @Test
  void ignoresExactDuplicateAndRejectsStaleOrConflictingQuotesWithoutChangingState() {
    OffsetDateTime storedAt = OffsetDateTime.parse("2026-09-08T20:30:00Z");
    cotacaoAdapter.cotacao = new Cotacao(new BigDecimal("30.0000"), storedAt, "Empresa");
    AcaoResponse acao = criarAcao("MONO3");

    service.atualizarCotacao(acao.id());
    assertEquals(
        1,
        historicos
            .findByAcaoId(acao.id(), org.springframework.data.domain.Pageable.unpaged())
            .getTotalElements());

    cotacaoAdapter.cotacao =
        new Cotacao(new BigDecimal("29.0000"), storedAt.minusMinutes(1), "Empresa");
    ApiException stale =
        assertThrows(ApiException.class, () -> service.atualizarCotacao(acao.id()));
    assertEquals("EXTERNAL_API_STALE_RESPONSE", stale.getCode());

    cotacaoAdapter.cotacao = new Cotacao(new BigDecimal("31.0000"), storedAt, "Empresa");
    ApiException conflicting =
        assertThrows(ApiException.class, () -> service.atualizarCotacao(acao.id()));
    assertEquals("EXTERNAL_API_INVALID_RESPONSE", conflicting.getCode());

    AcaoResponse preserved = service.buscarPorId(acao.id());
    assertEquals(new BigDecimal("30.0000"), preserved.cotacaoAtual());
    assertEquals(storedAt, preserved.dataHoraCotacao());
    assertEquals(
        1,
        historicos
            .findByAcaoId(acao.id(), org.springframework.data.domain.Pageable.unpaged())
            .getTotalElements());
  }

  @Test
  void rejectsMalformedTickerAndReversedHistoryPeriodAsBadRequests() {
    ApiException tickerError =
        assertThrows(
            ApiException.class,
            () -> service.criar(new AcaoRequest("PETR4 inválido", Mercado.BRASIL)));
    assertEquals(400, tickerError.getStatus().value());
    assertEquals(0, repository.count());

    cotacaoAdapter.cotacao = cotacao("10.0000", "Petrobras");
    AcaoResponse acao = criarAcao("PETR4");
    ApiException periodError =
        assertThrows(
            ApiException.class,
            () ->
                service.historico(
                    acao.id(),
                    OffsetDateTime.parse("2026-09-02T00:00:00Z"),
                    OffsetDateTime.parse("2026-09-01T00:00:00Z"),
                    org.springframework.data.domain.Pageable.unpaged()));
    assertEquals(400, periodError.getStatus().value());
    assertEquals("PERIODO_INVALIDO", periodError.getCode());
  }

  private void verificarFalha(
      String ticker, ApiException failure, int expectedStatus, String expectedCode) {
    cotacaoAdapter.cotacao = cotacao("10.0000", "Empresa importada");
    AcaoResponse acao = criarAcao(ticker);
    cotacaoAdapter.failure = failure;

    ApiException exception =
        assertThrows(ApiException.class, () -> service.atualizarCotacao(acao.id()));

    assertEquals(expectedStatus, exception.getStatus().value());
    assertEquals(expectedCode, exception.getCode());
    assertEquals(new BigDecimal("10.0000"), service.buscarPorId(acao.id()).cotacaoAtual());
    cotacaoAdapter.failure = null;
  }

  private Cotacao cotacao(String preco, String nomeEmpresa) {
    return new Cotacao(new BigDecimal(preco), OffsetDateTime.now(ZoneOffset.UTC), nomeEmpresa);
  }

  private AcaoResponse criarAcao(String ticker) {
    return new AcaoMapper().toResponse(service.resolverOuCriar(ticker, Mercado.BRASIL));
  }

  private static final class StubCotacaoAdapter implements CotacaoAdapter {
    private Cotacao cotacao;
    private RuntimeException failure;
    private final Map<String, RuntimeException> failures = new HashMap<>();

    @Override
    public Cotacao consultar(String ticker, Mercado mercado) {
      if (failure != null) {
        throw failure;
      }
      if (failures.containsKey(ticker)) {
        throw failures.get(ticker);
      }
      return cotacao;
    }
  }

  private static final class StubPtaxClient implements PtaxClient {
    private PtaxQuoteResponse resposta;

    @Override
    public PtaxQuoteResponse consultarCotacoes(
        String dataInicial, String dataFinal, int limite, String formato) {
      return resposta;
    }
  }
}
