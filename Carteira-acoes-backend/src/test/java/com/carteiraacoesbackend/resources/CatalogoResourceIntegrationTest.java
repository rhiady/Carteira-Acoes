package com.carteiraacoesbackend.resources;

import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.carteiraacoesbackend.domains.Corretora;
import com.carteiraacoesbackend.domains.enums.Mercado;
import com.carteiraacoesbackend.integrations.Cotacao;
import com.carteiraacoesbackend.integrations.CotacaoAdapter;
import com.carteiraacoesbackend.repositories.AcaoRepository;
import com.carteiraacoesbackend.repositories.CarteiraAcaoRepository;
import com.carteiraacoesbackend.repositories.CarteiraRepository;
import com.carteiraacoesbackend.repositories.CorretoraRepository;
import com.carteiraacoesbackend.repositories.HistoricoCotacaoRepository;
import com.carteiraacoesbackend.repositories.OperacaoRepository;
import com.carteiraacoesbackend.repositories.UsuarioRepository;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

@SpringBootTest(properties = "spring.jpa.hibernate.ddl-auto=validate")
@AutoConfigureMockMvc
@ActiveProfiles("test")
class CatalogoResourceIntegrationTest {

  @Autowired private MockMvc mockMvc;

  @Autowired private CorretoraRepository corretoras;

  @Autowired private CarteiraRepository carteiras;
  @Autowired private AcaoRepository acoes;
  @Autowired private HistoricoCotacaoRepository historicos;
  @Autowired private CarteiraAcaoRepository posicoes;
  @Autowired private OperacaoRepository operacoes;
  @Autowired private UsuarioRepository usuarios;
  @MockitoBean CotacaoAdapter cotacaoAdapter;

  @AfterEach
  void limparCarteirasDeTeste() {
    carteiras.deleteAll();
    corretoras.deleteAll();
    historicos.deleteAll();
    acoes.deleteAll();
  }

  @Test
  void createsUserAndDoesNotExposePassword() throws Exception {
    mockMvc
        .perform(
            post("/api/usuarios")
                .with(user("admin").roles("ADMIN"))
                .contentType("application/json")
                .content(
                    "{\"login\":\"ana@teste.com\",\"password\":\"segredo123\",\"papel\":\"USER\"}"))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.login").value("ana@teste.com"))
        .andExpect(jsonPath("$.papel").value("USER"))
        .andExpect(jsonPath("$.ativo").value(true))
        .andExpect(jsonPath("$.senha").doesNotExist());
  }

  @Test
  void rejectsMalformedJsonWithStandardErrorBody() throws Exception {
    mockMvc
        .perform(
            post("/api/usuarios")
                .with(user("admin").roles("ADMIN"))
                .contentType("application/json")
                .content("{\"login\":\"ana@teste.com\",\"password\":"))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.status").value(400))
        .andExpect(jsonPath("$.error").value("INVALID_REQUEST_FORMAT"))
        .andExpect(jsonPath("$.message").value("Formato da requisição é inválido."))
        .andExpect(jsonPath("$.path").value("/api/usuarios"));
  }

  @Test
  void rejectsInvalidPathAndQueryTypesWithStandardErrorBody() throws Exception {
    mockMvc
        .perform(get("/api/acoes/{id}", "not-a-number"))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.error").value("INVALID_REQUEST_FORMAT"))
        .andExpect(jsonPath("$.message").value("Formato da requisição é inválido."));

    mockMvc
        .perform(get("/api/acoes/{id}/historico", 1).param("inicio", "not-a-date"))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.error").value("INVALID_REQUEST_FORMAT"))
        .andExpect(jsonPath("$.path").value("/api/acoes/1/historico"));
  }

  @Test
  void rejectsMethodValidationWithStandardErrorBody() throws Exception {
    mockMvc
        .perform(get("/api/acoes/ticker/{ticker}", "_INVALID"))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.status").value(400))
        .andExpect(jsonPath("$.error").value("VALIDATION_ERROR"))
        .andExpect(jsonPath("$.path").value("/api/acoes/ticker/_INVALID"));
  }

