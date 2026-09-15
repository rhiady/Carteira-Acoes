package com.carteiraacoesbackend.resources;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.carteiraacoesbackend.domains.Acao;
import com.carteiraacoesbackend.domains.Carteira;
import com.carteiraacoesbackend.domains.Corretora;
import com.carteiraacoesbackend.domains.Usuario;
import com.carteiraacoesbackend.domains.enums.Mercado;
import com.carteiraacoesbackend.domains.enums.Moeda;
import com.carteiraacoesbackend.repositories.AcaoRepository;
import com.carteiraacoesbackend.repositories.CarteiraAcaoRepository;
import com.carteiraacoesbackend.repositories.CarteiraRepository;
import com.carteiraacoesbackend.repositories.CorretoraRepository;
import com.carteiraacoesbackend.repositories.OperacaoRepository;
import com.carteiraacoesbackend.repositories.UsuarioRepository;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@SpringBootTest(properties = "spring.jpa.hibernate.ddl-auto=validate")
@AutoConfigureMockMvc
@ActiveProfiles("test")
class OperacaoResourcePersistenceIntegrationTest {

  @Autowired private MockMvc mockMvc;
  @Autowired private WebApplicationContext context;
  @Autowired private PasswordEncoder passwordEncoder;
  @Autowired private OperacaoRepository operacoes;
  @Autowired private CarteiraAcaoRepository posicoes;
  @Autowired private AcaoRepository acoes;
  @Autowired private CarteiraRepository carteiras;
  @Autowired private CorretoraRepository corretoras;
  @Autowired private UsuarioRepository usuarios;

  private Carteira carteira;
  private Acao acao;
  private MockHttpSession session;

  @BeforeEach
  void setUp() throws Exception {
    Usuario usuario = new Usuario();
    usuario.setNome("Usuário de operações");
    usuario.setEmail("operacoes-" + System.nanoTime() + "@test.com");
    usuario.setSenha(passwordEncoder.encode("senha"));
    usuarios.save(usuario);

    Corretora corretora = new Corretora();
    corretora.setCnpj(String.format("%014d", Math.floorMod(System.nanoTime(), 100000000000000L)));
    corretora.setRazaoSocial("Corretora de teste");
    corretora.setCep("01001000");
    corretora.setLogradouro("Rua Teste");
    corretora.setNumero("1");
    corretora.setBairro("Centro");
    corretora.setCidade("São Paulo");
    corretora.setUf("SP");
    corretoras.save(corretora);

    carteira = new Carteira();
    carteira.setNome("Carteira de operações");
    carteira.setUsuario(usuario);
    carteira.setCorretora(corretora);
    carteiras.save(carteira);

    acao = new Acao();
    acao.setTicker("TEST3");
    acao.setNomeEmpresa("Empresa de teste");
    acao.setMercado(Mercado.BRASIL);
    acao.setMoeda(Moeda.BRL);
    acao.setCotacaoAtual(new BigDecimal("10.00"));
    acao.setDataHoraCotacao(OffsetDateTime.now(ZoneOffset.UTC));
    acoes.save(acao);
    session =
        (MockHttpSession)
            mockMvc
                .perform(
                    post("/auth/login")
                        .contentType("application/json")
                        .content("{\"email\":\"" + usuario.getEmail() + "\",\"senha\":\"senha\"}"))
                .andExpect(status().isOk())
                .andReturn()
                .getRequest()
                .getSession();
    mockMvc =
        MockMvcBuilders.webAppContextSetup(context)
            .defaultRequest(get("/").session(session))
            .build();
  }

  @AfterEach
  void cleanUp() {
    operacoes.deleteAll();
    posicoes.deleteAll();
    carteiras.deleteAll();
    acoes.deleteAll();
    corretoras.deleteAll();
    usuarios.deleteAll();
  }

