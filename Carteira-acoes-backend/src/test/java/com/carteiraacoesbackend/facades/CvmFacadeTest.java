package com.carteiraacoesbackend.facades;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.carteiraacoesbackend.clients.BrasilApiClient;
import com.carteiraacoesbackend.dto.integrations.BrasilApiCepResponse;
import com.carteiraacoesbackend.dto.integrations.BrasilApiCnpjResponse;
import com.carteiraacoesbackend.dto.integrations.BrasilApiCvmBrokerResponse;
import com.carteiraacoesbackend.exceptions.ApiException;
import org.junit.jupiter.api.Test;

class CvmFacadeTest {

  @Test
  void acceptsActiveBrokerWithFormattedCnpj() {
    CvmFacade facade =
        new CvmFacade(
            new FakeBrasilApiClient(
                new BrasilApiCvmBrokerResponse(
                    "12.345.678/0001-90", "12345", "EM FUNCIONAMENTO NORMAL")));

    assertEquals("12345", facade.consultarCorretora("12345678000190").codigoCvm());
  }

  @Test
  void rejectsInactiveBroker() {
    CvmFacade facade =
        new CvmFacade(
            new FakeBrasilApiClient(
                new BrasilApiCvmBrokerResponse("12345678000190", "12345", "CANCELADA")));
    ApiException exception =
        assertThrows(ApiException.class, () -> facade.consultarCorretora("12345678000190"));
    assertEquals("CORRETORA_NAO_REGISTRADA_CVM", exception.getCode());
    assertEquals(422, exception.getStatus().value());
  }

  @Test
  void rejectsBrokerMissingFromCvm() {
    CvmFacade facade = new CvmFacade(new FakeBrasilApiClient(null));

    ApiException exception =
        assertThrows(ApiException.class, () -> facade.consultarCorretora("12345678000190"));

    assertEquals("CORRETORA_NAO_REGISTRADA_CVM", exception.getCode());
    assertEquals(422, exception.getStatus().value());
  }

  @Test
  void rejectsIncompleteBrokerResponseAsExternalFailure() {
    CvmFacade facade =
        new CvmFacade(
            new FakeBrasilApiClient(
                new BrasilApiCvmBrokerResponse("12345678000190", "12345", null)));
    ApiException exception =
        assertThrows(ApiException.class, () -> facade.consultarCorretora("12345678000190"));
    assertEquals("EXTERNAL_API_UNAVAILABLE", exception.getCode());
    assertEquals(503, exception.getStatus().value());
  }

  private record FakeBrasilApiClient(BrasilApiCvmBrokerResponse response)
      implements BrasilApiClient {
    @Override
    public BrasilApiCnpjResponse consultarCnpj(String cnpj) {
      throw new UnsupportedOperationException();
    }

    @Override
    public BrasilApiCepResponse consultarCep(String cep) {
      throw new UnsupportedOperationException();
    }

    @Override
    public java.util.List<BrasilApiCvmBrokerResponse> listarCorretorasCvm() {
      throw new UnsupportedOperationException();
    }

    @Override
    public BrasilApiCvmBrokerResponse consultarCorretoraCvm(String cnpj) {
      if (response == null)
        throw ApiException.notFound(
            "EXTERNAL_RESOURCE_NOT_FOUND", "Recurso não encontrado no provedor externo.");
      return response;
    }
  }
}
