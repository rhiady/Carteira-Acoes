package com.carteiraacoesbackend.facades;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.carteiraacoesbackend.clients.BrasilApiClient;
import com.carteiraacoesbackend.dto.integrations.BrasilApiCepResponse;
import com.carteiraacoesbackend.dto.integrations.BrasilApiCnpjResponse;
import com.carteiraacoesbackend.dto.integrations.BrasilApiCvmBrokerResponse;
import com.carteiraacoesbackend.exceptions.ApiException;
import java.util.List;
import org.junit.jupiter.api.Test;

class CepFacadeTest {

  @Test
  void normalizesCepAndAcceptsAValidResponse() {
    StubClient client =
        new StubClient(new BrasilApiCepResponse("Praça da Sé", "Sé", "São Paulo", "SP"));

    BrasilApiCepResponse response = new CepFacade(client).consultar("01001-000");

    assertEquals("01001000", client.lastCep);
    assertEquals("Praça da Sé", response.street());
  }

  @Test
  void rejectsMalformedCepBeforeCallingProvider() {
    StubClient client = new StubClient(null);

    ApiException error =
        assertThrows(ApiException.class, () -> new CepFacade(client).consultar("123"));

    assertEquals("CEP_INVALIDO", error.getCode());
    assertEquals(null, client.lastCep);
  }

  @Test
  void translatesEmptyBodyAndNetworkFailure() {
    StubClient emptyClient = new StubClient(null);
    ApiException empty =
        assertThrows(ApiException.class, () -> new CepFacade(emptyClient).consultar("01001000"));
    assertEquals("EXTERNAL_API_UNAVAILABLE", empty.getCode());

    StubClient failingClient = new StubClient(null);
    failingClient.failure = new RuntimeException("network details must not escape");
    ApiException failure =
        assertThrows(ApiException.class, () -> new CepFacade(failingClient).consultar("01001000"));
    assertEquals("EXTERNAL_API_UNAVAILABLE", failure.getCode());
    assertEquals("Não foi possível consultar o CEP.", failure.getMessage());
  }

  private static final class StubClient implements BrasilApiClient {
    private final BrasilApiCepResponse response;
    private RuntimeException failure;
    private String lastCep;

    private StubClient(BrasilApiCepResponse response) {
      this.response = response;
    }

    @Override
    public BrasilApiCepResponse consultarCep(String cep) {
      lastCep = cep;
      if (failure != null) throw failure;
      return response;
    }

    @Override
    public BrasilApiCnpjResponse consultarCnpj(String cnpj) {
      throw new UnsupportedOperationException();
    }

    @Override
    public List<BrasilApiCvmBrokerResponse> listarCorretorasCvm() {
      throw new UnsupportedOperationException();
    }

    @Override
    public BrasilApiCvmBrokerResponse consultarCorretoraCvm(String cnpj) {
      throw new UnsupportedOperationException();
    }
  }
}
