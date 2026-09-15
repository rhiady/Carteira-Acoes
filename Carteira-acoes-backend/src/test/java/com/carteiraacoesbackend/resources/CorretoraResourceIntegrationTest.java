package com.carteiraacoesbackend.resources;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.carteiraacoesbackend.clients.BrasilApiClient;
import com.carteiraacoesbackend.dto.integrations.BrasilApiCepResponse;
import com.carteiraacoesbackend.dto.integrations.BrasilApiCnpjResponse;
import com.carteiraacoesbackend.dto.integrations.BrasilApiCvmBrokerResponse;
import com.carteiraacoesbackend.exceptions.ApiException;
import com.carteiraacoesbackend.exceptions.GlobalExceptionHandler;
import com.carteiraacoesbackend.facades.CnpjFacade;
import com.carteiraacoesbackend.facades.CvmFacade;
import com.carteiraacoesbackend.mappers.CorretoraMapper;
import com.carteiraacoesbackend.repositories.CorretoraRepository;
import com.carteiraacoesbackend.services.CorretoraService;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

class CorretoraResourceIntegrationTest {

  private final List<com.carteiraacoesbackend.domains.Corretora> persistidas = new ArrayList<>();
  private FakeBrasilApiClient brasilApiClient;
  private MockMvc mockMvc;

  @BeforeEach
  void prepararEndpoint() {
    brasilApiClient = new FakeBrasilApiClient();
    CorretoraService service =
        new CorretoraService(
            repositorioEmMemoria(),
            new CnpjFacade(brasilApiClient),
            new CvmFacade(brasilApiClient),
            new CorretoraMapper());
    mockMvc =
        MockMvcBuilders.standaloneSetup(new CorretoraResource(service))
            .setControllerAdvice(new GlobalExceptionHandler())
            .build();
  }

  @Test
  void createsBrokerWhenCvmReturnsActiveRecord() throws Exception {
    brasilApiClient.configurarCorretora(
        new BrasilApiCvmBrokerResponse("61.855.045/0001-32", "2038", "EM FUNCIONAMENTO NORMAL"));

    mockMvc
        .perform(
            post("/corretoras")
                .contentType("application/json")
                .content("{\"cnpj\":\"61.855.045/0001-32\"}"))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.cnpj").value("61855045000132"))
        .andExpect(jsonPath("$.registroCvm").value("2038"));
  }

  @Test
  void doesNotPersistBrokerMissingFromCvm() throws Exception {
    mockMvc
        .perform(
            post("/corretoras")
                .contentType("application/json")
                .content("{\"cnpj\":\"60.746.948/0001-12\"}"))
        .andExpect(status().isUnprocessableEntity())
        .andExpect(jsonPath("$.error").value("CORRETORA_NAO_REGISTRADA_CVM"));

    org.junit.jupiter.api.Assertions.assertEquals(0, persistidas.size());
  }

  @Test
  void rejectsMalformedCnpjBeforeCallingTheDomain() throws Exception {
    mockMvc
        .perform(
            post("/corretoras").contentType("application/json").content("{\"cnpj\":\"123ABC\"}"))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.error").value("VALIDATION_ERROR"));
    mockMvc
        .perform(get("/corretoras/cnpj/{cnpj}", "123ABC"))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.error").value("CNPJ_INVALIDO"));

    org.junit.jupiter.api.Assertions.assertEquals(0, persistidas.size());
  }

  private CorretoraRepository repositorioEmMemoria() {
    return (CorretoraRepository)
        Proxy.newProxyInstance(
            getClass().getClassLoader(),
            new Class<?>[] {CorretoraRepository.class},
            (proxy, method, args) ->
                switch (method.getName()) {
                  case "existsByCnpj" ->
                      persistidas.stream().anyMatch(c -> c.getCnpj().equals(args[0]));
                  case "save" -> {
                    com.carteiraacoesbackend.domains.Corretora corretora =
                        (com.carteiraacoesbackend.domains.Corretora) args[0];
                    corretora.setId((long) persistidas.size() + 1);
                    persistidas.add(corretora);
                    yield corretora;
                  }
                  default -> throw new UnsupportedOperationException(method.getName());
                });
  }

  private static class FakeBrasilApiClient implements BrasilApiClient {
    private BrasilApiCvmBrokerResponse corretora;

    void configurarCorretora(BrasilApiCvmBrokerResponse corretora) {
      this.corretora = corretora;
    }

    @Override
    public BrasilApiCnpjResponse consultarCnpj(String cnpj) {
      return new BrasilApiCnpjResponse(
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
          "SP");
    }

    @Override
    public BrasilApiCepResponse consultarCep(String cep) {
      throw new UnsupportedOperationException();
    }

    @Override
    public List<BrasilApiCvmBrokerResponse> listarCorretorasCvm() {
      throw new UnsupportedOperationException();
    }

    @Override
    public BrasilApiCvmBrokerResponse consultarCorretoraCvm(String cnpj) {
      if (corretora == null)
        throw ApiException.notFound(
            "EXTERNAL_RESOURCE_NOT_FOUND", "Recurso não encontrado no provedor externo.");
      return corretora;
    }
  }
}
