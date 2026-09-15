package com.carteiraacoesbackend.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import com.carteiraacoesbackend.domains.Acao;
import com.carteiraacoesbackend.domains.Carteira;
import com.carteiraacoesbackend.domains.CarteiraAcao;
import com.carteiraacoesbackend.domains.Corretora;
import com.carteiraacoesbackend.domains.Usuario;
import com.carteiraacoesbackend.domains.enums.Mercado;
import com.carteiraacoesbackend.domains.enums.Moeda;
import com.carteiraacoesbackend.dto.CompraRequest;
import com.carteiraacoesbackend.dto.VendaRequest;
import com.carteiraacoesbackend.exceptions.ApiException;
import com.carteiraacoesbackend.integrations.Cotacao;
import com.carteiraacoesbackend.integrations.CotacaoAdapter;
import com.carteiraacoesbackend.repositories.AcaoRepository;
import com.carteiraacoesbackend.repositories.CarteiraAcaoRepository;
import com.carteiraacoesbackend.repositories.CarteiraRepository;
import com.carteiraacoesbackend.repositories.CorretoraRepository;
import com.carteiraacoesbackend.repositories.HistoricoCotacaoRepository;
import com.carteiraacoesbackend.repositories.OperacaoRepository;
import com.carteiraacoesbackend.repositories.UsuarioRepository;
import jakarta.validation.Validator;
import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

@SpringBootTest
@ActiveProfiles("test")
class OperacaoServiceIntegrationTest {
  @Autowired OperacaoService service;
  @Autowired UsuarioRepository usuarios;
  @Autowired CarteiraRepository carteiras;
  @Autowired CorretoraRepository corretoras;
  @Autowired AcaoRepository acoes;
  @Autowired HistoricoCotacaoRepository historicos;
  @Autowired CarteiraAcaoRepository posicoes;
  @Autowired OperacaoRepository operacoes;
  @Autowired Validator validator;
  @Autowired PlatformTransactionManager transactionManager;
  @MockitoBean CotacaoAdapter cotacaoAdapter;
  private Carteira carteira;
  private Acao acao;

  @BeforeEach
  void setup() {
    operacoes.deleteAll();
    posicoes.deleteAll();
    carteiras.deleteAll();
    historicos.deleteAll();
    acoes.deleteAll();
    usuarios.deleteAll();
    Usuario usuario = new Usuario();
    usuario.setNome("Ana");
    usuario.setEmail("ana" + System.nanoTime() + "@test.com");
    usuario.setSenha("senha");
    usuarios.save(usuario);
    Corretora corretora = criarCorretora();
    carteira = new Carteira();
    carteira.setNome("Carteira");
    carteira.setUsuario(usuario);
    carteira.setCorretora(corretora);
    carteira = carteiras.save(carteira);
    acao = new Acao();
    acao.setTicker("TEST3");
    acao.setNomeEmpresa("Teste");
    acao.setMercado(Mercado.BRASIL);
    acao.setMoeda(Moeda.BRL);
    acao = acoes.save(acao);
    when(cotacaoAdapter.consultar(any(), any()))
        .thenReturn(
            new Cotacao(
                new BigDecimal("10.00"),
                java.time.OffsetDateTime.now(java.time.ZoneOffset.UTC),
                "Empresa de teste"));
  }

  @AfterEach
  void limparCorretorasDeTeste() {
    operacoes.deleteAll();
    posicoes.deleteAll();
    carteiras.deleteAll();
    corretoras.deleteAll();
  }

  @Test
  void sellsEntirePositionAndKeepsZeroPosition() {
    service.comprar(
        new CompraRequest(
            carteira.getId(),
            acao.getId(),
            new BigDecimal("2"),
            new BigDecimal("10"),
            null,
            null,
            null));
    service.vender(
        new VendaRequest(carteira.getId(), acao.getId(), new BigDecimal("2"), null, null, null));
    CarteiraAcao posicao =
        posicoes.findByCarteiraIdAndAcaoId(carteira.getId(), acao.getId()).orElseThrow();
    assertThat(posicao.getQuantidade()).isEqualByComparingTo("0");
    assertThat(posicao.getVersion()).isGreaterThan(0L);
    assertThat(
            operacoes.findByCarteiraIdOrderByDataHoraDesc(
                carteira.getId(), org.springframework.data.domain.Pageable.unpaged()))
        .hasSize(2);
  }

