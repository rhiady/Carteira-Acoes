package com.carteiraacoesbackend.resources;

import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.carteiraacoesbackend.domains.Usuario;
import com.carteiraacoesbackend.domains.enums.Papel;
import com.carteiraacoesbackend.integrations.Cotacao;
import com.carteiraacoesbackend.integrations.CotacaoAdapter;
import com.carteiraacoesbackend.repositories.UsuarioRepository;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest(properties = "spring.jpa.hibernate.ddl-auto=validate")
@AutoConfigureMockMvc
@ActiveProfiles("test")
class SecurityIntegrationTest {

  @Autowired private MockMvc mockMvc;
  @Autowired private UsuarioRepository usuarios;
  @Autowired private PasswordEncoder passwords;
  @MockitoBean private CotacaoAdapter cotacaoAdapter;

  @AfterEach
  void limpar() {
    usuarios.deleteAll();
  }

  @Test
  void loginCreatesAuthenticatedSession() throws Exception {
    criarUsuario("user@test.com", Papel.USER);

    mockMvc
        .perform(
            post("/auth/login")
                .contentType("application/json")
                .content("{\"email\":\"user@test.com\",\"senha\":\"senha123\"}"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.senha").doesNotExist())
        .andExpect(jsonPath("$.papel").doesNotExist());
  }

  @Test
  void userCannotUseAdministrativeActionEndpoint() throws Exception {
    criarUsuario("user@test.com", Papel.USER);
    MockHttpSession session = login("user@test.com");

    mockMvc
        .perform(
            post("/acoes")
                .session(session)
                .with(csrf())
                .contentType("application/json")
                .content("{\"ticker\":\"PETR4\",\"mercado\":\"BRASIL\"}"))
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
            post("/acoes")
                .session(login("admin@test.com"))
                .with(csrf())
                .contentType("application/json")
                .content("{\"ticker\":\"PETR4\",\"mercado\":\"BRASIL\"}"))
        .andExpect(status().isCreated());
  }

  @Test
  void registrationCannotElevateRole() throws Exception {
    mockMvc
        .perform(
            post("/usuarios")
                .contentType("application/json")
                .content(
                    "{\"nome\":\"Ana\",\"email\":\"ana@test.com\",\"senha\":\"senha123\",\"papel\":\"ADMIN\"}"))
        .andExpect(status().isCreated());

    org.assertj.core.api.Assertions.assertThat(
            usuarios.findByEmail("ana@test.com").orElseThrow().getPapel())
        .isEqualTo(Papel.USER);
  }

  private Usuario criarUsuario(String email, Papel papel) {
    Usuario usuario = new Usuario();
    usuario.setNome(email);
    usuario.setEmail(email);
    usuario.setSenha(passwords.encode("senha123"));
    usuario.setPapel(papel);
    return usuarios.save(usuario);
  }

  private MockHttpSession login(String email) throws Exception {
    return (MockHttpSession)
        mockMvc
            .perform(
                post("/auth/login")
                    .contentType("application/json")
                    .content("{\"email\":\"" + email + "\",\"senha\":\"senha123\"}"))
            .andExpect(status().isOk())
            .andReturn()
            .getRequest()
            .getSession();
  }
}
