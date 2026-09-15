package com.carteiraacoesbackend.services;

import static org.assertj.core.api.Assertions.assertThat;

import com.carteiraacoesbackend.clients.PtaxClient;
import com.carteiraacoesbackend.domains.Acao;
import com.carteiraacoesbackend.domains.Carteira;
import com.carteiraacoesbackend.domains.CarteiraAcao;
import com.carteiraacoesbackend.domains.Corretora;
import com.carteiraacoesbackend.domains.Operacao;
import com.carteiraacoesbackend.domains.Usuario;
import com.carteiraacoesbackend.domains.enums.Mercado;
import com.carteiraacoesbackend.domains.enums.Moeda;
import com.carteiraacoesbackend.domains.enums.TipoOperacao;
import com.carteiraacoesbackend.dto.CarteiraResumoResponse;
import com.carteiraacoesbackend.dto.integrations.PtaxQuoteResponse;
import com.carteiraacoesbackend.mappers.CarteiraMapper;
import com.carteiraacoesbackend.mappers.UsuarioMapper;
import com.carteiraacoesbackend.repositories.AcaoRepository;
import com.carteiraacoesbackend.repositories.CarteiraAcaoRepository;
import com.carteiraacoesbackend.repositories.CarteiraRepository;
import com.carteiraacoesbackend.repositories.CorretoraRepository;
import com.carteiraacoesbackend.repositories.OperacaoRepository;
import com.carteiraacoesbackend.repositories.UsuarioRepository;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;

@DataJpaTest(properties = "spring.jpa.hibernate.ddl-auto=validate")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
class CarteiraResumoServiceTest {
  @Autowired UsuarioRepository usuarios;
  @Autowired CorretoraRepository corretoras;
  @Autowired CarteiraRepository carteiras;
  @Autowired AcaoRepository acoes;
  @Autowired CarteiraAcaoRepository posicoes;
  @Autowired OperacaoRepository operacoes;

  @Test
  void returnsEnrichedPositionsAndKeepsCurrencyTotalsSeparate() {
    Carteira carteira = criarCarteira();
    Acao brl = criarAcao("PETR4", Mercado.BRASIL, Moeda.BRL, "20");
    Acao usd = criarAcao("AAPL", Mercado.EUA, Moeda.USD, "30");
    criarPosicao(carteira, brl, "2", "15");
    criarPosicao(carteira, usd, "3", "25");

    PtaxClient ptaxClient =
        (dataInicial, dataFinal, limite, formato) ->
            new PtaxQuoteResponse(
                List.of(new PtaxQuoteResponse.PtaxQuote("5.00", "2026-09-04 13:00:00")));
    CarteiraService service =
        new CarteiraService(
            carteiras,
            posicoes,
            corretoras,
            operacoes,
            new UsuarioService(usuarios, new UsuarioMapper()),
            new CarteiraMapper(),
            new PosicaoFinanceiraCalculator(),
            new CurrencyConversionService(ptaxClient));

    assertThat(service.listarPosicoesAtivas(carteira.getId(), PageRequest.of(0, 20)).getContent())
        .extracting(posicao -> posicao.moeda())
        .containsExactlyInAnyOrder(Moeda.BRL, Moeda.USD);
    assertThat(service.listarPosicoesAtivas(carteira.getId(), PageRequest.of(0, 20)).getContent())
        .anySatisfy(
            posicao -> {
              assertThat(posicao.mercado()).isEqualTo(Mercado.EUA);
              assertThat(posicao.cotacaoEmBrl()).isEqualByComparingTo("150");
              assertThat(posicao.valorAtualEmBrl()).isEqualByComparingTo("450");
              assertThat(posicao.taxaUsdBrl()).isEqualByComparingTo("5");
              assertThat(posicao.dataHoraTaxaUsdBrl())
                  .isEqualTo(OffsetDateTime.of(2026, 9, 4, 16, 0, 0, 0, ZoneOffset.UTC));
            })
        .anySatisfy(
            posicao -> {
              assertThat(posicao.mercado()).isEqualTo(Mercado.BRASIL);
              assertThat(posicao.cotacaoEmBrl()).isNull();
              assertThat(posicao.valorAtualEmBrl()).isNull();
            });
    CarteiraResumoResponse resumo = service.resumo(carteira.getId());

    assertThat(resumo.quantidadeAtivos()).isEqualTo(2);
    assertThat(resumo.resumosPorMoeda()).hasSize(2);
    assertThat(resumo.resumosPorMoeda())
        .anySatisfy(
            item -> {
              assertThat(item.moeda()).isEqualTo(Moeda.BRL);
              assertThat(item.valorAtual()).isEqualByComparingTo("40");
              assertThat(item.lucroPrejuizo()).isEqualByComparingTo("10");
            })
        .anySatisfy(
            item -> {
              assertThat(item.moeda()).isEqualTo(Moeda.USD);
              assertThat(item.valorAtual()).isEqualByComparingTo("90");
              assertThat(item.lucroPrejuizo()).isEqualByComparingTo("15");
            });
  }

