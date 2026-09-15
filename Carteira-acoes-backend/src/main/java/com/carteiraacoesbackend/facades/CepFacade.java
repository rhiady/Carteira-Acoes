package com.carteiraacoesbackend.facades;

import com.carteiraacoesbackend.clients.BrasilApiClient;
import com.carteiraacoesbackend.dto.integrations.BrasilApiCepResponse;
import com.carteiraacoesbackend.exceptions.ApiException;
import com.carteiraacoesbackend.integrations.ExternalCallLogger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CepFacade {
  private final BrasilApiClient client;
  private final ExternalCallLogger externalCallLogger;

  public CepFacade(BrasilApiClient client) {
    this(client, new ExternalCallLogger());
  }

  @Autowired
  public CepFacade(BrasilApiClient client, ExternalCallLogger externalCallLogger) {
    this.client = client;
    this.externalCallLogger = externalCallLogger;
  }

  public BrasilApiCepResponse consultar(String cep) {
    String normalizado = somenteDigitos(cep);
    if (normalizado.length() != 8)
      throw ApiException.unprocessable("CEP_INVALIDO", "CEP deve conter 8 dígitos.");
    try {
      BrasilApiCepResponse response =
          externalCallLogger.execute(
              "brasil-api", "cep", normalizado, () -> client.consultarCep(normalizado));
      if (response == null) {
        throw ApiException.external(
            org.springframework.http.HttpStatus.SERVICE_UNAVAILABLE,
            "EXTERNAL_API_UNAVAILABLE",
            "Não foi possível consultar o CEP.");
      }
      return response;
    } catch (ApiException exception) {
      throw exception;
    } catch (Exception e) {
      throw ApiException.external(
          org.springframework.http.HttpStatus.SERVICE_UNAVAILABLE,
          "EXTERNAL_API_UNAVAILABLE",
          "Não foi possível consultar o CEP.");
    }
  }

  private String somenteDigitos(String valor) {
    return valor == null ? "" : valor.replaceAll("\\D", "");
  }
}
