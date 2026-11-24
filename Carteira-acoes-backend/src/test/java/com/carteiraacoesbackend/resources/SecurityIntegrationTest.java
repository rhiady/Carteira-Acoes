package com.carteiraacoesbackend.resources;

import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.carteiraacoesbackend.clients.BrasilApiClient;
import com.carteiraacoesbackend.domains.Usuario;
import com.carteiraacoesbackend.domains.enums.Papel;
import com.carteiraacoesbackend.dto.integrations.BrasilApiCnpjResponse;
import com.carteiraacoesbackend.dto.integrations.BrasilApiCvmBrokerResponse;
import com.carteiraacoesbackend.integrations.Cotacao;
import com.carteiraacoesbackend.integrations.CotacaoAdapter;
import com.carteiraacoesbackend.repositories.AcaoRepository;
import com.carteiraacoesbackend.repositories.CorretoraRepository;
import com.carteiraacoesbackend.repositories.UsuarioRepository;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

@SpringBootTest(properties = "spring.jpa.hibernate.ddl-auto=validate")
@AutoConfigureMockMvc
@ActiveProfiles("test")
class SecurityIntegrationTest {

  @Autowired private MockMvc mockMvc;
  @Autowired private UsuarioRepository usuarios;
  @Autowired private AcaoRepository acoes;
  @Autowired private CorretoraRepository corretoras;
  @Autowired private PasswordEncoder passwords;
  @MockitoBean private CotacaoAdapter cotacaoAdapter;
  @MockitoBean private BrasilApiClient brasilApiClient;

  @AfterEach
  void limpar() {
    acoes.deleteAll();
    corretoras.deleteAll();
    usuarios.deleteAll();
  }

