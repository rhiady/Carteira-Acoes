package com.carteiraacoesbackend.resources;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
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
import com.carteiraacoesbackend.domains.enums.Papel;
import com.carteiraacoesbackend.integrations.Cotacao;
import com.carteiraacoesbackend.integrations.CotacaoAdapter;
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
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.RequestPostProcessor;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

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
  @MockitoBean private CotacaoAdapter cotacaoAdapter;

  private Carteira carteira;
  private Acao acao;
  private RequestPostProcessor session;

  @BeforeEach
  void setUp() throws Exception {
    Usuario usuario = new Usuario();
    usuario.setNome("Usuário de operações");
    usuario.setEmail("operacoes-" + System.nanoTime() + "@test.com");
    usuario.setSenha(passwordEncoder.encode("senha"));
    usuario.setPapel(Papel.USER);
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
        login(usuario.getEmail());
    mockMvc =
        MockMvcBuilders.webAppContextSetup(context)
            .apply(springSecurity())
            .defaultRequest(get("/").with(session))
            .build();
  }

  private RequestPostProcessor login(String email) throws Exception {
    String response =
        mockMvc
            .perform(
                post("/api/auth/login")
                    .contentType("application/json")
                    .content("{\"login\":\"" + email + "\",\"password\":\"senha\",\"papel\":\"USER\"}"))
            .andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString();
    String token = response.replaceAll(".*\\\"token\\\":\\\"([^\\\"]+)\\\".*", "$1");
    return request -> {
      request.addHeader("Authorization", "Bearer " + token);
      return request;
    };
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
            post("/api/operacoes/compras")
                .with(session)
                .with(csrf())
                .contentType("application/json")
                .content(
                    """
                    {"carteiraId":%d,"acaoId":%d,"quantidade":2,"precoUnitario":10.50}
                    """
                        .formatted(carteira.getId(), acao.getId())))
        .andExpect(status().isCreated())
        .andExpect(
            header().string("Location", org.hamcrest.Matchers.matchesPattern(".*/api/operacoes/\\d+")))
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
            post("/api/operacoes/vendas")
                .with(csrf())
                .contentType("application/json")
                .content(
                    """
                    {"carteiraId":%d,"acaoId":%d,"quantidade":1,"precoUnitario":12.00}
                    """
                        .formatted(carteira.getId(), acao.getId())))
        .andExpect(status().isCreated())
        .andExpect(
            header().string("Location", org.hamcrest.Matchers.matchesPattern(".*/api/operacoes/\\d+")))
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
        .perform(get("/api/operacoes/carteiras/{carteiraId}?page=0&size=20", carteira.getId()))
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
            post("/api/operacoes/compras")
                .with(csrf())
                .contentType("application/json")
                .content(
                    """
                    {"carteiraId":%d,"acaoId":%d,"quantidade":2,"precoUnitario":10.50}
                    """
                        .formatted(carteira.getId(), acao.getId())))
        .andExpect(status().isCreated());

    mockMvc
        .perform(get("/api/carteiras/{id}/acoes?page=0&size=20", carteira.getId()))
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
        .perform(get("/api/carteiras/{id}/acoes?sort=ticker,asc", carteira.getId()))
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
        .perform(get("/api/carteiras/{id}/acoes?sort=ticker,desc", carteira.getId()))
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
        .perform(get("/api/carteiras/{id}/acoes?page=1&size=1&sort=ticker,asc", carteira.getId()))
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
        .perform(get("/api/carteiras/{id}/acoes?sort=nomeEmpresa,asc", carteira.getId()))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.error").value("VALIDATION_ERROR"));
  }

  @Test
  void listsActivePositionsWithDefaultSortWhenSortIsNotExplicit() throws Exception {
    comprar(acao, "1");

    mockMvc
        .perform(get("/api/carteiras/{id}/acoes", carteira.getId()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content[0].ticker").value("TEST3"));
  }

  @Test
  void rejectsZeroManualPriceWithoutPersistingPurchase() throws Exception {
    mockMvc
        .perform(
            post("/api/operacoes/compras")
                .with(csrf())
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

  @Test
  void userCanBuyCatalogActionButCannotCreateOrReferenceTicker() throws Exception {
    Usuario admin = new Usuario();
    admin.setNome("Administrador");
    admin.setEmail("admin-operacoes-" + System.nanoTime() + "@test.com");
    admin.setSenha(passwordEncoder.encode("senha"));
    admin.setPapel(Papel.ADMIN);
    usuarios.save(admin);
    when(cotacaoAdapter.consultar(any(), any()))
        .thenReturn(new Cotacao(new BigDecimal("22.00"), OffsetDateTime.now(ZoneOffset.UTC), "Vale S.A."));
    MockMvc adminClient = MockMvcBuilders.webAppContextSetup(context).apply(springSecurity()).build();
    String adminResponse =
        adminClient
            .perform(
                post("/api/auth/login")
                    .contentType("application/json")
                    .content("{\"login\":\"" + admin.getEmail() + "\",\"password\":\"senha\"}"))
            .andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString();
    String adminToken = adminResponse.replaceAll(".*\"token\":\"([^\"]+)\".*", "$1");
    RequestPostProcessor adminSession =
        request -> {
          request.addHeader("Authorization", "Bearer " + adminToken);
          return request;
        };
    String catalogResponse =
        adminClient
            .perform(
                post("/api/acoes")
                    .with(adminSession)
                    .with(csrf())
                    .contentType("application/json")
                    .content("{\"ticker\":\"VALE3\",\"mercado\":\"BRASIL\"}"))
            .andExpect(status().isCreated())
            .andReturn()
            .getResponse()
            .getContentAsString();
    long catalogAcaoId = Long.parseLong(catalogResponse.replaceAll(".*\"id\":(\\d+).*", "$1"));

    mockMvc
        .perform(
            post("/api/acoes")
                .with(session)
                .with(csrf())
                .contentType("application/json")
                .content("{\"ticker\":\"ITUB4\",\"mercado\":\"BRASIL\"}"))
        .andExpect(status().isForbidden());
    mockMvc
        .perform(
            post("/api/operacoes/compras")
                .with(session)
                .with(csrf())
                .contentType("application/json")
                .content(
                    "{\"carteiraId\":%d,\"ticker\":\"VALE3\",\"mercado\":\"BRASIL\",\"quantidade\":1,\"precoUnitario\":22}"
                        .formatted(carteira.getId())))
        .andExpect(status().isBadRequest());
    mockMvc
        .perform(
            post("/api/operacoes/compras")
                .with(session)
                .with(csrf())
                .contentType("application/json")
                .content("{\"carteiraId\":%d,\"quantidade\":1,\"precoUnitario\":22}".formatted(carteira.getId())))
        .andExpect(status().isBadRequest());
    mockMvc
        .perform(
            post("/api/operacoes/compras")
                .with(session)
                .with(csrf())
                .contentType("application/json")
                .content(
                    "{\"carteiraId\":%d,\"acaoId\":999999,\"quantidade\":1,\"precoUnitario\":22}"
                        .formatted(carteira.getId())))
        .andExpect(status().isNotFound());
    mockMvc
        .perform(
            post("/api/operacoes/compras")
                .with(session)
                .with(csrf())
                .contentType("application/json")
                .content(
                    "{\"carteiraId\":%d,\"acaoId\":%d,\"quantidade\":1,\"precoUnitario\":22}"
                        .formatted(carteira.getId(), catalogAcaoId)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.acaoId").value(catalogAcaoId));
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
            post("/api/operacoes/compras")
                .with(session)
                .with(csrf())
                .contentType("application/json")
                .content(
                    """
                    {"carteiraId":%d,"acaoId":%d,"quantidade":%s,"precoUnitario":10.00}
                    """
                        .formatted(carteira.getId(), acaoCompra.getId(), quantidade)))
        .andExpect(status().isCreated());
  }
}
