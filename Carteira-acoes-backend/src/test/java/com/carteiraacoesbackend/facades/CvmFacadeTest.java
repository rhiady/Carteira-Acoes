package com.carteiraacoesbackend.facades;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.carteiraacoesbackend.clients.BrasilApiClient;
import com.carteiraacoesbackend.dto.integrations.BrasilApiCepResponse;
import com.carteiraacoesbackend.dto.integrations.BrasilApiCnpjResponse;
import com.carteiraacoesbackend.dto.integrations.BrasilApiCvmBrokerResponse;
import com.carteiraacoesbackend.exceptions.ApiException;
import org.junit.jupiter.api.Test;

class CvmFacadeTest {

  @Test
  void usesOfficialCvmFallbackWhenBrasilApiIsUnavailable() {
    CvmDataFacade fallback = mock(CvmDataFacade.class);
    when(fallback.consultarCorretora("12345678000190"))
        .thenReturn(
            new BrasilApiCvmBrokerResponse("12345678000190", "12345", "EM FUNCIONAMENTO NORMAL"));
    CvmFacade facade =
        new CvmFacade(
            new BrasilApiClient() {
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
                throw ApiException.external(
                    org.springframework.http.HttpStatus.SERVICE_UNAVAILABLE,
                    "EXTERNAL_API_UNAVAILABLE",
                    "Indisponível");
              }
            },
            fallback,
            new com.carteiraacoesbackend.integrations.ExternalCallLogger());

    assertEquals("12345", facade.consultarCorretora("12345678000190").codigoCvm());
  }

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

  @Test
  void rejectsInvalidCnpjBeforeCallingProvider() {
    CvmFacade facade = new CvmFacade(new FakeBrasilApiClient(null));

    ApiException exception =
        assertThrows(ApiException.class, () -> facade.consultarCorretora("abc"));

    assertEquals("CNPJ_INVALIDO", exception.getCode());
    assertEquals(422, exception.getStatus().value());
  }

  @Test
  void acceptsAlternativeActiveStatusAndFormattedResponseCnpj() {
    CvmFacade facade =
        new CvmFacade(
            new FakeBrasilApiClient(
                new BrasilApiCvmBrokerResponse(
                    "12.345.678/0001-90", "12345", " em funcionamento normal ")));

    assertEquals("12345", facade.consultarCorretora("12345678000190").codigoCvm());
  }

  @Test
  void rejectsMismatchedOrBlankStatusResponses() {
    CvmFacade mismatched =
        new CvmFacade(
            new FakeBrasilApiClient(
                new BrasilApiCvmBrokerResponse("99999999000199", "12345", "ATIVA")));
    ApiException mismatch =
        assertThrows(ApiException.class, () -> mismatched.consultarCorretora("12345678000190"));
    assertEquals("EXTERNAL_API_UNAVAILABLE", mismatch.getCode());

    CvmFacade blankStatus =
        new CvmFacade(
            new FakeBrasilApiClient(
                new BrasilApiCvmBrokerResponse("12345678000190", "12345", " ")));
    ApiException blank =
        assertThrows(ApiException.class, () -> blankStatus.consultarCorretora("12345678000190"));
    assertEquals("EXTERNAL_API_UNAVAILABLE", blank.getCode());
  }

  @Test
  void fallsBackForUnexpectedProviderErrorsAndKeepsClientErrorsWithoutFallback() {
    CvmDataFacade fallback = mock(CvmDataFacade.class);
    when(fallback.consultarCorretora("12345678000190"))
        .thenReturn(new BrasilApiCvmBrokerResponse("12345678000190", "12345", "ATIVA"));
    BrasilApiClient unavailable = mock(BrasilApiClient.class);
    when(unavailable.consultarCorretoraCvm("12345678000190"))
        .thenThrow(new RuntimeException("timeout"));
    CvmFacade withFallback =
        new CvmFacade(
            unavailable, fallback, new com.carteiraacoesbackend.integrations.ExternalCallLogger());
    assertEquals("12345", withFallback.consultarCorretora("12345678000190").codigoCvm());

    BrasilApiClient unauthorized = mock(BrasilApiClient.class);
    when(unauthorized.consultarCorretoraCvm("12345678000190"))
        .thenThrow(
            ApiException.external(
                org.springframework.http.HttpStatus.BAD_REQUEST, "VALIDATION_ERROR", "inválido"));
    ApiException error =
        assertThrows(
            ApiException.class,
            () -> new CvmFacade(unauthorized).consultarCorretora("12345678000190"));
    assertEquals("VALIDATION_ERROR", error.getCode());
  }

  @Test
  void usesFallbackForExplicitExternalApiErrorCodeAndRejectsInvalidFallbackData() {
    CvmDataFacade fallback = mock(CvmDataFacade.class);
    BrasilApiClient primary = mock(BrasilApiClient.class);
    when(primary.consultarCorretoraCvm("12345678000190"))
        .thenThrow(
            ApiException.external(
                org.springframework.http.HttpStatus.BAD_REQUEST, "EXTERNAL_API_ERROR", "falha"));
    when(fallback.consultarCorretora("12345678000190"))
        .thenReturn(new BrasilApiCvmBrokerResponse("12345678000190", "12345", "ATIVA"));
    assertEquals(
        "12345",
        new CvmFacade(
                primary, fallback, new com.carteiraacoesbackend.integrations.ExternalCallLogger())
            .consultarCorretora("12345678000190")
            .codigoCvm());

    when(fallback.consultarCorretora("12345678000190")).thenReturn(null);
    ApiException nullFallback =
        assertThrows(
            ApiException.class,
            () ->
                new CvmFacade(
                        primary,
                        fallback,
                        new com.carteiraacoesbackend.integrations.ExternalCallLogger())
                    .consultarCorretora("12345678000190"));
    assertEquals("EXTERNAL_API_UNAVAILABLE", nullFallback.getCode());

    when(fallback.consultarCorretora("12345678000190"))
        .thenReturn(new BrasilApiCvmBrokerResponse("12345678000190", "12345", "CANCELADA"));
    ApiException inactiveFallback =
        assertThrows(
            ApiException.class,
            () ->
                new CvmFacade(
                        primary,
                        fallback,
                        new com.carteiraacoesbackend.integrations.ExternalCallLogger())
                    .consultarCorretora("12345678000190"));
    assertEquals("CORRETORA_NAO_REGISTRADA_CVM", inactiveFallback.getCode());
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