  @Test
  void loginCreatesAuthenticatedSession() throws Exception {
    criarUsuario("user@test.com", Papel.USER);

    mockMvc
        .perform(
            post("/api/auth/login")
                .contentType("application/json")
                .content(
                    "{\"login\":\"user@test.com\",\"password\":\"senha123\",\"papel\":\"USER\"}"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.senha").doesNotExist())
        .andExpect(jsonPath("$.papel").doesNotExist());
  }

  @Test
  void adminCanListUsersWithOk() throws Exception {
    criarUsuario("admin-list@test.com", Papel.ADMIN);

    mockMvc
        .perform(get("/api/usuarios").with(login("admin-list@test.com")))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content").isArray());
  }

  @Test
  void anonymousCannotListUsersWithUnauthorized() throws Exception {
    mockMvc
        .perform(get("/api/usuarios"))
        .andExpect(status().isUnauthorized())
        .andExpect(jsonPath("$.error").value("UNAUTHORIZED"));
  }

  @Test
  void commonUserCannotListUsersWithForbidden() throws Exception {
    criarUsuario("user-list@test.com", Papel.USER);

    mockMvc
        .perform(get("/api/usuarios").with(login("user-list@test.com")))
        .andExpect(status().isForbidden())
        .andExpect(jsonPath("$.error").value("FORBIDDEN"));
  }

  @Test
  void commonUserCannotReadOrModifyUsers() throws Exception {
    Usuario actor = criarUsuario("user-actor@test.com", Papel.USER);
    Usuario target = criarUsuario("user-target@test.com", Papel.USER);
    RequestPostProcessor session = login(actor.getEmail());

    mockMvc
        .perform(get("/api/usuarios/{id}", target.getId()).with(session))
        .andExpect(status().isForbidden())
        .andExpect(jsonPath("$.error").value("FORBIDDEN"));

    mockMvc
        .perform(
            post("/api/usuarios")
                .with(session)
                .with(csrf())
                .contentType("application/json")
                .content(
                    "{\"login\":\"blocked-create@test.com\",\"password\":\"senha123\",\"papel\":\"USER\"}"))
        .andExpect(status().isForbidden());

    mockMvc
        .perform(
            put("/api/usuarios/{id}", target.getId())
                .with(session)
                .with(csrf())
                .contentType("application/json")
                .content("{\"login\":\"blocked-update@test.com\",\"ativo\":false}"))
        .andExpect(status().isForbidden());

    mockMvc
        .perform(
            put("/api/usuarios/{id}/senha", target.getId())
                .with(session)
                .with(csrf())
                .contentType("application/json")
                .content("{\"password\":\"nova-senha\"}"))
        .andExpect(status().isForbidden());

    mockMvc
        .perform(
            put("/api/usuarios/{id}/papel", target.getId())
                .with(session)
                .with(csrf())
                .contentType("application/json")
                .content("{\"papel\":\"ADMIN\"}"))
        .andExpect(status().isForbidden());

    org.assertj.core.api.Assertions.assertThat(usuarios.count()).isEqualTo(2);
    org.assertj.core.api.Assertions.assertThat(
            usuarios.findById(target.getId()).orElseThrow().getPapel())
        .isEqualTo(Papel.USER);
    org.assertj.core.api.Assertions.assertThat(
            usuarios.findById(target.getId()).orElseThrow().getEmail())
        .isEqualTo("user-target@test.com");
  }

  @Test
  void userCannotUseAdministrativeActionEndpoint() throws Exception {
    criarUsuario("user@test.com", Papel.USER);
    RequestPostProcessor session = login("user@test.com");

    mockMvc
        .perform(
            post("/api/acoes")
                .with(session)
                .with(csrf())
                .contentType("application/json")
                .content("{\"ticker\":\"PETR4\",\"mercado\":\"BRASIL\"}"))
        .andExpect(status().isForbidden())
        .andExpect(jsonPath("$.error").value("FORBIDDEN"));
  }

  @Test
  void userCannotUseAdministrativeBrokerEndpoint() throws Exception {
    criarUsuario("user-broker@test.com", Papel.USER);

    mockMvc
        .perform(
            post("/api/corretoras")
                .with(login("user-broker@test.com"))
                .with(csrf())
                .contentType("application/json")
                .content("{\"cnpj\":\"61.855.045/0001-32\"}"))
        .andExpect(status().isForbidden())
        .andExpect(jsonPath("$.error").value("FORBIDDEN"));

    mockMvc
        .perform(delete("/api/acoes/{id}", 1L).with(login("user-broker@test.com")).with(csrf()))
        .andExpect(status().isForbidden());
    mockMvc
        .perform(
            delete("/api/corretoras/{id}", 1L).with(login("user-broker@test.com")).with(csrf()))
        .andExpect(status().isForbidden());

    org.assertj.core.api.Assertions.assertThat(corretoras.count()).isZero();
  }

  @Test
  void anonymousCannotUseAdministrativeOrQuoteMutationEndpoints() throws Exception {
    mockMvc
        .perform(
            post("/api/acoes")
                .contentType("application/json")
                .content("{\"ticker\":\"PETR4\",\"mercado\":\"BRASIL\"}"))
        .andExpect(status().isUnauthorized())
        .andExpect(jsonPath("$.error").value("UNAUTHORIZED"));
    mockMvc
        .perform(post("/api/acoes/{id}/cotacao", 1L))
        .andExpect(status().isUnauthorized())
        .andExpect(jsonPath("$.error").value("UNAUTHORIZED"));
    org.assertj.core.api.Assertions.assertThat(acoes.count()).isZero();
  }

  @Test
  void userCannotRefreshActionQuote() throws Exception {
    criarUsuario("user-quote@test.com", Papel.USER);

    mockMvc
        .perform(
            post("/api/acoes/{id}/cotacao", 1L).with(login("user-quote@test.com")).with(csrf()))
        .andExpect(status().isForbidden())
        .andExpect(jsonPath("$.error").value("FORBIDDEN"));
  }

  @Test
  void adminCanUseAdministrativeActionEndpoint() throws Exception {
    criarUsuario("admin@test.com", Papel.ADMIN);
    when(cotacaoAdapter.consultar("PETR4", com.carteiraacoesbackend.domains.enums.Mercado.BRASIL))
        .thenReturn(new Cotacao(new BigDecimal("30"), OffsetDateTime.now(), "Petrobras"));

    mockMvc
        .perform(
            post("/api/acoes")
                .with(login("admin@test.com"))
                .with(csrf())
                .contentType("application/json")
                .content("{\"ticker\":\"PETR4\",\"mercado\":\"BRASIL\"}"))
        .andExpect(status().isCreated());
  }

  @Test
  void adminCanUseAdministrativeBrokerEndpoint() throws Exception {
    criarUsuario("admin-broker@test.com", Papel.ADMIN);
    when(brasilApiClient.consultarCnpj("61855045000132"))
        .thenReturn(
            new BrasilApiCnpjResponse(
                "BRADESCO S/A CTVM",
                "BRADESCO",
                "contato@bradesco.com.br",
                "1138479888",
                "ATIVA",
                "04543011",
                "Avenida Presidente Juscelino Kubitschek",
                "1309",
                null,
                "Vila Nova Conceição",
                "São Paulo",
                "SP"));
    when(brasilApiClient.consultarCorretoraCvm("61855045000132"))
        .thenReturn(
            new BrasilApiCvmBrokerResponse(
                "61.855.045/0001-32", "2038", "EM FUNCIONAMENTO NORMAL"));

    mockMvc
        .perform(
            post("/api/corretoras")
                .with(login("admin-broker@test.com"))
                .with(csrf())
                .contentType("application/json")
                .content("{\"cnpj\":\"61.855.045/0001-32\"}"))
        .andExpect(status().isCreated());
  }

  @Test
  void adminCanPromoteUser() throws Exception {
    mockMvc
        .perform(
            post("/api/usuarios")
                .with(user("admin").roles("ADMIN"))
                .contentType("application/json")
                .content(
                    "{\"login\":\"ana@test.com\",\"password\":\"senha123\",\"papel\":\"USER\"}"))
        .andExpect(status().isCreated());

    mockMvc
        .perform(
            put(
                    "/api/usuarios/{id}/papel",
                    usuarios.findByEmail("ana@test.com").orElseThrow().getId())
                .with(user("admin").roles("ADMIN"))
                .with(csrf())
                .contentType("application/json")
                .content("{\"papel\":\"ADMIN\"}"))
        .andExpect(status().isOk());
    org.assertj.core.api.Assertions.assertThat(
            usuarios.findByEmail("ana@test.com").orElseThrow().getPapel())
        .isEqualTo(Papel.ADMIN);
  }

  @Test
  void commonUserCannotPromoteAnotherUser() throws Exception {
    criarUsuario("user-promoter@test.com", Papel.USER);
    criarUsuario("target@test.com", Papel.USER);

    mockMvc
        .perform(
            put(
                    "/api/usuarios/{id}/papel",
                    usuarios.findByEmail("target@test.com").orElseThrow().getId())
                .with(login("user-promoter@test.com"))
                .with(csrf())
                .contentType("application/json")
                .content("{\"papel\":\"ADMIN\"}"))
        .andExpect(status().isForbidden());
  }

  private Usuario criarUsuario(String email, Papel papel) {
    Usuario usuario = new Usuario();
    usuario.setNome(email);
    usuario.setEmail(email);
    usuario.setSenha(passwords.encode("senha123"));
    usuario.setPapel(papel);
    return usuarios.save(usuario);
  }

  private RequestPostProcessor login(String email) throws Exception {
    String response =
        mockMvc
            .perform(
                post("/api/auth/login")
                    .contentType("application/json")
                    .content(
                        "{\"login\":\""
                            + email
                            + "\",\"password\":\"senha123\",\"papel\":\"USER\"}"))
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
}
