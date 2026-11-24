package com.carteiraacoesbackend.facades;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.carteiraacoesbackend.clients.BrasilApiClient;
import com.carteiraacoesbackend.clients.CnpjWsClient;
import com.carteiraacoesbackend.dto.integrations.BrasilApiCepResponse;
import com.carteiraacoesbackend.dto.integrations.BrasilApiCnpjResponse;
import com.carteiraacoesbackend.dto.integrations.BrasilApiCvmBrokerResponse;
import com.carteiraacoesbackend.dto.integrations.CnpjWsResponse;
import com.carteiraacoesbackend.exceptions.ApiException;
import com.carteiraacoesbackend.integrations.ExternalCallLogger;
import java.util.List;
import org.junit.jupiter.api.Test;

class CnpjFacadeTest {

  @Test
  void usesFallbackWhenBrasilApiIsUnavailable() {
    CnpjWsClient fallback =
        cnpj ->
            new CnpjWsResponse(
                "Empresa Fallback",
                new CnpjWsResponse.Estabelecimento(
                    "Fallback",
                    "contato@empresa.com",
                    "11999999999",
                    "ATIVA",
                    "01001000",
                    "Rua",
                    "1",
                    null,
                    "Centro",
                    new CnpjWsResponse.Cidade("São Paulo"),
                    new CnpjWsResponse.Estado("SP")));
    CnpjFacade facade =
        new CnpjFacade(
            new FakeBrasilApiClient() {
              @Override
              public BrasilApiCnpjResponse consultarCnpj(String cnpj) {
                throw ApiException.external(
                    org.springframework.http.HttpStatus.SERVICE_UNAVAILABLE,
                    "EXTERNAL_API_UNAVAILABLE",
                    "Indisponível");
              }
            },
            fallback,
            new ExternalCallLogger());

    BrasilApiCnpjResponse response = facade.consultar("12345678000190");

    assertEquals("Empresa Fallback", response.razaoSocial());
    assertEquals("São Paulo", response.cidade());
  }

  @Test
  void normalizesCnpjBeforeCallingClient() {
    CnpjFacade facade = new CnpjFacade(new FakeBrasilApiClient());
    assertEquals("Empresa", facade.consultar("12.345.678/0001-90").razaoSocial());
  }

  @Test
  void classifiesClientTimeoutAsUnavailable() {
    CnpjFacade facade =
        new CnpjFacade(
            new FakeBrasilApiClient() {
              @Override
              public BrasilApiCnpjResponse consultarCnpj(String cnpj) {
                throw new RuntimeException("timeout");
              }
            });
    ApiException error = assertThrows(ApiException.class, () -> facade.consultar("12345678000190"));
    assertEquals("EXTERNAL_API_UNAVAILABLE", error.getCode());
    assertEquals(503, error.getStatus().value());
  }

  @Test
  void classifiesEmptyResponseAsUnavailable() {
    CnpjFacade facade =
        new CnpjFacade(
            new FakeBrasilApiClient() {
              @Override
              public BrasilApiCnpjResponse consultarCnpj(String cnpj) {
                return null;
              }
            });

    ApiException error = assertThrows(ApiException.class, () -> facade.consultar("12345678000190"));

    assertEquals("EXTERNAL_API_UNAVAILABLE", error.getCode());
    assertEquals(503, error.getStatus().value());
  }

  @Test
  void rejectsInvalidCnpjBeforeCallingExternalApi() {
    ApiException error =
        assertThrows(
            ApiException.class, () -> new CnpjFacade(new FakeBrasilApiClient()).consultar("123"));

    assertEquals("CNPJ_INVALIDO", error.getCode());
    assertEquals(422, error.getStatus().value());
  }

  @Test
  void usesFallbackForExternalApiErrorAndMapsNullableAddressParts() {
    CnpjWsClient fallback = mock(CnpjWsClient.class);
    when(fallback.consultarCnpj("12345678000190"))
        .thenReturn(
            new CnpjWsResponse(
                "Razão",
                new CnpjWsResponse.Estabelecimento(
                    "Fantasia", null, null, "ATIVA", null, null, null, null, null, null, null)));
    BrasilApiClient primary = mock(BrasilApiClient.class);
    when(primary.consultarCnpj("12345678000190"))
        .thenThrow(
            ApiException.external(
                org.springframework.http.HttpStatus.BAD_GATEWAY, "EXTERNAL_API_ERROR", "falha"));

    BrasilApiCnpjResponse response =
        new CnpjFacade(primary, fallback, new ExternalCallLogger()).consultar("12345678000190");

    assertEquals("Razão", response.razaoSocial());
    assertEquals("Fantasia", response.nomeFantasia());
    assertEquals(null, response.cidade());
    assertEquals(null, response.uf());
  }

  @Test
  void doesNotUseFallbackForNonExternalApiErrors() {
    BrasilApiClient primary = mock(BrasilApiClient.class);
    when(primary.consultarCnpj("12345678000190"))
        .thenThrow(
            ApiException.external(
                org.springframework.http.HttpStatus.BAD_REQUEST, "VALIDATION_ERROR", "falha"));
    CnpjWsClient fallback = mock(CnpjWsClient.class);

    ApiException error =
        assertThrows(
            ApiException.class,
            () ->
                new CnpjFacade(primary, fallback, new ExternalCallLogger())
                    .consultar("12345678000190"));

    assertEquals("VALIDATION_ERROR", error.getCode());
  }

  @Test
  void convertsFallbackFailureOrEmptyFallbackToUnavailable() {
    BrasilApiClient primary = mock(BrasilApiClient.class);
    when(primary.consultarCnpj("12345678000190")).thenReturn(null);
    CnpjWsClient fallback = mock(CnpjWsClient.class);
    when(fallback.consultarCnpj("12345678000190")).thenReturn(null);

    ApiException empty =
        assertThrows(
            ApiException.class,
            () ->
                new CnpjFacade(primary, fallback, new ExternalCallLogger())
                    .consultar("12345678000190"));
    assertEquals("EXTERNAL_API_UNAVAILABLE", empty.getCode());

    when(fallback.consultarCnpj("12345678000190")).thenThrow(new RuntimeException("timeout"));
    ApiException failure =
        assertThrows(
            ApiException.class,
            () ->
                new CnpjFacade(primary, fallback, new ExternalCallLogger())
                    .consultar("12345678000190"));
    assertEquals("EXTERNAL_API_UNAVAILABLE", failure.getCode());
  }

  private static class FakeBrasilApiClient implements BrasilApiClient {
    @Override
    public BrasilApiCnpjResponse consultarCnpj(String cnpj) {
      if (!"12345678000190".equals(cnpj)) throw new AssertionError("CNPJ não normalizado");
      return new BrasilApiCnpjResponse(
          "Empresa",
          "Empresa",
          "email@test.com",
          "11999999999",
          "ATIVA",
          "01001000",
          "Rua",
          "100",
          null,
          "Centro",
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
      throw new UnsupportedOperationException();
    }
  }
}
