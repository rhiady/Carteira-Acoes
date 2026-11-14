package com.carteiraacoesbackend.resources;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.carteiraacoesbackend.repositories.UsuarioRepository;
import java.time.Instant;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest(properties = "spring.jpa.hibernate.ddl-auto=validate")
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AuthControllerIntegrationTest {

  @Autowired private MockMvc mockMvc;
  @Autowired private UsuarioRepository usuarios;
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
                    .content("{\"username\":\"ana@email.com\",\"password\":\"senha123\"}"))
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
  }

  @Test
  void authenticatesMeFromBearerToken() throws Exception {
    criarUsuario();
    String token = login();

    mockMvc
        .perform(get("/api/auth/me").header("Authorization", "Bearer " + token))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.username").value("ana@email.com"))
        .andExpect(jsonPath("$.authorities").value(org.hamcrest.Matchers.hasItem("ROLE_USER")));
  }

  @Test
  void rejectsUnknownEmailWithGenericCredentialsError() throws Exception {
    mockMvc
        .perform(
            post("/api/auth/login")
                .contentType("application/json")
                .content("{\"username\":\"naoexiste@email.com\",\"password\":\"senha123\"}"))
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
                .content("{\"username\":\"ana@email.com\",\"password\":\"errada\"}"))
        .andExpect(status().isUnauthorized())
        .andExpect(jsonPath("$.error").value("CREDENCIAIS_INVALIDAS"));
  }

  @Test
  void rejectsInvalidEmail() throws Exception {
    mockMvc
        .perform(
            post("/api/auth/login")
                .contentType("application/json")
                .content("{\"username\":\"invalido\",\"password\":\"senha123\"}"))
        .andExpect(status().isBadRequest());
  }

  @Test
  void rejectsEmptyPassword() throws Exception {
    mockMvc
        .perform(
            post("/api/auth/login")
                .contentType("application/json")
                .content("{\"username\":\"ana@email.com\",\"password\":\"\"}"))
        .andExpect(status().isBadRequest());
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
    String tampered = token.substring(0, token.length() - 1) + (token.endsWith("a") ? "b" : "a");

    mockMvc
        .perform(get("/api/auth/me").header("Authorization", "Bearer " + tampered))
        .andExpect(status().isUnauthorized());
  }

  private String login() throws Exception {
    return mockMvc
        .perform(
            post("/api/auth/login")
                .contentType("application/json")
                .content("{\"username\":\"ana@email.com\",\"password\":\"senha123\"}"))
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
            post("/usuarios")
                .contentType("application/json")
                .content("{\"nome\":\"Ana\",\"email\":\"ana@email.com\",\"senha\":\"senha123\"}"))
        .andExpect(status().isCreated());
  }
}