  @Test
  void sellsWithManualPriceWithoutLookingUpQuote() {
    service.comprar(
        new CompraRequest(
            carteira.getId(),
            acao.getId(),
            new BigDecimal("2"),
            new BigDecimal("10"),
            null,
            null,
            null));

    var venda =
        service.vender(
            new VendaRequest(
                carteira.getId(),
                acao.getId(),
                BigDecimal.ONE,
                new BigDecimal("12.50"),
                null,
                null,
                null));

    assertThat(venda.precoUnitario()).isEqualByComparingTo("12.50");
    verifyNoInteractions(cotacaoAdapter);
  }

  @Test
  void sellsWithoutManualPriceUsingExternalQuote() {
    service.comprar(
        new CompraRequest(
            carteira.getId(),
            acao.getId(),
            new BigDecimal("2"),
            new BigDecimal("10"),
            null,
            null,
            null));

    var venda =
        service.vender(
            new VendaRequest(carteira.getId(), acao.getId(), BigDecimal.ONE, null, null, null));

    assertThat(venda.precoUnitario()).isEqualByComparingTo("10.00");
    verify(cotacaoAdapter).consultar("TEST3", Mercado.BRASIL);
  }

  @Test
  void rejectsInvalidManualSalePriceBeforeProcessingTheSale() {
    var request =
        new VendaRequest(
            carteira.getId(), acao.getId(), BigDecimal.ONE, BigDecimal.ZERO, null, null, null);

    assertThat(validator.validate(request)).isNotEmpty();
    assertThat(operacoes.count()).isZero();
    assertThat(posicoes.count()).isZero();
  }

  @Test
  void rollsBackSaleWhenFallbackQuoteFails() {
    service.comprar(
        new CompraRequest(
            carteira.getId(),
            acao.getId(),
            new BigDecimal("2"),
            new BigDecimal("10"),
            null,
            null,
            null));
    when(cotacaoAdapter.consultar(any(), any()))
        .thenThrow(
            new ApiException(
                org.springframework.http.HttpStatus.SERVICE_UNAVAILABLE,
                "EXTERNAL_API_UNAVAILABLE",
                "indisponível"));

    assertThatThrownBy(
            () ->
                service.vender(
                    new VendaRequest(
                        carteira.getId(), acao.getId(), BigDecimal.ONE, null, null, null)))
        .isInstanceOf(ApiException.class);

    assertThat(
            posicoes
                .findByCarteiraIdAndAcaoId(carteira.getId(), acao.getId())
                .orElseThrow()
                .getQuantidade())
        .isEqualByComparingTo("2");
    assertThat(operacoes.count()).isEqualTo(1);
  }

  @Test
  void rejectsSaleAbovePositionWithoutCreatingOperation() {
    assertThatThrownBy(
            () ->
                service.vender(
                    new VendaRequest(
                        carteira.getId(), acao.getId(), BigDecimal.ONE, null, null, null)))
        .isInstanceOf(ApiException.class)
        .extracting("code")
        .isEqualTo("QUANTIDADE_INSUFICIENTE");
    assertThat(operacoes.count()).isZero();
  }

  @Test
  void rollsBackPurchaseWhenQuoteFails() {
    when(cotacaoAdapter.consultar(any(), any()))
        .thenThrow(
            new ApiException(
                org.springframework.http.HttpStatus.SERVICE_UNAVAILABLE,
                "EXTERNAL_API_UNAVAILABLE",
                "indisponível"));
    assertThatThrownBy(
            () ->
                service.comprar(
                    new CompraRequest(
                        carteira.getId(), acao.getId(), BigDecimal.ONE, null, null, null, null)))
        .isInstanceOf(ApiException.class);
    assertThat(operacoes.count()).isZero();
    assertThat(posicoes.count()).isZero();
  }

