package com.carteiraacoesbackend.resources;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.carteiraacoesbackend.domains.Acao;
import com.carteiraacoesbackend.domains.Carteira;
import com.carteiraacoesbackend.domains.Corretora;
import com.carteiraacoesbackend.domains.Usuario;
import com.carteiraacoesbackend.domains.enums.Mercado;
import com.carteiraacoesbackend.domains.enums.Moeda;
import com.carteiraacoesbackend.domains.enums.Papel;
import com.carteiraacoesbackend.repositories.AcaoRepository;
import com.carteiraacoesbackend.repositories.CarteiraRepository;
import com.carteiraacoesbackend.repositories.CorretoraRepository;
import com.carteiraacoesbackend.repositories.OperacaoRepository;
import com.carteiraacoesbackend.repositories.UsuarioRepository;
import java.math.BigDecimal;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest(properties = "spring.jpa.hibernate.ddl-auto=validate")
@AutoConfigureMockMvc
@ActiveProfiles("test")
class UserOwnershipIntegrationTest {
  @Autowired MockMvc mockMvc;
  @Autowired UsuarioRepository usuarios;
  @Autowired CarteiraRepository carteiras;
  @Autowired CorretoraRepository corretoras;
  @Autowired AcaoRepository acoes;
  @Autowired OperacaoRepository operacoes;
  @Autowired PasswordEncoder passwords;

  @AfterEach
  void limpar() {
    operacoes.deleteAll();
    carteiras.deleteAll();
    acoes.deleteAll();
    corretoras.deleteAll();
    usuarios.deleteAll();
  }

  @Test
  void userCannotReadAnotherUsersPortfolioOrForgeUserId() throws Exception {
    Usuario ana = usuario("ana@test.com");
    Usuario joao = usuario("joao@test.com");
    Corretora corretora = corretora("11111111000111");
    Carteira carteiraAna = carteira("Ana", ana, corretora);
    Carteira carteiraJoao = carteira("Joao", joao, corretora);
    MockHttpSession session = login("ana@test.com");

    mockMvc
        .perform(get("/carteiras/{id}", carteiraAna.getId()).session(session))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.nome").value("Ana"));
    mockMvc
        .perform(get("/carteiras/{id}", carteiraJoao.getId()).session(session))
        .andExpect(status().isNotFound());
    mockMvc
        .perform(get("/carteiras/{id}/acoes", carteiraJoao.getId()).session(session))
        .andExpect(status().isNotFound());
    mockMvc
        .perform(get("/carteiras/{id}/resumo", carteiraJoao.getId()).session(session))
        .andExpect(status().isNotFound());
    mockMvc
        .perform(get("/operacoes/carteiras/{id}", carteiraJoao.getId()).session(session))
        .andExpect(status().isNotFound());
    mockMvc
        .perform(get("/usuarios/{id}/carteiras", joao.getId()).session(session))
        .andExpect(status().isNotFound());
    mockMvc
        .perform(get("/carteiras").session(session))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content.length()").value(1))
        .andExpect(jsonPath("$.content[0].nome").value("Ana"));
  }

  @Test
  void purchaseInAnotherUsersPortfolioDoesNotPersist() throws Exception {
    Usuario ana = usuario("ana-buy@test.com");
    Usuario joao = usuario("joao-buy@test.com");
    Corretora corretora = corretora("22222222000122");
    Carteira carteiraJoao = carteira("Joao", joao, corretora);
    Acao acao = new Acao();
    acao.setTicker("PETR4");
    acao.setNomeEmpresa("Petrobras");
    acao.setMercado(Mercado.BRASIL);
    acao.setMoeda(Moeda.BRL);
    acao.setCotacaoAtual(new BigDecimal("30"));
    acao = acoes.save(acao);
    long antes = operacoes.count();

    mockMvc
        .perform(
            post("/operacoes/compras")
                .session(login("ana-buy@test.com"))
                .with(csrf())
                .contentType("application/json")
                .content(
                    "{\"carteiraId\":"
                        + carteiraJoao.getId()
                        + ",\"acaoId\":"
                        + acao.getId()
                        + ",\"quantidade\":1,\"precoUnitario\":30}"))
        .andExpect(status().isNotFound());
    assertThat(operacoes.count()).isEqualTo(antes);
    mockMvc
        .perform(
            post("/operacoes/vendas")
                .session(login("ana-buy@test.com"))
                .with(csrf())
                .contentType("application/json")
                .content(
                    "{\"carteiraId\":"
                        + carteiraJoao.getId()
                        + ",\"acaoId\":"
                        + acao.getId()
                        + ",\"quantidade\":1,\"precoUnitario\":30}"))
        .andExpect(status().isNotFound());
    assertThat(operacoes.count()).isEqualTo(antes);
  }

