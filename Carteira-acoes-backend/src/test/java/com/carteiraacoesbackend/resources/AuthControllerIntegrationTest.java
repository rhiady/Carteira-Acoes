package com.carteiraacoesbackend.resources;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.carteiraacoesbackend.repositories.UsuarioRepository;
import com.carteiraacoesbackend.domains.Usuario;
import com.carteiraacoesbackend.domains.enums.Papel;
import java.time.Instant;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;

@SpringBootTest(properties = "spring.jpa.hibernate.ddl-auto=validate")
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AuthControllerIntegrationTest {

  @Autowired private MockMvc mockMvc;
  @Autowired private UsuarioRepository usuarios;
  @Autowired private PasswordEncoder passwordEncoder;
  @Autowired private JwtDecoder jwtDecoder;
  @Autowired private JwtEncoder jwtEncoder;

  @AfterEach
  void limparUsuarios() {
    usuarios.deleteAll();
  }

  @Test
  void logsInWithValidCredentialsAndReturnsSignedToken() throws Exception {
    criarUsuario();

    String response =
        mockMvc
            .perform(
                post("/api/auth/login")
                    .contentType("application/json")
                    .content("{\"login\":\"ana@email.com\",\"password\":\"senha123\"}"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.token").isString())
            .andExpect(jsonPath("$.tipo").value("Bearer"))
            .andExpect(jsonPath("$.expiresIn").isNumber())
            .andExpect(jsonPath("$.senha").doesNotExist())
            .andReturn()
            .getResponse()
            .getContentAsString();
    String token = response.replaceAll(".*\\\"token\\\":\\\"([^\\\"]+)\\\".*", "$1");
    org.assertj.core.api.Assertions.assertThat(jwtDecoder.decode(token).getSubject())
        .isEqualTo("ana@email.com");
    org.assertj.core.api.Assertions.assertThat(
            jwtDecoder.decode(token).getClaimAsStringList("authorities"))
        .containsExactly("ROLE_USER");
  }

  @Test
  void logsInWithUsernameAlias() throws Exception {
    criarUsuario();

    mockMvc
        .perform(
            post("/api/auth/login")
                .contentType("application/json")
                .content("{\"username\":\"ana@email.com\",\"password\":\"senha123\"}"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.token").isString());
  }

  @Test
  void authenticatesMeFromBearerToken() throws Exception {
    criarUsuario();
    Long usuarioId = usuarios.findByEmail("ana@email.com").orElseThrow().getId();
    String token = login();

    mockMvc
        .perform(get("/api/auth/me").header("Authorization", "Bearer " + token))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(usuarioId))
        .andExpect(jsonPath("$.login").value("ana@email.com"))
        .andExpect(jsonPath("$.authorities").value(org.hamcrest.Matchers.contains("ROLE_USER")));
  }

  @Test
  @DisplayName("AC-001: usuário autenticado redefine a própria senha @spec:AC-001")
  void authenticatedUserCanRedefineOwnPassword() throws Exception {
    criarUsuario();
    String token = login();

    mockMvc
        .perform(
            put("/api/auth/senha")
                .header("Authorization", "Bearer " + token)
                .contentType("application/json")
                .content("{\"password\":\"nova-senha\"}"))
        .andExpect(status().isNoContent());

    mockMvc
        .perform(
            post("/api/auth/login")
                .contentType("application/json")
                .content("{\"login\":\"ana@email.com\",\"password\":\"nova-senha\"}"))
        .andExpect(status().isOk());
  }

  @Test
  @DisplayName("AC-002: usuário não autenticado não redefine senha @spec:AC-002")
  void unauthenticatedUserCannotRedefinePassword() throws Exception {
    mockMvc
        .perform(
            put("/api/auth/senha")
                .contentType("application/json")
                .content("{\"password\":\"nova-senha\"}"))
        .andExpect(status().isUnauthorized());
  }

  @Test
  @DisplayName("AC-003: senha inválida é rejeitada @spec:AC-003")
  void invalidNewPasswordIsRejected() throws Exception {
    criarUsuario();
    String token = login();

    mockMvc
        .perform(
            put("/api/auth/senha")
                .header("Authorization", "Bearer " + token)
                .contentType("application/json")
                .content("{\"password\":\"curta\"}"))
        .andExpect(status().isBadRequest());
  }

  @Test
  void rejectsUnknownEmailWithGenericCredentialsError() throws Exception {
    mockMvc
        .perform(
            post("/api/auth/login")
                .contentType("application/json")
                .content("{\"login\":\"naoexiste@email.com\",\"password\":\"senha123\"}"))
        .andExpect(status().isUnauthorized())
        .andExpect(jsonPath("$.error").value("CREDENCIAIS_INVALIDAS"));
  }

  @Test
  void rejectsIncorrectPasswordWithSameGenericCredentialsError() throws Exception {
    criarUsuario();

    mockMvc
        .perform(
            post("/api/auth/login")
                .contentType("application/json")
                .content("{\"login\":\"ana@email.com\",\"password\":\"errada\"}"))
        .andExpect(status().isUnauthorized())
        .andExpect(jsonPath("$.error").value("CREDENCIAIS_INVALIDAS"));
  }

  @Test
  void rejectsBlankLogin() throws Exception {
    mockMvc
        .perform(
            post("/api/auth/login")
                .contentType("application/json")
                .content("{\"login\":\"\",\"password\":\"senha123\"}"))
        .andExpect(status().isBadRequest());
  }

  @Test
  void rejectsEmptyPassword() throws Exception {
    mockMvc
        .perform(
            post("/api/auth/login")
                .contentType("application/json")
                .content("{\"login\":\"ana@email.com\",\"password\":\"\"}"))
        .andExpect(status().isBadRequest());
  }

  @Test
  void rejectsLoginForDisabledUser() throws Exception {
    Usuario usuario = new Usuario();
    usuario.setNome("desativado@email.com");
    usuario.setEmail("desativado@email.com");
    usuario.setSenha(passwordEncoder.encode("senha123"));
    usuario.setPapel(Papel.USER);
    usuario.setAtivo(false);
    usuarios.save(usuario);

    mockMvc
        .perform(
            post("/api/auth/login")
                .contentType("application/json")
                .content("{\"login\":\"desativado@email.com\",\"password\":\"senha123\"}"))
        .andExpect(status().isUnauthorized())
        .andExpect(jsonPath("$.error").value("CREDENCIAIS_INVALIDAS"));
  }

  @Test
  void rejectsExpiredToken() throws Exception {
    mockMvc
        .perform(
            get("/api/auth/me")
                .header("Authorization", "Bearer " + token("carteiracoes-test", Instant.now().minusSeconds(120))))
        .andExpect(status().isUnauthorized());
  }

  @Test
  void rejectsTokenFromAnotherIssuer() throws Exception {
    mockMvc
        .perform(
            get("/api/auth/me")
                .header("Authorization", "Bearer " + token("outro-issuer", Instant.now().plusSeconds(60))))
        .andExpect(status().isUnauthorized());
  }

  @Test
  void rejectsTamperedToken() throws Exception {
    criarUsuario();
    String token = login();
    String tampered = token + "x";

    mockMvc
        .perform(get("/api/auth/me").header("Authorization", "Bearer " + tampered))
        .andExpect(status().isUnauthorized());
  }

  private String login() throws Exception {
    return mockMvc
        .perform(
            post("/api/auth/login")
                .contentType("application/json")
                .content("{\"login\":\"ana@email.com\",\"password\":\"senha123\"}"))
        .andExpect(status().isOk())
        .andReturn()
        .getResponse()
        .getContentAsString()
        .replaceAll(".*\\\"token\\\":\\\"([^\\\"]+)\\\".*", "$1");
  }

  private String token(String issuer, Instant expiresAt) {
    JwtClaimsSet claims =
        JwtClaimsSet.builder()
            .issuer(issuer)
            .issuedAt(expiresAt.minusSeconds(60))
            .expiresAt(expiresAt)
            .subject("ana@email.com")
            .claim("authorities", java.util.List.of("ROLE_USER"))
            .build();
    return jwtEncoder
        .encode(
            JwtEncoderParameters.from(
                JwsHeader.with(MacAlgorithm.HS256).type("JWT").build(), claims))
        .getTokenValue();
  }

  private void criarUsuario() throws Exception {
    mockMvc
        .perform(
            post("/api/usuarios").with(user("admin").roles("ADMIN"))
                .contentType("application/json")
                .content("{\"login\":\"ana@email.com\",\"password\":\"senha123\",\"papel\":\"USER\"}"))
        .andExpect(status().isCreated());
  }
}