  @Test
  void validatesPaginationAndPublicSortBeforeQuerying() throws Exception {
    mockMvc
        .perform(get("/api/acoes").param("page", "-1"))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.error").value("INVALID_PAGINATION"));
    mockMvc
        .perform(get("/api/acoes").param("size", "0"))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.error").value("INVALID_PAGINATION"));
    mockMvc
        .perform(get("/api/acoes").param("size", "101"))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.error").value("INVALID_PAGINATION"));
    mockMvc
        .perform(get("/api/acoes").param("sort", "ticker,sideways"))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.error").value("INVALID_SORT"));
    mockMvc
        .perform(get("/api/acoes").param("sort", "internalField,asc"))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.error").value("VALIDATION_ERROR"));
    mockMvc
        .perform(
            get("/api/acoes").param("page", "0").param("size", "20").param("sort", "ticker,desc"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content").isArray())
        .andExpect(jsonPath("$.number").value(0))
        .andExpect(jsonPath("$.size").value(20))
        .andExpect(jsonPath("$.totalElements").isNumber())
        .andExpect(jsonPath("$.totalPages").isNumber())
        .andExpect(jsonPath("$.first").value(true))
        .andExpect(jsonPath("$.last").isBoolean())
        .andExpect(jsonPath("$.empty").isBoolean());
  }

  @Test
  void updatesUserWithoutExposingPassword() throws Exception {
    String response =
        mockMvc
            .perform(
                post("/api/usuarios")
                    .with(user("admin").roles("ADMIN"))
                    .contentType("application/json")
                    .content(
                        "{\"login\":\"ana-update@teste.com\",\"password\":\"segredo123\",\"papel\":\"USER\"}"))
            .andReturn()
            .getResponse()
            .getContentAsString();
    long id = Long.parseLong(response.replaceAll(".*\\\"id\\\":(\\d+).*", "$1"));
    RequestPostProcessor session = admin();
    String senhaAnterior = usuarios.findById(id).orElseThrow().getSenha();

    mockMvc
        .perform(
            put("/api/usuarios/{id}", id)
                .with(session)
                .with(csrf())
                .contentType("application/json")
                .content("{\"login\":\"ana-nova@teste.com\",\"ativo\":true}"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.login").value("ana-nova@teste.com"))
        .andExpect(jsonPath("$.senha").doesNotExist());
    String senha = usuarios.findById(id).orElseThrow().getSenha();
    org.junit.jupiter.api.Assertions.assertEquals(senhaAnterior, senha);
  }

  @Test
  void encodesNewPasswordAndInvalidatesTheOldCredential() throws Exception {
    String response =
        mockMvc
            .perform(
                post("/api/usuarios")
                    .with(user("admin").roles("ADMIN"))
                    .contentType("application/json")
                    .content(
                        "{\"login\":\"bia-update@teste.com\",\"password\":\"senha-antiga\",\"papel\":\"USER\"}"))
            .andExpect(status().isCreated())
            .andReturn()
            .getResponse()
            .getContentAsString();
    long id = Long.parseLong(response.replaceAll(".*\\\"id\\\":(\\d+).*", "$1"));
    RequestPostProcessor session = admin();

    mockMvc
        .perform(
            put("/api/usuarios/{id}", id)
                .with(session)
                .with(csrf())
                .contentType("application/json")
                .content(
                    "{\"login\":\"bia-update@teste.com\",\"ativo\":true,\"password\":\"senha-nova\"}"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.senha").doesNotExist());

    mockMvc
        .perform(
            post("/api/auth/login")
                .contentType("application/json")
                .content("{\"login\":\"bia-update@teste.com\",\"password\":\"senha-antiga\"}"))
        .andExpect(status().isOk());
    mockMvc
        .perform(
            put("/api/usuarios/{id}/senha", id)
                .with(session)
                .with(csrf())
                .contentType("application/json")
                .content("{\"password\":\"senha-nova\"}"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.password").doesNotExist());
    mockMvc
        .perform(
            post("/api/auth/login")
                .contentType("application/json")
                .content("{\"login\":\"bia-update@teste.com\",\"password\":\"senha-antiga\"}"))
        .andExpect(status().isUnauthorized());
    login("bia-update@teste.com", "senha-nova");
    org.junit.jupiter.api.Assertions.assertNotEquals(
        "senha-nova", usuarios.findById(id).orElseThrow().getSenha());
  }

  @Test
  void rejectsDuplicateEmailOnUserUpdate() throws Exception {
    mockMvc
        .perform(
            post("/api/usuarios")
                .with(user("admin").roles("ADMIN"))
                .contentType("application/json")
                .content(
                    "{\"login\":\"um-update@teste.com\",\"password\":\"segredo123\",\"papel\":\"USER\"}"))
        .andExpect(status().isCreated());
    String response =
        mockMvc
            .perform(
                post("/api/usuarios")
                    .with(user("admin").roles("ADMIN"))
                    .contentType("application/json")
                    .content(
                        "{\"login\":\"dois-update@teste.com\",\"password\":\"segredo123\",\"papel\":\"USER\"}"))
            .andReturn()
            .getResponse()
            .getContentAsString();
    long id = Long.parseLong(response.replaceAll(".*\\\"id\\\":(\\d+).*", "$1"));
    RequestPostProcessor session = admin();

    mockMvc
        .perform(
            put("/api/usuarios/{id}", id)
                .with(session)
                .with(csrf())
                .contentType("application/json")
                .content("{\"login\":\"um-update@teste.com\",\"ativo\":true}"))
        .andExpect(status().isConflict())
        .andExpect(jsonPath("$.error").value("EMAIL_DUPLICADO"));
  }

  @Test
  void rejectsInvalidUserUpdate() throws Exception {
    mockMvc
        .perform(
            put("/api/usuarios/{id}", 999999L)
                .contentType("application/json")
                .content("{\"nome\":\"\",\"email\":\"invalido\"}"))
        .andExpect(status().isUnauthorized())
        .andExpect(jsonPath("$.error").value("UNAUTHORIZED"));
  }

  @Test
  void rejectsDuplicateEmailAndReturnsStandardError() throws Exception {
    String body = "{\"login\":\"bruno@teste.com\",\"password\":\"segredo123\",\"papel\":\"USER\"}";
    mockMvc
        .perform(
            post("/api/usuarios")
                .with(user("admin").roles("ADMIN"))
                .contentType("application/json")
                .content(body))
        .andExpect(status().isCreated());
    mockMvc
        .perform(
            post("/api/usuarios")
                .with(user("admin").roles("ADMIN"))
                .contentType("application/json")
                .content(body))
        .andExpect(status().isConflict())
        .andExpect(jsonPath("$.error").value("EMAIL_DUPLICADO"));
  }

  @Test
  void createsPortfolioForExistingUserAndPaginatesUserPortfolios() throws Exception {
    String userResponse =
        mockMvc
            .perform(
                post("/api/usuarios")
                    .with(user("admin").roles("ADMIN"))
                    .contentType("application/json")
                    .content(
                        "{\"login\":\"carla@teste.com\",\"password\":\"segredo123\",\"papel\":\"USER\"}"))
            .andExpect(status().isCreated())
            .andReturn()
            .getResponse()
            .getContentAsString();
    long userId = Long.parseLong(userResponse.replaceAll(".*\\\"id\\\":(\\d+).*", "$1"));
    long corretoraId = criarCorretora().getId();
    RequestPostProcessor session = login("carla@teste.com", "segredo123");

    mockMvc
        .perform(
            post("/api/carteiras")
                .with(session)
                .with(csrf())
                .contentType("application/json")
                .content(
                    "{\"nome\":\"Longo Prazo\",\"usuarioId\":"
                        + userId
                        + ",\"corretoraId\":"
                        + corretoraId
                        + "}"))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.corretoraId").value(corretoraId));
    mockMvc
        .perform(get("/api/usuarios/{id}/carteiras?page=0&size=20", userId).with(session))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content[0].nome").value("Longo Prazo"))
        .andExpect(jsonPath("$.content[0].corretoraId").value(corretoraId));
  }

  @Test
  void rejectsPortfolioWithoutOrWithUnknownBroker() throws Exception {
    String userResponse =
        mockMvc
            .perform(
                post("/api/usuarios")
                    .with(user("admin").roles("ADMIN"))
                    .contentType("application/json")
                    .content(
                        "{\"login\":\"davi@teste.com\",\"password\":\"segredo123\",\"papel\":\"USER\"}"))
            .andExpect(status().isCreated())
            .andReturn()
            .getResponse()
            .getContentAsString();
    long userId = Long.parseLong(userResponse.replaceAll(".*\\\"id\\\":(\\d+).*", "$1"));
    RequestPostProcessor session = login("davi@teste.com", "segredo123");

    mockMvc
        .perform(
            post("/api/carteiras")
                .with(session)
                .with(csrf())
                .contentType("application/json")
                .content("{\"nome\":\"Sem corretora\",\"usuarioId\":" + userId + "}"))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.error").value("VALIDATION_ERROR"));
    mockMvc
        .perform(
            post("/api/carteiras")
                .with(session)
                .with(csrf())
                .contentType("application/json")
                .content(
                    "{\"nome\":\"Inválida\",\"usuarioId\":" + userId + ",\"corretoraId\":999999}"))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.error").value("CORRETORA_NAO_ENCONTRADA"));
  }

  @Test
  void returnsNotFoundForUnknownPortfolio() throws Exception {
    mockMvc
        .perform(get("/api/carteiras/9999"))
        .andExpect(status().isUnauthorized())
        .andExpect(jsonPath("$.error").value("UNAUTHORIZED"));
  }

  @Test
  void createsStockDirectlyAndKeepsCatalogQueriesAvailable() throws Exception {
    when(cotacaoAdapter.consultar("PETR4", Mercado.BRASIL))
        .thenReturn(
            new Cotacao(
                new BigDecimal("32.15"),
                OffsetDateTime.parse("2026-09-03T10:00:00-03:00"),
                "Petrobras S.A."));
    mockMvc
        .perform(
            post("/api/acoes")
                .with(user("admin").roles("ADMIN"))
                .with(csrf())
                .contentType("application/json")
                .content("{\"ticker\":\"PETR4\",\"mercado\":\"BRASIL\"}"))
        .andExpect(status().isCreated())
        .andExpect(
            header().string("Location", org.hamcrest.Matchers.matchesPattern(".*/api/acoes/\\d+")))
        .andExpect(jsonPath("$.ticker").value("PETR4"))
        .andExpect(jsonPath("$.nomeEmpresa").value("Petrobras S.A."));
    mockMvc
        .perform(
            post("/api/acoes")
                .with(user("admin").roles("ADMIN"))
                .with(csrf())
                .contentType("application/json")
                .content("{\"ticker\":\"PETR4\",\"mercado\":\"BRASIL\"}"))
        .andExpect(status().isConflict())
        .andExpect(jsonPath("$.error").value("TICKER_DUPLICADO"));
    org.assertj.core.api.Assertions.assertThat(acoes.count()).isEqualTo(1);
    org.assertj.core.api.Assertions.assertThat(historicos.count()).isEqualTo(1);
    org.assertj.core.api.Assertions.assertThat(posicoes.count()).isZero();
    org.assertj.core.api.Assertions.assertThat(operacoes.count()).isZero();
    mockMvc
        .perform(get("/api/acoes?page=0&size=20"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content").isArray());
  }

  @Test
  void rejectsPurchasesWithInvalidStockIdentifiers() throws Exception {
    mockMvc
        .perform(
            post("/api/operacoes/compras")
                .contentType("application/json")
                .content("{\"carteiraId\":1,\"quantidade\":1,\"precoUnitario\":10}"))
        .andExpect(status().isUnauthorized())
        .andExpect(jsonPath("$.error").value("UNAUTHORIZED"));
    mockMvc
        .perform(
            post("/api/operacoes/compras")
                .contentType("application/json")
                .content(
                    "{\"carteiraId\":1,\"acaoId\":1,\"ticker\":\"PETR4\",\"mercado\":\"BRASIL\",\"quantidade\":1,\"precoUnitario\":10}"))
        .andExpect(status().isUnauthorized())
        .andExpect(jsonPath("$.error").value("UNAUTHORIZED"));
    mockMvc
        .perform(
            post("/api/operacoes/compras")
                .contentType("application/json")
                .content(
                    "{\"carteiraId\":1,\"ticker\":\"PETR4\",\"quantidade\":1,\"precoUnitario\":10}"))
        .andExpect(status().isUnauthorized())
        .andExpect(jsonPath("$.error").value("UNAUTHORIZED"));
  }

  private RequestPostProcessor login(String email, String senha) throws Exception {
    String response =
        mockMvc
            .perform(
                post("/api/auth/login")
                    .contentType("application/json")
                    .content("{\"login\":\"" + email + "\",\"password\":\"" + senha + "\"}"))
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

  private RequestPostProcessor admin() {
    return user("admin").roles("ADMIN");
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
