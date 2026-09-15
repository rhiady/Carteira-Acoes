package com.carteiraacoesbackend.resources;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.carteiraacoesbackend.repositories.UsuarioRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest(properties = "spring.jpa.hibernate.ddl-auto=validate")
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AuthControllerIntegrationTest {

  @Autowired private MockMvc mockMvc;
  @Autowired private UsuarioRepository usuarios;

  @AfterEach
  void limparUsuarios() {
    usuarios.deleteAll();
  }

  @Test
  void logsInWithValidEmailAndPasswordWithoutExposingPassword() throws Exception {
    criarUsuario();

    mockMvc
        .perform(
            post("/auth/login")
                .contentType("application/json")
                .content("{\"email\":\"ana@email.com\",\"senha\":\"senha123\"}"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").isNumber())
        .andExpect(jsonPath("$.nome").value("Ana"))
        .andExpect(jsonPath("$.email").value("ana@email.com"))
        .andExpect(jsonPath("$.senha").doesNotExist())
        .andExpect(jsonPath("$.password").doesNotExist());
  }

  @Test
  void rejectsUnknownEmailWithGenericCredentialsError() throws Exception {
    mockMvc
        .perform(
            post("/auth/login")
                .contentType("application/json")
                .content("{\"email\":\"naoexiste@email.com\",\"senha\":\"senha123\"}"))
        .andExpect(status().isUnauthorized())
        .andExpect(jsonPath("$.error").value("CREDENCIAIS_INVALIDAS"))
        .andExpect(jsonPath("$.message").value("Email ou senha inválidos."));
  }

  @Test
  void rejectsIncorrectPasswordWithSameGenericCredentialsError() throws Exception {
    criarUsuario();

    mockMvc
        .perform(
            post("/auth/login")
                .contentType("application/json")
                .content("{\"email\":\"ana@email.com\",\"senha\":\"errada\"}"))
        .andExpect(status().isUnauthorized())
        .andExpect(jsonPath("$.error").value("CREDENCIAIS_INVALIDAS"))
        .andExpect(jsonPath("$.message").value("Email ou senha inválidos."));
  }

  @Test
  void rejectsInvalidEmail() throws Exception {
    mockMvc
        .perform(
            post("/auth/login")
                .contentType("application/json")
                .content("{\"email\":\"invalido\",\"senha\":\"senha123\"}"))
        .andExpect(status().isBadRequest());
  }

  @Test
  void rejectsEmptyPassword() throws Exception {
    mockMvc
        .perform(
            post("/auth/login")
                .contentType("application/json")
                .content("{\"email\":\"ana@email.com\",\"senha\":\"\"}"))
        .andExpect(status().isBadRequest());
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