  @Test
  void rejectsInvalidFallbackQuoteWithoutPersistingPurchase() {
    when(cotacaoAdapter.consultar(any(), any()))
        .thenReturn(
            new Cotacao(
                BigDecimal.ZERO,
                java.time.OffsetDateTime.now(java.time.ZoneOffset.UTC),
                "Inválida"));

    assertThatThrownBy(
            () ->
                service.comprar(
                    new CompraRequest(
                        carteira.getId(), acao.getId(), BigDecimal.ONE, null, null, null, null)))
        .isInstanceOf(ApiException.class)
        .extracting("code")
        .isEqualTo("EXTERNAL_API_INVALID_RESPONSE");
    assertThat(operacoes.count()).isZero();
    assertThat(posicoes.count()).isZero();
  }

  @Test
  void createsStockAndPositionWhenBuyingWithTickerAndMarket() {
    var compra =
        service.comprar(
            new CompraRequest(
                carteira.getId(),
                null,
                "novo3",
                Mercado.BRASIL,
                new BigDecimal("3"),
                new BigDecimal("12.50"),
                null,
                null,
                null));

    Acao acaoCriada = acoes.findById(compra.acaoId()).orElseThrow();
    CarteiraAcao posicao =
        posicoes.findByCarteiraIdAndAcaoId(carteira.getId(), acaoCriada.getId()).orElseThrow();
    assertThat(acaoCriada.getTicker()).isEqualTo("NOVO3");
    assertThat(acaoCriada.getNomeEmpresa()).isEqualTo("Empresa de teste");
    assertThat(acaoCriada.getCotacaoAtual()).isEqualByComparingTo("10.00");
    assertThat(posicao.getQuantidade()).isEqualByComparingTo("3");
    assertThat(operacoes.count()).isEqualTo(1);
    assertThat(
            historicos.findByAcaoId(
                acaoCriada.getId(), org.springframework.data.domain.Pageable.unpaged()))
        .hasSize(1);
    verify(cotacaoAdapter).consultar("NOVO3", Mercado.BRASIL);
  }

  @Test
  void reusesStockByTickerAndChangesOnlyTheRequestedPortfolio() {
    Acao existente = acoes.save(acaoNova("REUSE3"));

    service.comprar(
        new CompraRequest(
            carteira.getId(),
            null,
            "reuse3",
            Mercado.BRASIL,
            BigDecimal.ONE,
            new BigDecimal("10"),
            null,
            null,
            null));

    assertThat(acoes.findByTickerIgnoreCase("REUSE3")).map(Acao::getId).contains(existente.getId());
    assertThat(posicoes.findByCarteiraIdAndAcaoId(carteira.getId(), existente.getId())).isPresent();
  }

  @Test
  void rejectsInvalidStockIdentifiersWithoutPersistingAnything() {
    assertThatThrownBy(
            () ->
                service.comprar(
                    new CompraRequest(
                        carteira.getId(),
                        null,
                        null,
                        null,
                        BigDecimal.ONE,
                        new BigDecimal("10"),
                        null,
                        null,
                        null)))
        .isInstanceOf(ApiException.class)
        .extracting("code")
        .isEqualTo("IDENTIFICACAO_ACAO_INVALIDA");
    assertThatThrownBy(
            () ->
                service.comprar(
                    new CompraRequest(
                        carteira.getId(),
                        acao.getId(),
                        "TEST3",
                        Mercado.BRASIL,
                        BigDecimal.ONE,
                        new BigDecimal("10"),
                        null,
                        null,
                        null)))
        .isInstanceOf(ApiException.class)
        .extracting("code")
        .isEqualTo("IDENTIFICACAO_ACAO_INVALIDA");
    assertThat(operacoes.count()).isZero();
    assertThat(posicoes.count()).isZero();
  }