  @Test
  void preservesMissingQuoteNullabilityWithoutPublishingPartialCurrencyTotals() {
    Carteira carteira = criarCarteira();
    Acao semCotacao = criarAcao("MSFT", Mercado.EUA, Moeda.USD, null);
    criarPosicao(carteira, semCotacao, "2", "25");

    PtaxClient ptaxClient =
        (dataInicial, dataFinal, limite, formato) -> {
          throw new AssertionError("PTAX não deve ser consultada sem cotação a converter");
        };
    CarteiraService service =
        new CarteiraService(
            carteiras,
            posicoes,
            corretoras,
            operacoes,
            new UsuarioService(usuarios, new UsuarioMapper()),
            new CarteiraMapper(),
            new PosicaoFinanceiraCalculator(),
            new CurrencyConversionService(ptaxClient));

    var posicao =
        service
            .listarPosicoesAtivas(carteira.getId(), PageRequest.of(0, 20))
            .getContent()
            .getFirst();
    assertThat(posicao.cotacaoAtual()).isNull();
    assertThat(posicao.dataHoraCotacao()).isNull();
    assertThat(posicao.precoMedio()).isEqualByComparingTo("25");
    assertThat(posicao.lucroPrejuizoRealizado()).isEqualByComparingTo("0");
    assertThat(posicao.valorAtual()).isNull();
    assertThat(posicao.lucroPrejuizoNaoRealizado()).isNull();
    assertThat(posicao.lucroPrejuizo()).isNull();
    assertThat(posicao.cotacaoEmBrl()).isNull();
    assertThat(posicao.valorAtualEmBrl()).isNull();
    assertThat(posicao.taxaUsdBrl()).isNull();

    CarteiraResumoResponse resumo = service.resumo(carteira.getId());
    assertThat(resumo.quantidadeAtivos()).isEqualTo(1);
    assertThat(resumo.resumosPorMoeda())
        .singleElement()
        .satisfies(
            item -> {
              assertThat(item.moeda()).isEqualTo(Moeda.USD);
              assertThat(item.valorAtual()).isNull();
              assertThat(item.lucroPrejuizo()).isNull();
            });
  }

  private Carteira criarCarteira() {
    Usuario usuario = new Usuario();
    usuario.setNome("Ana");
    usuario.setEmail("ana" + System.nanoTime() + "@test.com");
    usuario.setSenha("senha");
    usuarios.save(usuario);
    Corretora corretora = new Corretora();
    corretora.setCnpj(String.format("%014d", Math.floorMod(System.nanoTime(), 100000000000000L)));
    corretora.setRazaoSocial("Corretora Teste");
    corretora.setCep("01001000");
    corretora.setLogradouro("Rua Teste");
    corretora.setNumero("1");
    corretora.setBairro("Centro");
    corretora.setCidade("São Paulo");
    corretora.setUf("SP");
    corretoras.save(corretora);
    Carteira carteira = new Carteira();
    carteira.setNome("Carteira");
    carteira.setUsuario(usuario);
    carteira.setCorretora(corretora);
    return carteiras.save(carteira);
  }

  private Acao criarAcao(String ticker, Mercado mercado, Moeda moeda, String cotacao) {
    Acao acao = new Acao();
    acao.setTicker(ticker);
    acao.setNomeEmpresa(ticker);
    acao.setMercado(mercado);
    acao.setMoeda(moeda);
    acao.setCotacaoAtual(cotacao == null ? null : new BigDecimal(cotacao));
    acao.setDataHoraCotacao(cotacao == null ? null : OffsetDateTime.now(ZoneOffset.UTC));
    return acoes.save(acao);
  }

  private void criarPosicao(Carteira carteira, Acao acao, String quantidade, String preco) {
    CarteiraAcao posicao = new CarteiraAcao();
    posicao.setCarteira(carteira);
    posicao.setAcao(acao);
    posicao.setQuantidade(new BigDecimal(quantidade));
    posicoes.save(posicao);
    Operacao operacao = new Operacao();
    operacao.setCarteira(carteira);
    operacao.setAcao(acao);
    operacao.setTipo(TipoOperacao.COMPRA);
    operacao.setQuantidade(new BigDecimal(quantidade));
    operacao.setPrecoUnitario(new BigDecimal(preco));
    operacoes.save(operacao);
  }
}