  @Test
  void userRoutesRequireAuthenticationAndHideOtherUsers() throws Exception {
    Usuario ana = usuario("ana-profile@test.com");
    Usuario joao = usuario("joao-profile@test.com");
    MockHttpSession session = login("ana-profile@test.com");

    mockMvc.perform(get("/usuarios")).andExpect(status().isUnauthorized());
    mockMvc.perform(get("/usuarios/{id}", ana.getId())).andExpect(status().isUnauthorized());
    mockMvc
        .perform(get("/usuarios").session(session))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.totalElements").value(1))
        .andExpect(jsonPath("$.content[0].id").value(ana.getId()))
        .andExpect(jsonPath("$.content[0].senha").doesNotExist());
    mockMvc
        .perform(get("/usuarios/{id}", joao.getId()).session(session))
        .andExpect(status().isNotFound());
    mockMvc
        .perform(
            put("/usuarios/{id}", joao.getId())
                .session(session)
                .with(csrf())
                .contentType("application/json")
                .content(
                    "{\"nome\":\"Invadido\",\"email\":\"invadido@test.com\",\"senha\":\"nova\"}"))
        .andExpect(status().isNotFound());

    assertThat(usuarios.findById(joao.getId()).orElseThrow().getEmail())
        .isEqualTo("joao-profile@test.com");
  }

  @Test
  void forgedUserIdCannotAssignANewPortfolioToAnotherUser() throws Exception {
    Usuario ana = usuario("ana-create@test.com");
    Usuario joao = usuario("joao-create@test.com");
    Corretora corretora = corretora("33333333000133");

    String body =
        mockMvc
            .perform(
                post("/carteiras")
                    .session(login("ana-create@test.com"))
                    .with(csrf())
                    .contentType("application/json")
                    .content(
                        "{\"nome\":\"Forjada\",\"usuarioId\":"
                            + joao.getId()
                            + ",\"corretoraId\":"
                            + corretora.getId()
                            + "}"))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.usuarioId").value(ana.getId()))
            .andReturn()
            .getResponse()
            .getContentAsString();

    long carteiraId = Long.parseLong(body.replaceAll(".*\\\"id\\\":(\\d+).*", "$1"));
    assertThat(carteiras.findById(carteiraId).orElseThrow().getUsuario().getId())
        .isEqualTo(ana.getId());
  }

  private Usuario usuario(String email) {
    Usuario usuario = new Usuario();
    usuario.setNome(email);
    usuario.setEmail(email);
    usuario.setSenha(passwords.encode("senha123"));
    usuario.setPapel(Papel.USER);
    return usuarios.save(usuario);
  }

  private Carteira carteira(String nome, Usuario usuario, Corretora corretora) {
    Carteira carteira = new Carteira();
    carteira.setNome(nome);
    carteira.setUsuario(usuario);
    carteira.setCorretora(corretora);
    return carteiras.save(carteira);
  }

  private Corretora corretora(String cnpj) {
    Corretora corretora = new Corretora();
    corretora.setCnpj(cnpj);
    corretora.setRazaoSocial("Corretora " + cnpj);
    corretora.setNomeFantasia("Corretora");
    return corretoras.save(corretora);
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