  @Test
  void rollsBackNewStockWhenItsMarketLookupFails() {
    when(cotacaoAdapter.consultar(any(), any()))
        .thenThrow(
            new ApiException(
                org.springframework.http.HttpStatus.NOT_FOUND,
                "TICKER_NAO_ENCONTRADO",
                "não encontrado"));

    assertThatThrownBy(
            () ->
                service.comprar(
                    new CompraRequest(
                        carteira.getId(),
                        null,
                        "INVALID",
                        Mercado.BRASIL,
                        BigDecimal.ONE,
                        new BigDecimal("10"),
                        null,
                        null,
                        null)))
        .isInstanceOf(ApiException.class);

    assertThat(acoes.findByTickerIgnoreCase("INVALID")).isEmpty();
    assertThat(historicos.count()).isZero();
    assertThat(posicoes.count()).isZero();
    assertThat(operacoes.count()).isZero();
  }

  @Test
  void optimisticVersionPreventsTwoIncompatiblePositionUpdates() throws Exception {
    service.comprar(
        new CompraRequest(
            carteira.getId(),
            acao.getId(),
            new BigDecimal("10"),
            new BigDecimal("30"),
            null,
            null,
            null));
    Long posicaoId =
        posicoes.findByCarteiraIdAndAcaoId(carteira.getId(), acao.getId()).orElseThrow().getId();
    CountDownLatch loaded = new CountDownLatch(2);
    CountDownLatch update = new CountDownLatch(1);
    var executor = Executors.newFixedThreadPool(2);
    try {
      var attempts =
          List.of(
              executor.submit(() -> concurrentSaleUpdate(posicaoId, loaded, update)),
              executor.submit(() -> concurrentSaleUpdate(posicaoId, loaded, update)));
      assertThat(loaded.await(5, TimeUnit.SECONDS)).isTrue();
      update.countDown();
      long successes = 0;
      for (var attempt : attempts) {
        if (attempt.get(10, TimeUnit.SECONDS)) {
          successes++;
        }
      }
      assertThat(successes).isEqualTo(1);
    } finally {
      executor.shutdownNow();
    }

    assertThat(posicoes.findById(posicaoId).orElseThrow().getQuantidade())
        .isEqualByComparingTo("2");
  }

  private boolean concurrentSaleUpdate(
      Long posicaoId, CountDownLatch loaded, CountDownLatch update) {
    try {
      new TransactionTemplate(transactionManager)
          .executeWithoutResult(
              status -> {
                CarteiraAcao posicao = posicoes.findById(posicaoId).orElseThrow();
                loaded.countDown();
                try {
                  if (!update.await(5, TimeUnit.SECONDS)) {
                    throw new IllegalStateException("timeout aguardando atualização concorrente");
                  }
                } catch (InterruptedException exception) {
                  Thread.currentThread().interrupt();
                  throw new IllegalStateException(exception);
                }
                posicao.setQuantidade(posicao.getQuantidade().subtract(new BigDecimal("8")));
                posicoes.saveAndFlush(posicao);
              });
      return true;
    } catch (org.springframework.dao.OptimisticLockingFailureException exception) {
      return false;
    }
  }

  private Acao acaoNova(String ticker) {
    Acao nova = new Acao();
    nova.setTicker(ticker);
    nova.setNomeEmpresa("Teste");
    nova.setMercado(Mercado.BRASIL);
    nova.setMoeda(Moeda.BRL);
    nova.setCotacaoAtual(BigDecimal.TEN);
    nova.setDataHoraCotacao(java.time.OffsetDateTime.now(java.time.ZoneOffset.UTC));
    return nova;
  }

  private Corretora criarCorretora() {
    Corretora corretora = new Corretora();
    corretora.setCnpj(String.format("%014d", Math.floorMod(System.nanoTime(), 100000000000000L)));
    corretora.setRazaoSocial("Corretora Teste");
    corretora.setCep("01001000");
    corretora.setLogradouro("Rua Teste");
    corretora.setNumero("1");
    corretora.setBairro("Centro");
    corretora.setCidade("São Paulo");
    corretora.setUf("SP");
    return corretoras.save(corretora);
  }
}