  @Test
  void persistsPurchaseAndSaleSubmittedThroughHttp() throws Exception {
    mockMvc
        .perform(
            post("/operacoes/compras")
                .contentType("application/json")
                .content(
                    """
                    {"carteiraId":%d,"acaoId":%d,"quantidade":2,"precoUnitario":10.50}
                    """
                        .formatted(carteira.getId(), acao.getId())))
        .andExpect(status().isCreated())
        .andExpect(
            header().string("Location", org.hamcrest.Matchers.matchesPattern(".*/operacoes/\\d+")))
        .andExpect(jsonPath("$.id").isNumber())
        .andExpect(jsonPath("$.ticker").value("TEST3"))
        .andExpect(jsonPath("$.nomeEmpresa").value("Empresa de teste"))
        .andExpect(jsonPath("$.mercado").value("BRASIL"))
        .andExpect(jsonPath("$.moeda").value("BRL"));

    assertThat(operacoes.count()).isEqualTo(1);
    assertThat(
            posicoes
                .findByCarteiraIdAndAcaoId(carteira.getId(), acao.getId())
                .orElseThrow()
                .getQuantidade())
        .isEqualByComparingTo("2");

    mockMvc
        .perform(
            post("/operacoes/vendas")
                .contentType("application/json")
                .content(
                    """
                    {"carteiraId":%d,"acaoId":%d,"quantidade":1,"precoUnitario":12.00}
                    """
                        .formatted(carteira.getId(), acao.getId())))
        .andExpect(status().isCreated())
        .andExpect(
            header().string("Location", org.hamcrest.Matchers.matchesPattern(".*/operacoes/\\d+")))
        .andExpect(jsonPath("$.id").isNumber())
        .andExpect(jsonPath("$.ticker").value("TEST3"))
        .andExpect(jsonPath("$.nomeEmpresa").value("Empresa de teste"))
        .andExpect(jsonPath("$.mercado").value("BRASIL"))
        .andExpect(jsonPath("$.moeda").value("BRL"));

    assertThat(operacoes.count()).isEqualTo(2);
    assertThat(
            posicoes
                .findByCarteiraIdAndAcaoId(carteira.getId(), acao.getId())
                .orElseThrow()
                .getQuantidade())
        .isEqualByComparingTo("1");

    mockMvc
        .perform(get("/operacoes/carteiras/{carteiraId}?page=0&size=20", carteira.getId()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content").isArray())
        .andExpect(jsonPath("$.totalElements").value(2))
        .andExpect(jsonPath("$.content[0].acaoId").value(acao.getId()))
        .andExpect(jsonPath("$.content[0].ticker").value("TEST3"))
        .andExpect(jsonPath("$.content[0].nomeEmpresa").value("Empresa de teste"))
        .andExpect(jsonPath("$.content[0].mercado").value("BRASIL"))
        .andExpect(jsonPath("$.content[0].moeda").value("BRL"));
  }

  @Test
  void returnsNullAverageSalePriceForPositionWithoutSales() throws Exception {
    mockMvc
        .perform(
            post("/operacoes/compras")
                .contentType("application/json")
                .content(
                    """
                    {"carteiraId":%d,"acaoId":%d,"quantidade":2,"precoUnitario":10.50}
                    """
                        .formatted(carteira.getId(), acao.getId())))
        .andExpect(status().isCreated());

    mockMvc
        .perform(get("/carteiras/{id}/acoes?page=0&size=20", carteira.getId()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content[0].ticker").value("TEST3"))
        .andExpect(
            jsonPath("$.content[0].precoMedioVenda").value(org.hamcrest.Matchers.nullValue()))
        .andExpect(jsonPath("$.content[0].precoMedio").value(10.50));
  }

  @Test
  void sortsActivePositionsByTickerAscending() throws Exception {
    Acao bbdc4 = criarAcao("BBDC4", "Banco Bradesco");
    Acao vale3 = criarAcao("VALE3", "Vale S.A.");
    comprar(acao, "1");
    comprar(vale3, "1");
    comprar(bbdc4, "1");

    mockMvc
        .perform(get("/carteiras/{id}/acoes?sort=ticker,asc", carteira.getId()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content[0].ticker").value("BBDC4"))
        .andExpect(jsonPath("$.content[1].ticker").value("TEST3"))
        .andExpect(jsonPath("$.content[2].ticker").value("VALE3"));
  }

  @Test
  void sortsActivePositionsByTickerDescending() throws Exception {
    Acao bbdc4 = criarAcao("BBDC4", "Banco Bradesco");
    Acao vale3 = criarAcao("VALE3", "Vale S.A.");
    comprar(acao, "1");
    comprar(vale3, "1");
    comprar(bbdc4, "1");

    mockMvc
        .perform(get("/carteiras/{id}/acoes?sort=ticker,desc", carteira.getId()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content[0].ticker").value("VALE3"))
        .andExpect(jsonPath("$.content[1].ticker").value("TEST3"))
        .andExpect(jsonPath("$.content[2].ticker").value("BBDC4"));
  }

  @Test
  void appliesTickerSortBeforePositionPagination() throws Exception {
    Acao bbdc4 = criarAcao("BBDC4", "Banco Bradesco");
    Acao vale3 = criarAcao("VALE3", "Vale S.A.");
    comprar(acao, "1");
    comprar(vale3, "1");
    comprar(bbdc4, "1");

    mockMvc
        .perform(get("/carteiras/{id}/acoes?page=1&size=1&sort=ticker,asc", carteira.getId()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content[0].ticker").value("TEST3"))
        .andExpect(jsonPath("$.number").value(1))
        .andExpect(jsonPath("$.size").value(1))
        .andExpect(jsonPath("$.totalElements").value(3));
  }

  @Test
  void rejectsUnsupportedActivePositionSortField() throws Exception {
    comprar(acao, "1");

    mockMvc
        .perform(get("/carteiras/{id}/acoes?sort=nomeEmpresa,asc", carteira.getId()))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.error").value("VALIDATION_ERROR"));
  }

  @Test
  void listsActivePositionsWithDefaultSortWhenSortIsNotExplicit() throws Exception {
    comprar(acao, "1");

    mockMvc
        .perform(get("/carteiras/{id}/acoes", carteira.getId()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content[0].ticker").value("TEST3"));
  }

  @Test
  void rejectsZeroManualPriceWithoutPersistingPurchase() throws Exception {
    mockMvc
        .perform(
            post("/operacoes/compras")
                .contentType("application/json")
                .content(
                    """
                    {"carteiraId":%d,"acaoId":%d,"quantidade":5,"precoUnitario":0}
                    """
                        .formatted(carteira.getId(), acao.getId())))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.error").value("VALIDATION_ERROR"));

    assertThat(operacoes.count()).isZero();
    assertThat(posicoes.count()).isZero();
  }

  private Acao criarAcao(String ticker, String nomeEmpresa) {
    Acao nova = new Acao();
    nova.setTicker(ticker);
    nova.setNomeEmpresa(nomeEmpresa);
    nova.setMercado(Mercado.BRASIL);
    nova.setMoeda(Moeda.BRL);
    nova.setCotacaoAtual(new BigDecimal("10.00"));
    nova.setDataHoraCotacao(OffsetDateTime.now(ZoneOffset.UTC));
    return acoes.save(nova);
  }

  private void comprar(Acao acaoCompra, String quantidade) throws Exception {
    mockMvc
        .perform(
            post("/operacoes/compras")
                .contentType("application/json")
                .content(
                    """
                    {"carteiraId":%d,"acaoId":%d,"quantidade":%s,"precoUnitario":10.00}
                    """
                        .formatted(carteira.getId(), acaoCompra.getId(), quantidade)))
        .andExpect(status().isCreated());
  }
}
